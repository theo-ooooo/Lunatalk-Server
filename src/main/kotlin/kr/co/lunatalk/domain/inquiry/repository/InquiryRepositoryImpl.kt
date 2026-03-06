package kr.co.lunatalk.domain.inquiry.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import kr.co.lunatalk.domain.inquiry.domain.Inquiry
import kr.co.lunatalk.domain.inquiry.domain.InquiryStatus
import kr.co.lunatalk.domain.inquiry.domain.InquiryType
import kr.co.lunatalk.domain.inquiry.domain.QInquiry.inquiry
import kr.co.lunatalk.domain.member.domain.QMember.member
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
class InquiryRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : InquiryRepositoryCustom {

    override fun findInquiryByIdWithMember(inquiryId: Long): Optional<Inquiry> {
        return Optional.ofNullable(
            queryFactory
                .selectFrom(inquiry)
                .leftJoin(inquiry.member, member).fetchJoin()
                .leftJoin(inquiry.reply).fetchJoin()
                .where(inquiry.id.eq(inquiryId))
                .fetchOne()
        )
    }

    override fun findAllInquiries(
        memberId: Long,
        type: InquiryType?,
        status: InquiryStatus?,
        pageable: Pageable
    ): Page<Inquiry> {
        val content = queryFactory
            .selectFrom(inquiry)
            .leftJoin(inquiry.member, member).fetchJoin()
            .leftJoin(inquiry.reply).fetchJoin()
            .where(
                memberIdEq(memberId),
                typeEq(type),
                statusEq(status)
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(inquiry.createdAt.desc())
            .fetch()

        val count = Optional.ofNullable(
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
        ).orElse(0L)

        return PageImpl(content, pageable, count)
    }

    override fun findAllInquiriesForAdmin(
        type: InquiryType?,
        status: InquiryStatus?,
        memberUsername: String?,
        pageable: Pageable
    ): Page<Inquiry> {
        val content = queryFactory
            .selectFrom(inquiry)
            .leftJoin(inquiry.member, member).fetchJoin()
            .leftJoin(inquiry.reply).fetchJoin()
            .where(
                typeEq(type),
                statusEq(status),
                memberUsernameEq(memberUsername)
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(inquiry.createdAt.desc())
            .fetch()

        val count = Optional.ofNullable(
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
        ).orElse(0L)

        return PageImpl(content, pageable, count)
    }

    private fun memberIdEq(memberId: Long?): BooleanExpression? {
        return memberId?.let { inquiry.member.id.eq(it) }
    }

    private fun typeEq(type: InquiryType?): BooleanExpression? {
        return type?.let { inquiry.type.eq(it) }
    }

    private fun statusEq(status: InquiryStatus?): BooleanExpression? {
        return status?.let { inquiry.status.eq(it) }
    }

    private fun memberUsernameEq(memberUsername: String?): BooleanExpression? {
        return if (!memberUsername.isNullOrBlank()) {
            inquiry.member.username.containsIgnoreCase(memberUsername)
        } else {
            null
        }
    }
}
