package kr.co.lunatalk.domain.product.domain;

import jakarta.persistence.*;
import kr.co.lunatalk.domain.common.domain.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ProductColor extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id")
	private Product product;

	private String color;


	@Builder(access = AccessLevel.PRIVATE)
	public ProductColor(Product product, String color) {
		this.product = product;
		this.color = color;
	}

	public static ProductColor createProductColor(Product product, String color) {
		return ProductColor.builder()
			.product(product)
			.color(color)
			.build();
	}

	public void updateProductColor(String color) {
		this.color = color;
	}
}
