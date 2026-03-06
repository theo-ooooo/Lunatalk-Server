package kr.co.lunatalk.domain.auth.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class LoginRequest(
    @field:NotBlank(message = "아이디는 필수로 입력해야 합니다.")
    @field:Size(min = 4, max = 50, message = "4자 이상 50자 이하로 작성해야 합니다.")
    @Schema(description = "로그인 ID", example = "kwkang")
    val username: String,

    @field:NotBlank(message = "비밀번호는 필수로 입력해야 합니다.")
    @field:Size(min = 8, message = "비밀번호는 8자 이상이여야 합니다.")
    @Schema(description = "비밀번호")
    val password: String,
)
