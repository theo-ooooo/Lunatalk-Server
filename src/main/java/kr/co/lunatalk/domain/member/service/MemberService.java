package kr.co.lunatalk.domain.member.service;

import kr.co.lunatalk.domain.member.domain.Member;
import kr.co.lunatalk.domain.member.domain.MemberStatus;
import kr.co.lunatalk.domain.member.repository.MemberRepository;
import kr.co.lunatalk.global.exception.CustomException;
import kr.co.lunatalk.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
	private final MemberRepository memberRepository;


	public void save(String username, String password) {
		Member member = Member.of(username, password);
		memberRepository.save(member);
	}

	public Member findByUsername(String username) {
		Optional<Member> findMember = memberRepository.findByUsername(username);
		if (findMember.isEmpty()) {
			throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
		}

		Member member = findMember.get();
		if(member.getStatus() == MemberStatus.DELETE) {
			throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
		}
		return member;
	}
}
