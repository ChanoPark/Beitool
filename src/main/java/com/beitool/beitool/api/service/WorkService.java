package com.beitool.beitool.api.service;

import com.beitool.beitool.api.repository.BelongWorkInfoRepository;
import com.beitool.beitool.api.repository.MemberRepository;
import com.beitool.beitool.api.repository.StoreRepository;
import com.beitool.beitool.domain.Member;
import com.beitool.beitool.domain.Store;
import com.beitool.beitool.domain.WorkInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 근로와 관련된 서비스
 * 1.출퇴근
 * 
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

    /*출퇴근*/
    public String workCommute(String workType, String accessToken) {
        System.out.println("***workType:" + workType + " accessToken:" + accessToken);
        String result = "Failed";

        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(accessToken);
        Member member = memberRepository.findOne(memberId);
        Store store = storeRepository.findOne(member.getActiveStore().getId()); //현재 일하는 사업장
        LocalDateTime currentTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        //출근이면 근로정보 생성
        if(workType.equals("onWork")) {
            System.out.println("***출근");

            //퇴근하지 않고 출근 버튼을 누르면 출근 불가능
            if (belongWorkInfoRepository.findWorkInfo(member, store) == 0) {
                //근로정보 생성(출근)
                WorkInfo workInfo = new WorkInfo(member, member.getActiveStore(), currentTime);
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
}
