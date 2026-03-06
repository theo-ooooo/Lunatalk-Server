package kr.co.lunatalk.domain.member.domain

import jakarta.persistence.*
import kr.co.lunatalk.domain.common.domain.BaseTimeEntity
import java.time.LocalDateTime

@Entity
open class Member protected constructor(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(unique = true, nullable = false)
    val username: String,

    @Column(nullable = false)
    val password: String,

    @Embedded
    var profile: Profile = Profile.of("", ""),

    @Enumerated(EnumType.STRING)
    var status: MemberStatus,

    @Enumerated(EnumType.STRING)
    val role: MemberRole,

    @Column(nullable = false)
    val phone: String,

    @Column(nullable = false)
    val email: String,

    var lastLoginAt: LocalDateTime? = null

) : BaseTimeEntity() {

    companion object {
        fun createMember(
            username: String,
            password: String,
            profile: Profile,
            phone: String,
            email: String
        ): Member {
            return Member(
                username = username,
                password = password,
                profile = profile,
                role = MemberRole.USER,
                status = MemberStatus.NORMAL,
                email = email,
                phone = phone,
                lastLoginAt = LocalDateTime.now()
            )
        }
    }

    fun updateProfile(profile: Profile) {
        this.profile = profile
    }

    fun withdrawal() {
        this.status = MemberStatus.DELETE
    }

    fun updateNickname(nickname: String) {
        this.profile = Profile.of(nickname, this.profile.profileImageUrl)
    }

    fun updateLastLoginAt() {
        this.lastLoginAt = LocalDateTime.now()
    }
}
