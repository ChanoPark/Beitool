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
    public void workCommute(String workType, String accessToken) {
        System.out.println("***workType:" + workType + " accessToken:" + accessToken);
        try {
            Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(accessToken);
            Member member = memberRepository.findOne(memberId);
            LocalDateTime currentTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

            if(workType.equals("onWork")) { //출근이면 근로정보 생성
                System.out.println("***출근");
                
                //근로정보 생성(출근)
                WorkInfo workInfo = new WorkInfo(member, member.getActiveStore(),currentTime);
                belongWorkInfoRepository.createWorkInfo(workInfo);

            } else { //퇴근이면 근로정보 조회 후 퇴근정보 추가
                System.out.println("***퇴근");
                Store store = storeRepository.findOne(member.getActiveStore().getId()); //현재 일하는 사업장

                //퇴근 정보 업데이트
                belongWorkInfoRepository.findWorkInfo(member, store, currentTime);
            }
        } catch(JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
