package kr.co.lunatalk.domain.member.domain;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kr.co.lunatalk.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;


@SpringBootTest
@Transactional
class MemberTest {

	@Autowired
	private MemberRepository memberRepository;

	@PersistenceContext
	EntityManager em;


	@Test
	@DisplayName("멤버 생성일과 수정일 확인")
	void 멤버_정보를_수정했을때_생성일과_수정일은_달라야_한다() {
		//given
		Member member = Member.of("test", "test1", Profile.of("",""));
		memberRepository.save(member);
		member.updateLastLoginAt();

		// when
		em.flush();
		em.clear();

		Member findMember = memberRepository.findById(member.getId()).orElseThrow();

		//then
		System.out.println("member.getCreatedAt() = " + findMember.getCreatedAt());
		System.out.println("member.getUpdatedAt() = " + findMember.getUpdatedAt());
		Assertions.assertNotNull(findMember.getCreatedAt());
		Assertions.assertNotNull(findMember.getUpdatedAt());
		Assertions.assertNotEquals(findMember.getCreatedAt(), findMember.getUpdatedAt());
	}

}
