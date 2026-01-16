package kr.co.lunatalk.domain.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record OrderCreateRequest(
	@NotNull(message = "구매할 상품을 선택해주세요.")
	@Size(min = 1, message = "구매할 상품을 1개 이상 선택해주세요.")
	@Schema(name = "구매할 상품들")
	List<OrderProductRequest> products
) {
}
