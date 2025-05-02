package kr.co.lunatalk.domain.category.domain;

import jakarta.persistence.*;
import kr.co.lunatalk.domain.common.domain.BaseTimeEntity;
import kr.co.lunatalk.domain.product.domain.Product;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Category extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "category_id")
	private Long id;

	@Column(name = "name", unique = true, nullable = false)
	private String name;

	@Enumerated(EnumType.STRING)
	@ColumnDefault("'active'")
	private CategoryStatus status;

	@Enumerated(EnumType.STRING)
	@ColumnDefault("'visible'")
	private CategoryVisibility visibility;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "category")
	private List<Product> products = new ArrayList<>();

	@Builder(access = AccessLevel.PRIVATE)
	public Category(String name, CategoryStatus status, CategoryVisibility visibility) {
		this.name = name;
		this.status = status;
		this.visibility = visibility;
	}

	public static Category createCategory(String name, CategoryVisibility visibility) {
		return Category.builder()
			.name(name)
			.status(CategoryStatus.ACTIVE)
			.visibility(visibility)
			.build();
	}

	public void updateName(String name) {
		this.name = name;
	}

	public void deleteStatus() {
		this.status = CategoryStatus.DELETED;
	}

	public void updateVisibility(CategoryVisibility visibility) {
		this.visibility = visibility;
	}

	public void addProduct(Product product) {
		this.products.add(product);
		product.setCategory(this);
	}

	public void removeProduct(Product product) {
		this.products.remove(product);
		product.setCategory(null);
	}
}
