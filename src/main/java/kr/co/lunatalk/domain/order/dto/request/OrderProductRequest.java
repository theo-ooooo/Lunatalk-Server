package kr.co.lunatalk.domain.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kr.co.lunatalk.domain.order.domain.OptionSnapshot;

public record OrderProductRequest(
	@NotNull(message = "상품 ID는 필수 값입니다.")
	@Schema(name = "상품 ID", example = "1")
	Long productId,

	@NotNull(message = "해당 상품을 구매할 갯수를 입력해주세요.")
	@Min(value = 1, message = "갯수는 1개 이상이여야 합니다.")
	@Schema(name = "상품 갯수")
	int quantity,

	@NotNull(message = "해당 상품에 옵션을 선택해주세요.")
	@Schema(name = "상품 옵션")
	OptionSnapshot optionSnapshot
) {
}
