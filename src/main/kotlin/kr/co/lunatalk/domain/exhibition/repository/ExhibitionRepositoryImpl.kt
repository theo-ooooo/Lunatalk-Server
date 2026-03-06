package kr.co.lunatalk.domain.exhibition.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import kr.co.lunatalk.domain.exhibition.domain.Exhibition
import kr.co.lunatalk.domain.exhibition.domain.ExhibitionVisibility
import kr.co.lunatalk.domain.exhibition.domain.QExhibition.exhibition
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class ExhibitionRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : ExhibitionRepositoryCustom {

    override fun findActiveExhibitions(visibility: ExhibitionVisibility, now: LocalDateTime): List<Exhibition> {
        return queryFactory
            .selectFrom(exhibition)
            .where(
                exhibition.visibility.eq(visibility)
                    .and(exhibition.startAt.loe(now))
                    .and(isActiveEndDate(now))
            )
            .fetch()
    }

    private fun isActiveEndDate(now: LocalDateTime): BooleanExpression {
        return exhibition.endAt.isNull.or(exhibition.endAt.goe(now))
    }
}
