package kr.co.lunatalk.domain.product.domain;

import jakarta.persistence.*;
import kr.co.lunatalk.domain.common.domain.BaseTimeEntity;
import kr.co.lunatalk.domain.product.dto.request.ProductUpdateRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.boot.context.properties.bind.DefaultValue;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "product_id")
	private Long id;
	private String name;

	private Long price;

	private Integer quantity;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	@ColumnDefault("'DISABLE'")
	private ProductStatus status;

	@Builder(access = AccessLevel.PRIVATE)
	public Product(String name, Long price, Integer quantity, ProductStatus status) {
		this.name = name;
		this.price = price;
		this.quantity = quantity;
		this.status = status;
	}

	public static Product createProduct(String name, Long price, Integer quantity, ProductStatus status) {
		return Product.builder()
			.name(name)
			.price(price)
			.quantity(quantity)
			.status(status)
			.build();
	}

	public void updateProduct(ProductUpdateRequest request) {
		if(request.name() != null) this.name = request.name();
		if(request.price() != null) this.price = request.price();
		if(request.quantity() != null) this.quantity = request.quantity();
		if(request.status() != null) this.status = request.status();
	}
}
