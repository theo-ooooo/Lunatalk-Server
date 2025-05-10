package kr.co.lunatalk.domain.member.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.lunatalk.domain.member.domain.Member;
import kr.co.lunatalk.domain.member.domain.QMember;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static kr.co.lunatalk.domain.member.domain.QMember.*;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {
	private final JPAQueryFactory queryFactory;
	@Override
	public Page<Member> findMembers(Pageable pageable) {
		List<Member> content = queryFactory.selectFrom(member)
			.orderBy(member.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();


		Long total = Optional.ofNullable(
			queryFactory
				.select(member.count())
				.from(member)
				.fetchOne()
		).orElse(0L);

		System.out.println("OFFSET = " + pageable.getOffset());
		System.out.println("PAGE = " + pageable.getPageNumber());

		return new PageImpl<>(content,pageable, total);
	}
}
