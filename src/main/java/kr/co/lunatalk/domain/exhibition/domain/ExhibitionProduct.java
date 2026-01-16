package kr.co.lunatalk.domain.exhibition.domain;

import jakarta.persistence.*;
import kr.co.lunatalk.domain.common.domain.BaseTimeEntity;
import kr.co.lunatalk.domain.product.domain.Product;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExhibitionProduct extends BaseTimeEntity {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "exhibition_id")
	private Exhibition exhibition;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id")
	private Product product;

	private int sortOrder;

	@Builder
	public ExhibitionProduct(Exhibition exhibition, Product product, int sortOrder) {
		this.exhibition = exhibition;
		this.product = product;
		this.sortOrder = sortOrder;
	}

	public static ExhibitionProduct createExhibitionProduct(Exhibition exhibition, Product product, int sortOrder) {
		return ExhibitionProduct.builder()
			.exhibition(exhibition)
			.product(product)
			.sortOrder(sortOrder)
			.build();
	}
}
