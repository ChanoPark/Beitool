package com.beitool.beitool.api.service;

import com.beitool.beitool.api.repository.MemberRepository;
import com.beitool.beitool.domain.Member;
import com.beitool.beitool.domain.MemberPosition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    /*직급 결정*/
    public String setPosition(Long id, String position) {
        Member member = memberRepository.findOne(id);
        String screen = "redirect:/";
        System.out.println("***??" + position);
        System.out.println("**?" + id);
        if (position =="President") {
            member.setPosition(MemberPosition.President);
            screen = "PlaceRegister";
            System.out.println("**직급적용");
        } else if (position.equals("Employee")) {
            member.setPosition(MemberPosition.Employee);
            screen = "PlaceJoin";
        }
        System.out.println("***직급" + member.getPosition());
        return screen;
    }
}
