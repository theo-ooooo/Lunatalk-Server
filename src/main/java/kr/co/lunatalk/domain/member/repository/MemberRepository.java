package kr.co.lunatalk.domain.member.repository;

import kr.co.lunatalk.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
