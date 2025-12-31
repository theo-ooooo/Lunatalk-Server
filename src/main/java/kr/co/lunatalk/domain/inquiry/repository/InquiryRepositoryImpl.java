package kr.co.lunatalk.domain.inquiry.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.lunatalk.domain.inquiry.domain.Inquiry;
import kr.co.lunatalk.domain.inquiry.domain.InquiryStatus;
import kr.co.lunatalk.domain.inquiry.domain.InquiryType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static kr.co.lunatalk.domain.inquiry.domain.QInquiry.inquiry;
import static kr.co.lunatalk.domain.member.domain.QMember.member;

@Repository
@RequiredArgsConstructor
public class InquiryRepositoryImpl implements InquiryRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Optional<Inquiry> findInquiryByIdWithMember(Long inquiryId) {
		return Optional.ofNullable(
			queryFactory
				.selectFrom(inquiry)
				.leftJoin(inquiry.member, member).fetchJoin()
				.leftJoin(inquiry.reply).fetchJoin()
				.where(inquiry.id.eq(inquiryId))
				.fetchOne()
		);
	}

	@Override
	public Page<Inquiry> findAllInquiries(Long memberId, InquiryType type, InquiryStatus status, Pageable pageable) {
		var content = queryFactory
			.selectFrom(inquiry)
			.leftJoin(inquiry.member, member).fetchJoin()
			.leftJoin(inquiry.reply).fetchJoin()
			.where(
				memberIdEq(memberId),
				typeEq(type),
				statusEq(status)
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(inquiry.createdAt.desc())
			.fetch();

		Long count = Optional.ofNullable(
			queryFactory
				.select(inquiry.count())
				.from(inquiry)
				.leftJoin(inquiry.member, member)
				.where(
					memberIdEq(memberId),
					typeEq(type),
					statusEq(status)
				)
				.fetchOne()
		).orElse(0L);

		return new PageImpl<>(content, pageable, count);
	}

	@Override
	public Page<Inquiry> findAllInquiriesForAdmin(InquiryType type, InquiryStatus status, String memberUsername, Pageable pageable) {
		var content = queryFactory
			.selectFrom(inquiry)
			.leftJoin(inquiry.member, member).fetchJoin()
			.leftJoin(inquiry.reply).fetchJoin()
			.where(
				typeEq(type),
				statusEq(status),
				memberUsernameEq(memberUsername)
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(inquiry.createdAt.desc())
			.fetch();

		Long count = Optional.ofNullable(
			queryFactory
				.select(inquiry.count())
				.from(inquiry)
				.leftJoin(inquiry.member, member)
				.where(
					typeEq(type),
					statusEq(status),
					memberUsernameEq(memberUsername)
				)
				.fetchOne()
		).orElse(0L);

		return new PageImpl<>(content, pageable, count);
	}

	private BooleanExpression memberIdEq(Long memberId) {
		return memberId != null ? inquiry.member.id.eq(memberId) : null;
	}

	private BooleanExpression typeEq(InquiryType type) {
		return type != null ? inquiry.type.eq(type) : null;
	}

	private BooleanExpression statusEq(InquiryStatus status) {
		return status != null ? inquiry.status.eq(status) : null;
	}

	private BooleanExpression memberUsernameEq(String memberUsername) {
		return memberUsername != null && !memberUsername.isBlank() 
			? inquiry.member.username.containsIgnoreCase(memberUsername) 
			: null;
	}
}

