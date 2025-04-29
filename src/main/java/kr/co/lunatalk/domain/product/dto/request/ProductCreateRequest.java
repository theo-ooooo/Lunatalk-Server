package kr.co.lunatalk.domain.product.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kr.co.lunatalk.domain.product.domain.ProductStatus;

import java.util.List;

public record ProductCreateRequest(
	@NotBlank(message = "상품 이름은 필수로 입력해야 합니다.")
	String name,

	@NotNull(message = "상품 가격은 필수로 입력해야 합니다.")
	@Min(value = 0, message = "상품 가격은 0 이상이여야 합니다.")
	Long price,

	@NotNull(message = "상품 갯수는 필수로 입력해야 합니다.")
	@Min(value = 0, message = "상품 갯수는 1개 이상이여야 합니다.")
	Integer quantity,

	@NotNull(message = "상품 상태는 필수입니다.")
	ProductStatus status,

	@NotNull(message = "색상 리스트는 필수입니다.")
	@Size(min = 1, message = "색상은 최소 1개 이상 선택해야 합니다.")
	List<String> colors
) {
}
