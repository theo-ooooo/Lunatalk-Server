package kr.co.lunatalk.domain.member.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import kr.co.lunatalk.domain.member.domain.Member
import java.time.LocalDateTime

data class MemberInfoResponse(
    @Schema(description = "회원 고유 ID")
    val memberId: Long?,

    @Schema(description = "로그인 ID")
    val username: String,

    @Schema(description = "회원 닉네임")
    val nickname: String,

    @Schema(description = "핸드폰 번호")
    val phone: String?,

    @Schema(description = "이메일 주소")
    val email: String?,

    @Schema(description = "회원 프로필 이미지 URL")
    val profileImgUrl: String?,

    @Schema(description = "소셜 로그인 제공자")
    val provider: String?,

    @Schema(description = "가입일")
    val createdAt: LocalDateTime?
) {
    companion object {
        fun from(member: Member): MemberInfoResponse {
            return MemberInfoResponse(
                memberId = member.id,
                username = member.username,
                nickname = member.profile.nickname,
                phone = member.phone,
                email = member.email,
                profileImgUrl = member.profile.profileImageUrl,
                provider = member.provider,
                createdAt = member.createdAt
            )
        }
    }
}
