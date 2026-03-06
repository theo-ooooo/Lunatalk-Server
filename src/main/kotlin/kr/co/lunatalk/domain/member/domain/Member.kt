package kr.co.lunatalk.domain.member.domain

import jakarta.persistence.*
import kr.co.lunatalk.domain.common.domain.BaseTimeEntity
import java.time.LocalDateTime

@Entity
@Table(uniqueConstraints = [
    UniqueConstraint(name = "uk_provider_provider_id", columnNames = ["provider", "provider_id"])
])
open class Member protected constructor(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(unique = true, nullable = false)
    val username: String,

    @Column(nullable = true)
    val password: String? = null,

    @Column(nullable = true)
    val provider: String? = null,

    @Column(name = "provider_id", nullable = true)
    val providerId: String? = null,

    @Embedded
    var profile: Profile = Profile.of("", ""),

    @Enumerated(EnumType.STRING)
    var status: MemberStatus,

    @Enumerated(EnumType.STRING)
    val role: MemberRole,

    @Column(nullable = true)
    val phone: String? = null,

    @Column(nullable = true)
    val email: String? = null,

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
                phone = if (phone.isBlank()) null else phone,
                lastLoginAt = LocalDateTime.now(),
                provider = null,
                providerId = null
            )
        }

        fun createSocialMember(
            username: String,
            profile: Profile,
            email: String,
            provider: String,
            providerId: String
        ): Member {
            return Member(
                username = username,
                password = null,
                profile = profile,
                role = MemberRole.USER,
                status = MemberStatus.NORMAL,
                email = email,
                phone = null,
                lastLoginAt = LocalDateTime.now(),
                provider = provider,
                providerId = providerId
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
