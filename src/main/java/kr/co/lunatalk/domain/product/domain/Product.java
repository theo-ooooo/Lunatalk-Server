package kr.co.lunatalk.domain.product.domain;

import jakarta.persistence.*;
import kr.co.lunatalk.domain.category.domain.Category;
import kr.co.lunatalk.domain.common.domain.BaseTimeEntity;
import kr.co.lunatalk.domain.product.dto.request.ProductUpdateRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.ArrayList;
import java.util.List;

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
	@ColumnDefault("'active'")
	private ProductStatus status = ProductStatus.ACTIVE;

	@Enumerated(EnumType.STRING)
	@Column(name = "visibility", nullable = false)
	@ColumnDefault("'hidden'")
	private ProductVisibility visibility = ProductVisibility.HIDDEN;

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ProductColor> productColor = new ArrayList<>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id")
	private Category category;

	@Builder(access = AccessLevel.PRIVATE)
	public Product(String name, Long price, Integer quantity, ProductStatus status, ProductVisibility visibility) {
		this.name = name;
		this.price = price;
		this.quantity = quantity;
		this.status = status;
		this.visibility = visibility;
	}

	public static Product createProduct(String name, Long price, Integer quantity, ProductStatus status, ProductVisibility visibility) {
		return Product.builder()
			.name(name)
			.price(price)
			.quantity(quantity)
			.status(status)
			.visibility(visibility)
			.build();
	}

	public void updateProduct(ProductUpdateRequest request) {
		if(request.name() != null) this.name = request.name();
		if(request.price() != null) this.price = request.price();
		if(request.quantity() != null) this.quantity = request.quantity();
		if(request.status() != null) this.status = request.status();

		if(!request.colors().isEmpty()) {
			clearProductColor();
			request.colors().forEach(color -> {
				addProductColor(ProductColor.createProductColor(this, color));
			});
		}
	}

	public void addProductColor(ProductColor productColor) {
		this.productColor.add(productColor);
	}

	public void clearProductColor() {
		this.productColor.clear();
	}

	public void deleteProduct() {
		if(this.visibility == ProductVisibility.VISIBLE) {
			this.visibility = ProductVisibility.HIDDEN;
		}

		if(this.status == ProductStatus.ACTIVE) {
			this.status = ProductStatus.DELETED;

		}
	}

	public void setCategory(Category category) {
		this.category = category;
	}
}
