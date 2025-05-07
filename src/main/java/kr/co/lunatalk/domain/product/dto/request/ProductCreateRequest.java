package kr.co.lunatalk.domain.product.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.co.lunatalk.domain.product.domain.ProductStatus;
import kr.co.lunatalk.domain.product.domain.ProductVisibility;

import java.util.List;

public record ProductCreateRequest(
	@NotBlank(message = "상품 이름은 필수로 입력해야 합니다.")
	@Schema(description = "상품 이름")
	String name,

	@NotNull(message = "상품 가격은 필수로 입력해야 합니다.")
	@Min(value = 0, message = "상품 가격은 0 이상이여야 합니다.")
	@Schema(description = "상품 가격")
	Long price,

	@NotNull(message = "상품 갯수는 필수로 입력해야 합니다.")
	@Min(value = 1, message = "상품 갯수는 1개 이상이여야 합니다.")
	@Schema(description = "상품 갯수")
	Integer quantity,

	@NotNull(message = "상품 상태는 필수 입니다.")
	@Schema(description = "상품 상태")
	ProductStatus status,

	@NotNull(message = "상품 노출 여부는 필수 입니다.")
	@Schema(description = "상품 노출 여부")
	ProductVisibility visibility,

	@NotNull(message = "색상을 선택해주세요.")
	@Schema(description = "상품 컬러들")
	List<String> colors
) {
}
