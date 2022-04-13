package com.beitool.beitool.api.service;

import com.beitool.beitool.api.repository.MemberRepository;
import com.beitool.beitool.domain.Member;
import com.beitool.beitool.domain.MemberPosition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 2022-04-02 회원과 관련된 서비스를 제공하는 클래스
 * 1. 직급 갱신
 *
 * Implemented by Chanos
 */
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    /*직급 결정*/ //->지금은 직급 선택 방법이 바뀌었지만, 추후 직급 수정 기능을 감안해서 id와 position을 같이 받는 것으로 유지.
    /**추후 직급 수정으로 변경 예정**/
//    public String setPosition(Long id, String position) {
//        Member member = memberRepository.findOne(id);
//        String screen = "redirect:/";
//        if (position.equals("President")) {
//            member.setPosition(MemberPosition.President);
//            screen = "PlaceRegister";
//            System.out.println("**직급적용");
//        } else if (position.equals("Employee")) {
//            member.setPosition(MemberPosition.Employee);
//            screen = "PlaceJoin";
//        }
//        System.out.println("***직급" + member.getPosition() + "***화면:" + screen);
//        return screen;
//    }
}
