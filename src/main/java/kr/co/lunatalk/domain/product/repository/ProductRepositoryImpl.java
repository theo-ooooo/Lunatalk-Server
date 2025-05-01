package kr.co.lunatalk.domain.product.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.lunatalk.domain.image.domain.Image;
import kr.co.lunatalk.domain.image.domain.QImage;
import kr.co.lunatalk.domain.product.domain.*;
import kr.co.lunatalk.domain.product.dto.FindProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static kr.co.lunatalk.domain.image.domain.QImage.image;
import static kr.co.lunatalk.domain.product.domain.QProduct.product;
import static kr.co.lunatalk.domain.product.domain.QProductColor.productColor;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public FindProductDto findProductById(Long productId) {
		Product product = fetchProduct(productId);
		if (product == null) {
			return null;
		}

		List<ProductColor> colors = fetchColorsByProductId(productId);
		List<Image> images = fetchImagesByReferenceId(productId);

		return FindProductDto.from(product, colors, images);
	}

	private Product fetchProduct(Long productId) {
		return queryFactory
			.selectFrom(product)
			.where(
				product.id.eq(productId)
					.and(product.status.eq(ProductStatus.ACTIVE)
					.and(product.visibility.eq(ProductVisibility.VISIBLE)))
			)
			.fetchOne();
	}

	private List<ProductColor> fetchColorsByProductId(Long productId) {
		return queryFactory
			.selectFrom(productColor)
			.where(productColor.product.id.eq(productId))
			.fetch();
	}

	private List<Image> fetchImagesByReferenceId(Long referenceId) {
		return queryFactory
			.selectFrom(image)
			.where(image.referenceId.eq(referenceId))
			.fetch();
	}
}
