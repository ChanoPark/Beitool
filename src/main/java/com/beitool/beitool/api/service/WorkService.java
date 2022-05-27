package com.beitool.beitool.api.service;

import com.beitool.beitool.api.dto.SalaryCalPresidentResponseDto;
import com.beitool.beitool.api.dto.SalaryCalPresidentResponseDto.*;
import com.beitool.beitool.api.dto.ScheduleCreateRequestDto;
import com.beitool.beitool.api.dto.ScheduleReadResponseDto;
import com.beitool.beitool.api.dto.ScheduleReadResponseDto.WorkInfoResponse;
import com.beitool.beitool.api.dto.ScheduleUpdateRequestDto;
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
import java.util.List;

/**
 * 근로와 관련된 서비스
 * 1.출퇴근
 * 2.근무 시프트 작성
 * 3.근무 시프트 작성시 유효성 검사
 * 4.근무 시프트 조회
 * 5.근무 시프트 삭제
 * 6.근무 시프트 수정
 * 
 * @author Chanos
 * @since 2022-04-18
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
        if(workType.equals("onWork")) {
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
            if(belongWorkInfoRepository.findWorkInfo(member, store) > 0) {
                //퇴근 정보 업데이트
                belongWorkInfoRepository.updateOffWork(member, store, currentTime);
                result = "Success";
            }
        }
        System.out.println("***출퇴근 완료");
        return result;
    }

    /*2.근무 시프트 작성*/
    public ResponseEntity createSchedule(Member member, ScheduleCreateRequestDto scheduleCreateRequestDto) {
        Store store = member.getActiveStore();
        Long employeeId = scheduleCreateRequestDto.getEmployee();
        LocalDate workDay = scheduleCreateRequestDto.getWorkDay();
        LocalDateTime workStartTime = scheduleCreateRequestDto.getWorkStartTime();
        LocalDateTime workEndTime = scheduleCreateRequestDto.getWorkEndTime();

        Member employee = memberRepository.findOne(employeeId);

        if (validateSchedule(workDay, workStartTime, workEndTime)) {
            WorkSchedule workSchedule = new WorkSchedule(employee, member , store, workDay, workStartTime, workEndTime);
            belongWorkInfoRepository.createSchedule(workSchedule);
            return new ResponseEntity("Success", HttpStatus.CREATED);
        } else {
            return new ResponseEntity("Failed", HttpStatus.BAD_REQUEST);
        }
    }
    
    /*3.근무시프트 작성시 Validation*/
    public boolean validateSchedule(LocalDate workDay, LocalDateTime workStartTime, LocalDateTime workEndTime) {
        //퇴근 시간이 출근 시간보다 이르면 불가능
        if(workStartTime.isAfter(workEndTime))
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

    /*4.근무 시프트 조회*/
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
                WorkInfoResponse workInfoResponse = new WorkInfoResponse(postId, name, workInfo.getWorkStartTime(), workInfo.getWorkEndTime());
                scheduleReadResponseDto.setWorkInfos(workInfoResponse);
            }
        } else { //오늘 포함 이후의 날짜를 선택하면 배정되어 있는 근무를 반환함.
            List<WorkSchedule> workSchedules = belongWorkInfoRepository.readScheduleFuture(store, day);
            for (WorkSchedule workSchedule : workSchedules) {
                String employeeName = belongWorkInfoRepository.findBelongInfo(workSchedule.getEmployee(), store).getName();
                String authorName = belongWorkInfoRepository.findBelongInfo(workSchedule.getAuthor(), store).getName();
                Long postId = workSchedule.getId();
                WorkInfoResponse workInfoResponse = new WorkInfoResponse(postId, employeeName, authorName, workSchedule.getWorkStartTime(), workSchedule.getWorkEndTime());
                scheduleReadResponseDto.setWorkInfos(workInfoResponse);
            }
        }
        return scheduleReadResponseDto;
    }

    /*5.근무 시프트 삭제*/
    public ResponseEntity deleteSchedule(Member member, Long postId) {
        Store store = member.getActiveStore();
        Belong belongInfo = belongWorkInfoRepository.findBelongInfo(member, store);

        Member employee = belongWorkInfoRepository.findEmployee(postId).getAuthor();
        //작성자 혹은 사장만 삭제 가능
        if(belongInfo.getPosition().equals(MemberPosition.President) || member.equals(employee)) {
            belongWorkInfoRepository.deleteSchedule(postId);
            return new ResponseEntity("Delete Success", HttpStatus.ACCEPTED);
        }
        return new ResponseEntity("Delete Failed", HttpStatus.BAD_REQUEST);
    }

    /*6.근무 시프트 수정*/
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

    /*7.급여 계산기(사장)*/
    public SalaryCalPresidentResponseDto calculateSalaryForPresident(Member member) {
        SalaryCalPresidentResponseDto responseDto = new SalaryCalPresidentResponseDto();

        Store store = member.getActiveStore(); // 해당 가게 조회

        //이번 달의 1일, 말일 구하기
        LocalDate today = LocalDate.now(); //현재시간을 기준으로 1일, 말일 구함.
        LocalDate firstDate = today.withDayOfMonth(1); //1일
        LocalDate lastDate = today.withDayOfMonth(today.lengthOfMonth()); //말일
        LocalTime zeroTime = LocalTime.of(0,0,0); //00시 00분 00초

        LocalDateTime firstDateTime = LocalDateTime.of(firstDate, zeroTime); //1일
        LocalDateTime lastDateTime = LocalDateTime.of(lastDate,zeroTime); //말일

        //회원 목록 조회
        List<Belong> employeeList = belongWorkInfoRepository.getBelongEmployeeList(store);

        //각 회원당 근무 기록 조회
        int totalSalary = 0; //모든 직원의 급여 합계
        int totalWorkingHour = 0; //모든 직원의 근로 시간(시간) 합계
        int totalWorkingMin = 0 ; //모든 직원의 근로 시간(분) 합계
        int totalSalaryPerEmployee; //직원 별 급여 합계
        int workingHour;
        int workingMin;
        long workingTime;

        //각 직원 별 급여 계산
        for (Belong employee : employeeList) {
            Integer salaryHour = employee.getSalaryHour(); //해당 가게에서 받는 시급 가져오기.
            workingTime = 0;
            //각 직원의 모든 근무 기록 조회
            List<WorkInfo> workingTimes = belongWorkInfoRepository.findWorkHistoryAtMonth(employee.getMember(), store, firstDateTime, lastDateTime);

            //각 직원의 총 일한 시간 계산
            for (WorkInfo workInfo : workingTimes) {
                workingTime += ChronoUnit.MINUTES.between(workInfo.getWorkStartTime(), workInfo.getWorkEndTime());
            }

            //아직 이번 달에 일을 안했을 경우, 0을 반환해야 한다.
            if (workingTime==0) {
                workingHour = 0;
                workingMin = 0;
                totalSalaryPerEmployee = 0;
            } else {
                workingHour = (int) workingTime / 60; //근로 시간(시간)
                workingMin = (int) workingTime % 60; //근로 시간(분)
                totalSalaryPerEmployee = (int) ( ( ((double) workingTime / 60.0) * salaryHour) ); //최종 급여 (분 단위 계산)
            }
            //결과 추가
            totalSalary += totalSalaryPerEmployee;
            totalWorkingHour += workingHour;
            totalWorkingMin += workingMin;
            SalaryInfo salaryInfo = new SalaryInfo(employee.getName(), totalSalaryPerEmployee, workingHour, workingMin);
            responseDto.addInfo(salaryInfo);
        }
        //총 고용 시간의 분(Min) 부분이 60 이상일 경우, 시간으로 변환
        if (totalWorkingMin >= 60) {
            totalWorkingHour += totalWorkingMin/60;
            totalWorkingMin = totalWorkingMin%60;
        }
        responseDto.setTotalInfo(totalSalary, totalWorkingHour, totalWorkingMin);
        return responseDto;
    }
}
