package kr.co.lunatalk.domain.product.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kr.co.lunatalk.domain.product.domain.ProductStatus;
import kr.co.lunatalk.domain.product.domain.ProductVisibility;

import java.util.List;

public record ProductUpdateRequest(
	@NotBlank(message = "상품 이름은 필수로 입력해야 합니다.")
	@Schema(description = "상품 이름")
	String name,

	@NotNull(message = "상품 가격은 필수로 입력해야 합니다.")
	@Min(value = 0, message = "상품 가격은 0 이상이여야 합니다.")
	@Schema(description = "상품 가격")
	Long price,

	@NotNull(message = "상품 갯수는 필수로 입력해야 합니다.")
	@Min(value = 0, message = "상품 갯수는 1개 이상이여야 합니다.")
	@Schema(description = "상품 갯수")
	Integer quantity,

//	@NotNull(message = "상품 상태는 필수입니다.")
//	@Schema(description = "상품 상태")
//	ProductStatus status,

	@NotNull(message = "상품 노출여부는 필수 입니다.")
	@Schema(description = "상품 노출 여부")
	ProductVisibility visibility,

	@Schema(description = "상품 색상들")
	List<String> colors,

	@Schema(description = "연결할 카테고리 고유 ID")
	Long categoryId
) {
}
