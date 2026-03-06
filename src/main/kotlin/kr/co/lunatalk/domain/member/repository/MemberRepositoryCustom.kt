package kr.co.lunatalk.domain.member.repository

import kr.co.lunatalk.domain.member.domain.Member
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface MemberRepositoryCustom {
    fun findMembers(pageable: Pageable): Page<Member>
}
