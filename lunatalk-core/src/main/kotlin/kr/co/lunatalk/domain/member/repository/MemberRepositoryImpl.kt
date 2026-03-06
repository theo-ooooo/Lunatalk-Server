package kr.co.lunatalk.domain.member.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import kr.co.lunatalk.domain.member.domain.Member
import kr.co.lunatalk.domain.member.domain.QMember.member
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class MemberRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : MemberRepositoryCustom {

    override fun findMembers(pageable: Pageable): Page<Member> {
        val content: List<Member> = queryFactory.selectFrom(member)
            .orderBy(member.createdAt.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        val total: Long = queryFactory
            .select(member.count())
            .from(member)
            .fetchOne() ?: 0L

        return PageImpl(content, pageable, total)
    }
}
