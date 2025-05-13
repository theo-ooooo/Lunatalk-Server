package kr.co.lunatalk.domain.member.repository;

import kr.co.lunatalk.domain.member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MemberRepositoryCustom {
	Page<Member> findMembers(Pageable pageable);
}
