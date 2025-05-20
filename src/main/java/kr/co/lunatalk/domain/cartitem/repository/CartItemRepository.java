package kr.co.lunatalk.domain.cartitem.repository;

import kr.co.lunatalk.domain.cartitem.domain.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long>, CartItemRepositoryCustom {

}
