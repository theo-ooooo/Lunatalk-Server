package kr.co.lunatalk.domain.member.service

import kr.co.lunatalk.domain.member.domain.Member
import kr.co.lunatalk.domain.member.domain.Profile
import kr.co.lunatalk.domain.member.dto.response.MemberInfoResponse
import kr.co.lunatalk.domain.member.repository.MemberRepository
import kr.co.lunatalk.domain.order.domain.Order
import kr.co.lunatalk.domain.order.repository.OrderRepository
import kr.co.lunatalk.global.exception.CustomException
import kr.co.lunatalk.global.exception.ErrorCode
import kr.co.lunatalk.global.util.SecurityUtil
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.test.util.ReflectionTestUtils
import java.util.*

@ExtendWith(MockitoExtension::class)
class MemberServiceTest {

    private lateinit var memberService: MemberService

    @Mock
    private lateinit var memberRepository: MemberRepository

    @Mock
    private lateinit var orderRepository: OrderRepository

    @Mock
    private lateinit var securityUtil: SecurityUtil

    private lateinit var testMember: Member
    private lateinit var testOrder: Order

    @BeforeEach
    fun setUp() {
        memberService = MemberService(memberRepository, orderRepository, securityUtil)

        testMember = Member.createMember(
            "testuser",
            "1234",
            Profile.of("테스트닉", "img"),
            "01012341234",
            "test@email.com"
        )
        ReflectionTestUtils.setField(testMember, "id", 1L)

        testOrder = Order.createOrder(
            "test-test",
            testMember,
            1000L
        )
    }

    @Test
    fun `회원 리스트 조회`() {
        // given
        val memberPage = PageImpl(listOf(testMember))
        val pageable = PageRequest.of(0, 10)

        whenever(memberRepository.findMembers(pageable)).thenReturn(memberPage)

        // when
        val result = memberService.getMembers(pageable)

        // then
        assertEquals(1, result.content.size)
        assertEquals(testMember.email, result.content[0].email)
    }

    @Test
    fun `특정 회원 정보 조회`() {
        // given
        whenever(memberRepository.findById(testMember.id!!)).thenReturn(Optional.of(testMember))

        // when
        val response = memberService.getMemberInformation(testMember.id!!)

        // then
        assertEquals(testMember.profile.nickname, response.nickname)
        assertEquals(testMember.email, response.email)
    }

    @Test
    fun `특정 회원 정보 조회 실패 예외`() {
        // given
        val invalidId = 999L
        whenever(memberRepository.findById(invalidId)).thenReturn(Optional.empty())

        // when & then
        val exception = assertThrows(CustomException::class.java) {
            memberService.getMemberInformation(invalidId)
        }
        assertEquals(ErrorCode.MEMBER_NOT_FOUND, exception.errorCode)
    }

    @Test
    fun `현재 회원 정보 조회`() {
        // given
        whenever(securityUtil.getCurrentMemberId()).thenReturn(testMember.id!!)
        whenever(memberRepository.findById(testMember.id!!)).thenReturn(Optional.of(testMember))

        // when
        val response = memberService.myInformation()

        // then
        assertEquals(testMember.email, response.email)
        assertEquals(testMember.profile.nickname, response.nickname)
    }

    @Test
    fun `현재 회원 주문 조회`() {
        // given
        val pageable = PageRequest.of(0, 10)
        val orderPage = PageImpl(listOf(testOrder))

        whenever(securityUtil.getCurrentMemberId()).thenReturn(testMember.id!!)
        whenever(memberRepository.findById(testMember.id!!)).thenReturn(Optional.of(testMember))
        whenever(orderRepository.findOrdersWithItemsByMemberId(testMember.id!!, pageable))
            .thenReturn(orderPage)

        // when
        val responses = memberService.findOrders(pageable)

        // then
        assertEquals(1, responses.content.size)
        assertEquals(testOrder.orderNumber, responses.content[0].orderNumber)
        assertEquals(testOrder.totalPrice, responses.content[0].totalPrice)
    }

    @Test
    fun `MemberInfoResponse 정적 팩토리 메서드 검증`() {
        // when
        val response = MemberInfoResponse.from(testMember)

        // then
        assertEquals(testMember.id, response.memberId)
        assertEquals(testMember.email, response.email)
        assertEquals(testMember.profile.nickname, response.nickname)
    }
}
