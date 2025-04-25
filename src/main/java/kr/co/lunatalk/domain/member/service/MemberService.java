package kr.co.lunatalk.domain.member.service;

import kr.co.lunatalk.domain.member.domain.Member;
import kr.co.lunatalk.domain.member.domain.MemberStatus;
import kr.co.lunatalk.domain.member.dto.response.MemberInfoResponse;
import kr.co.lunatalk.domain.member.repository.MemberRepository;
import kr.co.lunatalk.global.exception.CustomException;
import kr.co.lunatalk.global.exception.ErrorCode;
import kr.co.lunatalk.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MemberService {
	private final MemberRepository memberRepository;
	private final SecurityUtil securityUtil;

	@Transactional(readOnly = true)
	public MemberInfoResponse myInformation() {
		return MemberInfoResponse.from(getCurrentMember());

	}

	public Member getCurrentMember() {
		return memberRepository
			.findById(securityUtil.getCurrentMemberId())
			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
	}
}
