package kr.co.lunatalk.domain.cartitem.domain;

import jakarta.persistence.*;
import kr.co.lunatalk.domain.common.domain.BaseTimeEntity;
import kr.co.lunatalk.domain.member.domain.Member;
import kr.co.lunatalk.domain.product.domain.Product;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItem extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id")
	private Product product;

	private int quantity;

	@Builder
	public CartItem(Member member, Product product, int quantity) {
		this.member = member;
		this.product = product;
		this.quantity = quantity;
	}

	public static CartItem createCartItem(Member member, Product product, Integer quantity) {
		return CartItem.builder()
			.member(member)
			.product(product)
			.quantity(quantity == null ? 1 : quantity)
			.build();
	}

	public void updateQuantity(int quantity) {
		this.quantity += quantity;
	}
}
