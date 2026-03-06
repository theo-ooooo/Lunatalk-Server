package kr.co.lunatalk.domain.member.domain

import jakarta.persistence.Embeddable

@Embeddable
class Profile protected constructor(
    val nickname: String = "",
    val profileImageUrl: String = ""
) {
    companion object {
        fun of(nickname: String, profileImageUrl: String): Profile {
            return Profile(nickname = nickname, profileImageUrl = profileImageUrl)
        }
    }

    override fun toString(): String {
        return "Profile(nickname='$nickname', profileImageUrl='$profileImageUrl')"
    }
}
