package kr.co.lunatalk.domain.member.service

import kr.co.lunatalk.domain.member.domain.Member
import kr.co.lunatalk.domain.member.dto.response.MemberInfoResponse
import kr.co.lunatalk.domain.member.repository.MemberRepository
import kr.co.lunatalk.domain.order.dto.response.OrderFindResponse
import kr.co.lunatalk.domain.order.repository.OrderRepository
import kr.co.lunatalk.global.exception.CustomException
import kr.co.lunatalk.global.exception.ErrorCode
import kr.co.lunatalk.global.util.SecurityUtil
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class MemberService(
    private val memberRepository: MemberRepository,
    private val orderRepository: OrderRepository,
    private val securityUtil: SecurityUtil
) {
    private val log = LoggerFactory.getLogger(MemberService::class.java)

    @Transactional(readOnly = true)
    fun myInformation(): MemberInfoResponse {
        return MemberInfoResponse.from(getCurrentMember())
    }

    fun getCurrentMember(): Member {
        return memberRepository
            .findById(securityUtil.getCurrentMemberId())
            .orElseThrow { CustomException(ErrorCode.MEMBER_NOT_FOUND) }
    }

    fun getMembers(pageable: Pageable): Page<MemberInfoResponse> {
        val members: Page<Member> = memberRepository.findMembers(pageable)
        return members.map { MemberInfoResponse.from(it) }
    }

    fun getMemberInformation(id: Long): MemberInfoResponse {
        val member = findMemberById(id)
        return MemberInfoResponse.from(member)
    }

    @Transactional(readOnly = true)
    fun findOrders(pageable: Pageable): Page<OrderFindResponse> {
        val member = getCurrentMember()
        val orders = orderRepository.findOrdersWithItemsByMemberId(member.id!!, pageable)
        return orders.map { OrderFindResponse.from(it) }
    }

    private fun findMemberById(id: Long): Member {
        return memberRepository.findById(id).orElseThrow {
            CustomException(ErrorCode.MEMBER_NOT_FOUND)
        }
    }
}
