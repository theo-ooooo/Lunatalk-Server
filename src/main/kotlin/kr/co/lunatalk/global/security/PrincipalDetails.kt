package kr.co.lunatalk.global.security

import kr.co.lunatalk.domain.member.domain.MemberRole
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class PrincipalDetails(
    private val memberId: Long,
    private val memberRole: MemberRole
) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> =
        setOf(SimpleGrantedAuthority(memberRole.value))

    override fun getPassword(): String? = null

    override fun getUsername(): String = memberId.toString()

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true
}
