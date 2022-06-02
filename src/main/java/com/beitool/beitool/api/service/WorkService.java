package com.beitool.beitool.api.service;

import com.beitool.beitool.api.dto.*;
import com.beitool.beitool.api.dto.SalaryCalPresidentResponseDto.*;
import com.beitool.beitool.api.dto.SalaryCalEmployeeResponseDto.*;
import com.beitool.beitool.api.dto.ScheduleReadResponseDto.WorkInfoResponse;
import com.beitool.beitool.api.repository.BelongWorkInfoRepository;
import com.beitool.beitool.api.repository.MemberRepository;
import com.beitool.beitool.api.repository.StoreRepository;
import com.beitool.beitool.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 근로와 관련된 서비스
 * 1.출퇴근
 * 2.캘린더 일정 작성
 * 3.캘린더 일정 작성시 유효성 검사
 * 3.캘린더 일정 한달 조회
 * 4.캘린더 일정 하루 조회
 * 5.캘린더 일정 삭제
 * 6.캘린더 일정 수정
 * 7.급여 계산기(사장)
 * 8.급여 계산기(직원)
 * 9.주휴 수당 계산
 * 10.4대보험 계산
 * 
 * @author Chanos
 * @since 2022-05-27
 */
@Service
@RequiredArgsConstructor
@Transactional
public class WorkService {
    private final MemberKakaoApiService memberKakaoApiService;
    private final MemberRepository memberRepository;
    private final BelongWorkInfoRepository belongWorkInfoRepository;
    private final StoreRepository storeRepository;

    /*1.출퇴근*/
    public String workCommute(String workType, String accessToken) {
        System.out.println("***workType:" + workType + " accessToken:" + accessToken);
        String result = "Failed";

        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(accessToken);
        Member member = memberRepository.findOne(memberId);
        Store store = storeRepository.findOne(member.getActiveStore().getId()); //현재 일하는 사업장
        LocalDateTime currentTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        LocalDate today = LocalDate.now();

        //출근이면 근로정보 생성
        if (workType.equals("onWork")) {
            System.out.println("***출근");

            //퇴근하지 않고 출근 버튼을 누르면 출근 불가능
            if (belongWorkInfoRepository.findWorkInfo(member, store) == 0) {
                //근로정보 생성(출근)
                WorkInfo workInfo = new WorkInfo(member, member.getActiveStore(), currentTime, today);
                belongWorkInfoRepository.createWorkInfo(workInfo);
                result = "Success";
            }//퇴근이면 근로정보 조회 후 퇴근 정보 업데이트
        } else {
            System.out.println("***퇴근");

            //출근하지 않고 퇴근 버튼을 누르면 퇴근 불가능
            if (belongWorkInfoRepository.findWorkInfo(member, store) > 0) {
                //퇴근 정보 업데이트
                belongWorkInfoRepository.updateOffWork(member, store, currentTime);
                result = "Success";
            }
        }
        System.out.println("***출퇴근 완료");
        return result;
    }

    /*2.캘린더 일정 작성*/
    public ResponseEntity createSchedule(Member member, ScheduleCreateRequestDto scheduleCreateRequestDto) {
        Store store = member.getActiveStore();
        Long employeeId = scheduleCreateRequestDto.getEmployee();
        LocalDate workDay = scheduleCreateRequestDto.getWorkDay();
        LocalDateTime workStartTime = scheduleCreateRequestDto.getWorkStartTime();
        LocalDateTime workEndTime = scheduleCreateRequestDto.getWorkEndTime();

        Member employee = memberRepository.findOne(employeeId);

        if (validateSchedule(workDay, workStartTime, workEndTime)) {
            WorkSchedule workSchedule = new WorkSchedule(employee, member, store, workDay, workStartTime, workEndTime);
            belongWorkInfoRepository.createSchedule(workSchedule);
            return new ResponseEntity("Success", HttpStatus.CREATED);
        } else {
            return new ResponseEntity("Failed", HttpStatus.BAD_REQUEST);
        }
    }

    /*3.캘린더 일정 작성시 Validation*/
    public boolean validateSchedule(LocalDate workDay, LocalDateTime workStartTime, LocalDateTime workEndTime) {
        //퇴근 시간이 출근 시간보다 이르면 불가능
        if (workStartTime.isAfter(workEndTime))
            return false;

        try {
            List<WorkSchedule> workSchedules = belongWorkInfoRepository.getWorkSchedule(workDay);//해당 날짜 근무 조회

            for (WorkSchedule workSchedule : workSchedules) {

                //1.기존 근무와 양 끝이 겹치는 경우
                boolean duplicateWorkStartTime = workStartTime.isBefore(workSchedule.getWorkStartTime())
                        && workEndTime.isAfter(workSchedule.getWorkStartTime());

                boolean duplicateWorkEndTime = workStartTime.isBefore(workSchedule.getWorkEndTime())
                        && workEndTime.isAfter(workSchedule.getWorkEndTime());

                //2.기존 근무에 포함되거나, 기존 근무를 포함하는 경우.
                boolean includedWorkTime = workStartTime.isAfter(workSchedule.getWorkStartTime())
                        && workEndTime.isBefore(workSchedule.getWorkEndTime());

                boolean includingWorkTime = workStartTime.isBefore(workSchedule.getWorkStartTime())
                        && workEndTime.isAfter(workSchedule.getWorkEndTime());

                //3.기존 근무의 시작과 끝 시간이 겹치는 경우
                boolean sameStartTime = workStartTime.isEqual(workSchedule.getWorkStartTime())
                        && (workEndTime.isBefore(workSchedule.getWorkEndTime())
                        || workEndTime.isEqual(workSchedule.getWorkEndTime()));

                boolean sameEndTime = workEndTime.isEqual(workSchedule.getWorkEndTime())
                        && (workStartTime.isAfter(workSchedule.getWorkStartTime())
                        || workStartTime.isEqual(workSchedule.getWorkStartTime()));

                if (duplicateWorkStartTime || duplicateWorkEndTime || includedWorkTime || includingWorkTime
                        || sameStartTime || sameEndTime)
                    return false;
                else
                    continue;
            }
        } catch (NoResultException e) {
            return true;
        }
        return true;
    }

    /*4.캘린더 일정 한달 조회*/
    public ScheduleReadResponseDto readScheduleMonthly(Member member, LocalDateTime firstDateTime, LocalDateTime lastDateTime) {
        ScheduleReadResponseDto scheduleReadResponseDto = new ScheduleReadResponseDto();
        Store store = member.getActiveStore();
        LocalDateTime today = LocalDateTime.now();

        //1일 ~ 오늘 : 근무 기록
        List<WorkInfo> workInfos = belongWorkInfoRepository.findWorkHistoryPeriod(member, store, firstDateTime, today);
        for (WorkInfo workInfo : workInfos) {
            String name = belongWorkInfoRepository.findBelongInfo(workInfo.getMember(), store).getName();
            Long postId = workInfo.getId();
            WorkInfoResponse workInfoResponse = new WorkInfoResponse(postId, name, workInfo.getWorkStartTime(), workInfo.getWorkEndTime(), workInfo.getWorkDay());
            scheduleReadResponseDto.setWorkInfos(workInfoResponse);
        }

        //오늘 ~ 말일 : 근무 예정
        List<WorkSchedule> workSchedules = belongWorkInfoRepository.findWorkFuturePeriod(member, store, today, lastDateTime);
        for (WorkSchedule workSchedule : workSchedules) {
            String name = belongWorkInfoRepository.findBelongInfo(workSchedule.getEmployee(), store).getName();
            String author = belongWorkInfoRepository.findBelongInfo(workSchedule.getAuthor(), store).getName();
            Long postId = workSchedule.getId();
            WorkInfoResponse workInfoResponse = new WorkInfoResponse(postId, name, author, workSchedule.getWorkStartTime(), workSchedule.getWorkEndTime(), workSchedule.getWorkDay());
            scheduleReadResponseDto.setWorkInfos(workInfoResponse);
        }

        return scheduleReadResponseDto;
    }
    /*5.캘린더 일정 하루 조회*/
    public ScheduleReadResponseDto readSchedule(Member member, LocalDate day) {
        ScheduleReadResponseDto scheduleReadResponseDto = new ScheduleReadResponseDto();
        Store store = member.getActiveStore();
        LocalDate today = LocalDate.now();

        //오늘 이전의 날짜를 선택하면 실제 근무 기록을 반환함.
        if (day.isBefore(today) || day.isEqual(today)) {
            List<WorkInfo> workInfos = belongWorkInfoRepository.readScheduleHistory(store, day);
            for (WorkInfo workInfo : workInfos) {
                String name = belongWorkInfoRepository.findBelongInfo(workInfo.getMember(), store).getName();
                Long postId = workInfo.getId();
                WorkInfoResponse workInfoResponse = new WorkInfoResponse(postId, name, workInfo.getWorkStartTime(), workInfo.getWorkEndTime(), workInfo.getWorkDay());
                scheduleReadResponseDto.setWorkInfos(workInfoResponse);
            }
        } else { //오늘 포함 이후의 날짜를 선택하면 배정되어 있는 근무를 반환함.
            List<WorkSchedule> workSchedules = belongWorkInfoRepository.readScheduleFuture(store, day);
            for (WorkSchedule workSchedule : workSchedules) {
                String employeeName = belongWorkInfoRepository.findBelongInfo(workSchedule.getEmployee(), store).getName();
                String authorName = belongWorkInfoRepository.findBelongInfo(workSchedule.getAuthor(), store).getName();
                Long postId = workSchedule.getId();
                WorkInfoResponse workInfoResponse = new WorkInfoResponse(postId, employeeName, authorName, workSchedule.getWorkStartTime(), workSchedule.getWorkEndTime(), workSchedule.getWorkDay());
                scheduleReadResponseDto.setWorkInfos(workInfoResponse);
            }
        }
        return scheduleReadResponseDto;
    }

    /*6.캘린더 일정 삭제*/
    public ResponseEntity deleteSchedule(Member member, Long postId) {
        Store store = member.getActiveStore();
        Belong belongInfo = belongWorkInfoRepository.findBelongInfo(member, store);

        Member employee = belongWorkInfoRepository.findEmployee(postId).getAuthor();
        //작성자 혹은 사장만 삭제 가능
        if (belongInfo.getPosition().equals(MemberPosition.President) || member.equals(employee)) {
            belongWorkInfoRepository.deleteSchedule(postId);
            return new ResponseEntity("Delete Success", HttpStatus.ACCEPTED);
        }
        return new ResponseEntity("Delete Failed", HttpStatus.BAD_REQUEST);
    }

    /*7.캘린더 일정 수정*/
    @Transactional
    public ResponseEntity updateSchedule(Member author, ScheduleUpdateRequestDto scheduleUpdateRequestDto) {
        Long postId = scheduleUpdateRequestDto.getId();
        WorkSchedule schedule = belongWorkInfoRepository.findSchedule(postId);

        Long employeeId = scheduleUpdateRequestDto.getEmployee();

        Member employee = memberRepository.findOne(employeeId);
        LocalDate workDay = scheduleUpdateRequestDto.getWorkDay();
        LocalDateTime workStartTime = scheduleUpdateRequestDto.getWorkStartTime();
        LocalDateTime workEndTime = scheduleUpdateRequestDto.getWorkEndTime();

        if (validateSchedule(workDay, workStartTime, workEndTime)) {
            schedule.updateWorkSchedule(employee, author, workDay, workStartTime, workEndTime);
            return new ResponseEntity("Success", HttpStatus.CREATED);
        } else {
            return new ResponseEntity("Failed", HttpStatus.BAD_REQUEST);
        }
    }

    /*8.급여 계산기(사장)*/   //추후 리팩토링을 통해 급여 계산하는 메소드를 따로 빼서 코드의 중복성을 줄이자.
    public SalaryCalPresidentResponseDto calculateSalaryForPresident(Member member, LocalDateTime firstDateTime, LocalDateTime lastDateTime) {
        SalaryCalPresidentResponseDto responseDto = new SalaryCalPresidentResponseDto();

        // 해당 가게 조회
        Store store = member.getActiveStore();

        //회원 목록 조회
        List<Belong> employeeList = belongWorkInfoRepository.getBelongEmployeeList(store);

        //각 회원당 근무 기록 조회
        int totalSalary = 0; //모든 직원의 급여 합계
        int totalWorkingHour = 0; //모든 직원의 근로 시간(시간) 합계
        int totalWorkingMin = 0; //모든 직원의 근로 시간(분) 합계
        int totalSalaryPerEmployee; //직원 별 급여 합계
        int workingHour; // 근로 시간(시간)
        int workingMin; // 근로 시간(분)
        int workingTime; // 계산용 총 근로 시간(분 단위 계산)
        int holidayPay; //주휴수당
        int totalHolidayPay = 0; //주휴수당 합계
        int totalInsurance = 0; //4대보험 합계
        Map<String, Integer> insurance; //4대보험

        //각 직원 별 급여 계산
        for (Belong employee : employeeList) {
            Integer salaryHour = employee.getSalaryHour(); //해당 가게에서 받는 시급 가져오기.
            workingTime = 0;
            //각 직원의 모든 근무 기록 조회
            List<WorkInfo> workingTimes = belongWorkInfoRepository.findWorkHistoryPeriod(employee.getMember(), store, firstDateTime, lastDateTime);

            //각 직원의 근로 시간 합계 계산
            for (WorkInfo workInfo : workingTimes) {
                workingTime += ChronoUnit.MINUTES.between(workInfo.getWorkStartTime(), workInfo.getWorkEndTime());
            }

            //근로 시간 합계, 급여 합계 구하기 -> 아직 이번 달에 일을 안했을 경우, 0을 반환해야 한다.
            workingHour = workingTime / 60; //근로 시간(시간)
            workingMin = workingTime % 60; //근로 시간(분)
            totalSalaryPerEmployee = (int) ((((double) workingTime / 60.0) * salaryHour)); //최종 급여 (분 단위 계산)

            //결과 추가
            totalSalary += totalSalaryPerEmployee;
            totalWorkingHour += workingHour;
            totalWorkingMin += workingMin;

            //주휴수당 계산
            holidayPay = calculateHolidayPay(employee, firstDateTime, lastDateTime);
            totalHolidayPay += holidayPay;

            //4대보험 계산
            if ((workingTime/60) >= 60) {
                insurance = calculateInsurance(totalSalary);
                for (Map.Entry<String, Integer> pay : insurance.entrySet()) {
                    totalInsurance += pay.getValue();
                }
            }

            SalaryInfo salaryInfo = new SalaryInfo(employee.getName(), totalSalaryPerEmployee, workingHour, workingMin, holidayPay);
            responseDto.addInfo(salaryInfo);
        }
        //총 고용 시간의 분(Min) 부분이 60 이상일 경우, 시간으로 변환
        if (totalWorkingMin >= 60) {
            totalWorkingHour += totalWorkingMin / 60;
            totalWorkingMin = totalWorkingMin % 60;
        }

        responseDto.setTotalInfo(totalSalary, totalWorkingHour, totalWorkingMin,
                                    totalHolidayPay, employeeList.size(), totalInsurance);
        return responseDto;
    }

    /*9. 급여 계산기(직원)*/
    public SalaryCalEmployeeResponseDto calculateSalaryForEmployee(Member member, LocalDateTime firstDateTime,
                                                                   LocalDateTime lastDateTime) {
        SalaryCalEmployeeResponseDto responseDto = new SalaryCalEmployeeResponseDto();
        Store store = member.getActiveStore();

        //직원 정보 조회
        Belong employee = belongWorkInfoRepository.findBelongInfo(member, store);

        //각 직원의 모든 근무 기록 조회
        List<WorkInfo> workingTimes = belongWorkInfoRepository.findWorkHistoryPeriod(employee.getMember(), store, firstDateTime, lastDateTime);

        //각 직원의 근로 시간 합계
        int salaryHour = employee.getSalaryHour();
        int workingHour; //근로 시간(시간)
        int workingMin;  //근로 시간(분)
        int workingTime = 0; //근로 시간 합계
        int totalSalary; // 총 급여
        int holidayPay; //주휴수당
        Map<String, Integer> insurance = new HashMap<>(); //4대보험

        for (WorkInfo workInfo : workingTimes) {
            workingTime += ChronoUnit.MINUTES.between(workInfo.getWorkStartTime(), workInfo.getWorkEndTime()); //분 단위 계산
            responseDto.addWorkingHistory(new WorkingHistory(workInfo.getWorkStartTime(), workInfo.getWorkEndTime()));
        }

        //근로 시간 합계, 급여 합계 구하기 -> 아직 이번 달에 일을 안했을 경우, 0을 반환해야 한다.
        workingHour = workingTime / 60; //근로 시간(시간)
        workingMin = workingTime % 60; //근로 시간(분)
        totalSalary = (int) ((((double) workingTime / 60.0) * salaryHour)); //최종 급여 (분 단위 계산)

        //주휴수당 계산
        holidayPay = calculateHolidayPay(employee, firstDateTime, lastDateTime);

        //4대보험 계산
        if (workingHour >= 60) {
            insurance = calculateInsurance(totalSalary);
        } else {
            insurance.put("pension", 0);
            insurance.put("healthInsurance", 0);
            insurance.put("longTermCareInsurance", 0);
            insurance.put("unemploymentPay", 0);
        }

        responseDto.setInfo(totalSalary, workingHour, workingMin, salaryHour, holidayPay, insurance);

        return responseDto;
    }

    /*10.주휴수당 계산*/
    public int calculateHolidayPay(Belong employee, LocalDateTime firstDateTime, LocalDateTime lastDateTime) {
        int workingTimeInWeek = 0; //주 근로 시간
        int holidayPay = 0;

        LocalDateTime startWeek = firstDateTime;
        LocalDateTime endWeek = startWeek.plusWeeks(1).plusDays(1); //plusDays(1)을 해서 7일차까지 포함되게 함.

        //일주일 근무 시간 및 주휴수당 계산 -> 월급, 주급 분리해서 볼 때 활용하면 코드 중복성을 낮출 수 있겠음.
        while (endWeek.isBefore(lastDateTime)) {
            List<WorkInfo> workingTimes = belongWorkInfoRepository.findWorkHistoryPeriod(employee.getMember(), employee.getStore(), startWeek, endWeek);

            for (WorkInfo workInfo : workingTimes) {
                workingTimeInWeek += ChronoUnit.MINUTES.between(workInfo.getWorkStartTime(), workInfo.getWorkEndTime());
            }
            //주 근로 시간이 15시간 이상인 경우, 주휴수당 지급
            if ((workingTimeInWeek / 60) >= 15) {
                holidayPay += ((workingTimeInWeek / 60.0) / workingTimes.size()) * employee.getSalaryHour();
            }
            workingTimeInWeek = 0;
            startWeek = endWeek;
            endWeek = endWeek.plusWeeks(1);
        }

        startWeek = endWeek;
        endWeek = lastDateTime.plusDays(1); //1일 00시까지 근무하는 것을 확인해야 하기 때문.
        List<WorkInfo> workingTimes = belongWorkInfoRepository.findWorkHistoryPeriod(employee.getMember(), employee.getStore(), startWeek, endWeek);

        for (WorkInfo workInfo : workingTimes) {
            workingTimeInWeek += ChronoUnit.MINUTES.between(workInfo.getWorkStartTime(), workInfo.getWorkEndTime());
        }
        //주 근로 시간이 15시간 이상인 경우, 주휴수당 지급
        if ((workingTimeInWeek / 60) >= 15) {
            holidayPay += ((workingTimeInWeek / 60.0) / workingTimes.size()) * employee.getSalaryHour();
        }

        return holidayPay;
    }

    /*11.4대 보험 계산 */ //산재 보험 제외
    public Map<String, Integer> calculateInsurance(int salary) {
        Map<String, Integer> insurance = new HashMap<>();

        double ratio;

        //1.국민연금 계산
        ratio = 0.045;
        int pension = (int) (salary * ratio); //월 소득액의 4.5%

        //2.건강보험료
        ratio = 0.0699;
        int healthInsurance = (int) (salary * ratio); //월 소득액 6.99% (근로자가 부담해야 되는 금액)

        //3.장기요양보험료
        ratio = 0.1227;
        int longTermCareInsurance = (int) (healthInsurance * ratio); //건보료의 12.27%

        //4.실업 급여
        ratio = 0.008;
        int unemploymentPay = (int) (salary * ratio); //월 소득액 0.8%

        insurance.put("pension", pension);
        insurance.put("healthInsurance", healthInsurance);
        insurance.put("longTermCareInsurance", longTermCareInsurance);
        insurance.put("unemploymentPay", unemploymentPay);

        return insurance;
    }
}
