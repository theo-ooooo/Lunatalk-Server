package kr.co.lunatalk.global.util

import kr.co.lunatalk.domain.member.domain.Member
import kr.co.lunatalk.domain.member.repository.MemberRepository
import kr.co.lunatalk.global.exception.CustomException
import kr.co.lunatalk.global.exception.ErrorCode
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class MemberUtil(
    private val memberRepository: MemberRepository,
    private val securityUtil: SecurityUtil
) {

    val currentMember: Member
        get() = findCurrentMember()

    @Transactional(readOnly = true)
    fun findCurrentMember(): Member =
        memberRepository
            .findById(securityUtil.getCurrentMemberId())
            .orElseThrow { CustomException(ErrorCode.MEMBER_NOT_FOUND) }

    @Transactional(readOnly = true)
    fun getMemberByMemberId(memberId: Long): Member =
        memberRepository
            .findById(memberId)
            .orElseThrow { CustomException(ErrorCode.MEMBER_NOT_FOUND) }
}
