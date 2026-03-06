package kr.co.lunatalk.domain.member.repository

import kr.co.lunatalk.domain.member.domain.Member
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface MemberRepository : JpaRepository<Member, Long>, MemberRepositoryCustom {
    fun findByUsername(username: String): Optional<Member>
}
