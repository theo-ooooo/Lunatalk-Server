package kr.co.lunatalk.domain.member.controller;

import kr.co.lunatalk.domain.member.domain.Member;
import kr.co.lunatalk.domain.member.domain.Profile;
import kr.co.lunatalk.domain.member.dto.response.MemberInfoResponse;
import kr.co.lunatalk.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {
	private final MemberService memberService;


	@GetMapping("/me")
	public MemberInfoResponse myInformation() {
		return memberService.myInformation();
	}

}
