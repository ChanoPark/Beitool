package com.beitool.beitool.api.service;

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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

        schedule.updateWorkSchedule(employee, author, workDay, workStartTime, workEndTime);
        return new ResponseEntity("Update success", HttpStatus.ACCEPTED);
    }
}
