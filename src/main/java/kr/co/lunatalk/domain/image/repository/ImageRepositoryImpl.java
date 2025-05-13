package kr.co.lunatalk.domain.image.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.lunatalk.domain.image.domain.Image;
import kr.co.lunatalk.domain.image.domain.ImageStatus;
import kr.co.lunatalk.domain.image.domain.ImageType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static kr.co.lunatalk.domain.image.domain.QImage.image;

@Repository
@RequiredArgsConstructor
public class ImageRepositoryImpl implements ImageRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	public List<Image> fetchProductImagesByProductId(Long productId) {
		return queryFactory
			.selectFrom(image)
			.where(image.referenceId.eq(productId)
				.and(image.imageType.in(ImageType.PRODUCT_CONTENT, ImageType.PRODUCT_THUMBNAIL)),
				isImageCompleted())
			.fetch();
	}

	public List<Image> fetchProductImagesByProductIds(List<Long> productIds) {
		return queryFactory
			.selectFrom(image)
			.where(image.referenceId.in(productIds)
				.and(image.imageType.in(ImageType.PRODUCT_CONTENT, ImageType.PRODUCT_THUMBNAIL)),
				isImageCompleted())
			.fetch();
	}

	private BooleanExpression isImageCompleted() {
		return image.imageStatus.eq(ImageStatus.COMPLETED);
	}
}
