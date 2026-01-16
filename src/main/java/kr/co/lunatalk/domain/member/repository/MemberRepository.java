package kr.co.lunatalk.domain.member.repository;

import kr.co.lunatalk.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
	public Optional<Member> findByUsername(String username);
}
