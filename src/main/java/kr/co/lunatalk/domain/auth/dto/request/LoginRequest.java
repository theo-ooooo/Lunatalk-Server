package kr.co.lunatalk.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
	@NotBlank(message = "아이디는 필수로 입력해야 합니다.")
	@Size(min = 4, max = 50, message = "4자 이상 50자 이하로 작성해야 합니다.")
	String username,

	@NotBlank(message = "비밀번호는 필수로 입력해야 합니다.")
	@Size(min = 8, message = "비밀번호는 8자 이상이여야 합니다.")
	String password
) {
}
