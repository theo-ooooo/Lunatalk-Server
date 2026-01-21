package kr.co.lunatalk.domain.exhibition.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.lunatalk.domain.exhibition.domain.Exhibition;
import kr.co.lunatalk.domain.exhibition.domain.ExhibitionVisibility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static kr.co.lunatalk.domain.exhibition.domain.QExhibition.exhibition;

@Repository
@RequiredArgsConstructor
public class ExhibitionRepositoryImpl implements ExhibitionRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<Exhibition> findActiveExhibitions(ExhibitionVisibility visibility, LocalDateTime now) {
		return queryFactory
			.selectFrom(exhibition)
			.where(
				exhibition.visibility.eq(visibility)
					.and(exhibition.startAt.loe(now))
					.and(isActiveEndDate(now))
			)
			.fetch();
	}

	private BooleanExpression isActiveEndDate(LocalDateTime now) {
		return exhibition.endAt.isNull().or(exhibition.endAt.goe(now));
	}
}
