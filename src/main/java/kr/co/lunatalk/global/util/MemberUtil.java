package kr.co.lunatalk.global.util;

import kr.co.lunatalk.domain.member.domain.Member;
import kr.co.lunatalk.domain.member.repository.MemberRepository;
import kr.co.lunatalk.global.exception.CustomException;
import kr.co.lunatalk.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class MemberUtil {
	private final MemberRepository memberRepository;
	private final SecurityUtil securityUtil;

	@Transactional(readOnly = true)
	public Member getCurrentMember() {
		return memberRepository
			.findById(securityUtil.getCurrentMemberId())
			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
	}

	@Transactional(readOnly = true)
	public Member getMemberByMemberId(Long memberId) {
		return memberRepository
			.findById(memberId)
			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
	}

}
