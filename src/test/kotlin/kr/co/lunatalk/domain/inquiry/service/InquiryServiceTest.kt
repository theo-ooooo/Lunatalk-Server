package kr.co.lunatalk.domain.inquiry.service

import kr.co.lunatalk.domain.inquiry.domain.Inquiry
import kr.co.lunatalk.domain.inquiry.domain.InquiryReply
import kr.co.lunatalk.domain.inquiry.domain.InquiryStatus
import kr.co.lunatalk.domain.inquiry.domain.InquiryType
import kr.co.lunatalk.domain.inquiry.dto.request.InquiryCreateRequest
import kr.co.lunatalk.domain.inquiry.dto.request.InquiryReplyCreateRequest
import kr.co.lunatalk.domain.inquiry.dto.request.InquiryReplyUpdateRequest
import kr.co.lunatalk.domain.inquiry.dto.request.InquiryUpdateRequest
import kr.co.lunatalk.domain.inquiry.repository.InquiryReplyRepository
import kr.co.lunatalk.domain.inquiry.repository.InquiryRepository
import kr.co.lunatalk.domain.member.domain.Member
import kr.co.lunatalk.domain.member.domain.MemberRole
import kr.co.lunatalk.domain.member.domain.Profile
import kr.co.lunatalk.domain.order.domain.Order
import kr.co.lunatalk.domain.order.repository.OrderRepository
import kr.co.lunatalk.domain.product.domain.Product
import kr.co.lunatalk.domain.product.domain.ProductStatus
import kr.co.lunatalk.domain.product.domain.ProductVisibility
import kr.co.lunatalk.domain.product.repository.ProductRepository
import kr.co.lunatalk.global.exception.CustomException
import kr.co.lunatalk.global.exception.ErrorCode
import kr.co.lunatalk.global.util.MemberUtil
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.test.util.ReflectionTestUtils
import java.util.*

@ExtendWith(MockitoExtension::class)
@DisplayName("InquiryService 테스트")
class InquiryServiceTest {

    @Mock
    private lateinit var inquiryRepository: InquiryRepository

    @Mock
    private lateinit var inquiryReplyRepository: InquiryReplyRepository

    @Mock
    private lateinit var productRepository: ProductRepository

    @Mock
    private lateinit var orderRepository: OrderRepository

    @Mock
    private lateinit var memberUtil: MemberUtil

    @InjectMocks
    private lateinit var inquiryService: InquiryService

    private lateinit var member: Member
    private lateinit var admin: Member
    private lateinit var product: Product
    private lateinit var order: Order

    @BeforeEach
    fun setUp() {
        member = Member.createMember("testuser", "1234", Profile.of("테스트", ""), "01012341234", "test@test.com")
        ReflectionTestUtils.setField(member, "id", 1L)
        ReflectionTestUtils.setField(member, "role", MemberRole.USER)

        admin = Member.createMember("admin", "1234", Profile.of("관리자", ""), "01012341234", "admin@test.com")
        ReflectionTestUtils.setField(admin, "id", 2L)
        ReflectionTestUtils.setField(admin, "role", MemberRole.ADMIN)

        product = Product.createProduct("테스트 상품", 10000L, 10, ProductStatus.ACTIVE, ProductVisibility.VISIBLE)
        ReflectionTestUtils.setField(product, "id", 100L)

        order = Order.createOrder("L1234567890", member, 10000L)
        ReflectionTestUtils.setField(order, "id", 200L)
    }

    @Test
    @DisplayName("상품 문의 생성 성공")
    fun `createProductInquiry shouldSucceed`() {
        // given
        val request = InquiryCreateRequest(
            InquiryType.PRODUCT, "상품 문의", "상품에 대해 문의합니다.", 100L, null
        )

        given(memberUtil.currentMember).willReturn(member)
        given(productRepository.findById(100L)).willReturn(Optional.of(product))
        given(inquiryRepository.save(any(Inquiry::class.java))).willAnswer { invocation ->
            val saved = invocation.getArgument<Inquiry>(0)
            ReflectionTestUtils.setField(saved, "id", 1L)
            saved
        }

        // when
        val response = inquiryService.createInquiry(request)

        // then
        assertThat(response.type()).isEqualTo(InquiryType.PRODUCT)
        assertThat(response.title()).isEqualTo("상품 문의")
        assertThat(response.referenceId()).isEqualTo(100L)
        assertThat(response.referenceName()).isEqualTo("테스트 상품")
        verify(inquiryRepository).save(any(Inquiry::class.java))
    }

    @Test
    @DisplayName("주문 문의 생성 성공 - orderNumber 사용")
    fun `createOrderInquiry withOrderNumber shouldSucceed`() {
        // given
        val request = InquiryCreateRequest(
            InquiryType.ORDER, "주문 문의", "주문에 대해 문의합니다.", null, "L1234567890"
        )

        given(memberUtil.currentMember).willReturn(member)
        given(orderRepository.findByOrderWithItems("L1234567890")).willReturn(Optional.of(order))
        given(inquiryRepository.save(any(Inquiry::class.java))).willAnswer { invocation ->
            val saved = invocation.getArgument<Inquiry>(0)
            ReflectionTestUtils.setField(saved, "id", 1L)
            saved
        }

        // when
        val response = inquiryService.createInquiry(request)

        // then
        assertThat(response.type()).isEqualTo(InquiryType.ORDER)
        assertThat(response.referenceId()).isEqualTo(200L)
        assertThat(response.referenceName()).isEqualTo("L1234567890")
        verify(inquiryRepository).save(any(Inquiry::class.java))
    }

    @Test
    @DisplayName("주문 문의 생성 성공 - referenceId 사용")
    fun `createOrderInquiry withReferenceId shouldSucceed`() {
        // given
        val request = InquiryCreateRequest(
            InquiryType.ORDER, "주문 문의", "주문에 대해 문의합니다.", 200L, null
        )

        given(memberUtil.currentMember).willReturn(member)
        given(orderRepository.findById(200L)).willReturn(Optional.of(order))
        given(inquiryRepository.save(any(Inquiry::class.java))).willAnswer { invocation ->
            val saved = invocation.getArgument<Inquiry>(0)
            ReflectionTestUtils.setField(saved, "id", 1L)
            saved
        }

        // when
        val response = inquiryService.createInquiry(request)

        // then
        assertThat(response.type()).isEqualTo(InquiryType.ORDER)
        assertThat(response.referenceId()).isEqualTo(200L)
        verify(inquiryRepository).save(any(Inquiry::class.java))
    }

    @Test
    @DisplayName("일반 문의 생성 성공")
    fun `createGeneralInquiry shouldSucceed`() {
        // given
        val request = InquiryCreateRequest(
            InquiryType.GENERAL, "일반 문의", "일반 문의입니다.", null, null
        )

        given(memberUtil.currentMember).willReturn(member)
        given(inquiryRepository.save(any(Inquiry::class.java))).willAnswer { invocation ->
            val saved = invocation.getArgument<Inquiry>(0)
            ReflectionTestUtils.setField(saved, "id", 1L)
            saved
        }

        // when
        val response = inquiryService.createInquiry(request)

        // then
        assertThat(response.type()).isEqualTo(InquiryType.GENERAL)
        assertThat(response.referenceId()).isNull()
        assertThat(response.referenceName()).isNull()
        verify(inquiryRepository).save(any(Inquiry::class.java))
    }

    @Test
    @DisplayName("존재하지 않는 상품으로 문의 생성 시 예외")
    fun `createProductInquiry shouldThrow whenProductNotFound`() {
        // given
        val request = InquiryCreateRequest(
            InquiryType.PRODUCT, "상품 문의", "상품에 대해 문의합니다.", 999L, null
        )

        given(memberUtil.currentMember).willReturn(member)
        given(productRepository.findById(999L)).willReturn(Optional.empty())

        // when & then
        assertThatThrownBy { inquiryService.createInquiry(request) }
            .isInstanceOf(CustomException::class.java)
            .extracting("errorCode")
            .isEqualTo(ErrorCode.PRODUCT_NOT_FOUND)
    }

    @Test
    @DisplayName("상품 문의 생성 시 referenceId가 null이면 예외")
    fun `createProductInquiry shouldThrow whenReferenceIdIsNull`() {
        // given
        val request = InquiryCreateRequest(
            InquiryType.PRODUCT, "상품 문의", "상품에 대해 문의합니다.", null, null
        )

        given(memberUtil.currentMember).willReturn(member)

        // when & then
        assertThatThrownBy { inquiryService.createInquiry(request) }
            .isInstanceOf(CustomException::class.java)
            .extracting("errorCode")
            .isEqualTo(ErrorCode.BAD_REQUEST)
    }

    @Test
    @DisplayName("존재하지 않는 주문으로 문의 생성 시 예외")
    fun `createOrderInquiry shouldThrow whenOrderNotFound`() {
        // given
        val request = InquiryCreateRequest(
            InquiryType.ORDER, "주문 문의", "주문에 대해 문의합니다.", null, "INVALID"
        )

        given(memberUtil.currentMember).willReturn(member)
        given(orderRepository.findByOrderWithItems("INVALID")).willReturn(Optional.empty())

        // when & then
        assertThatThrownBy { inquiryService.createInquiry(request) }
            .isInstanceOf(CustomException::class.java)
            .extracting("errorCode")
            .isEqualTo(ErrorCode.ORDER_NOT_FOUND)
    }

    @Test
    @DisplayName("다른 사람의 주문으로 문의 생성 시 예외")
    fun `createOrderInquiry shouldThrow whenNotMyOrder`() {
        // given
        val otherMember = Member.createMember("other", "1234", Profile.of("다른사용자", ""), "01099999999", "other@test.com")
        ReflectionTestUtils.setField(otherMember, "id", 999L)
        val otherOrder = Order.createOrder("L9999999999", otherMember, 5000L)
        ReflectionTestUtils.setField(otherOrder, "id", 300L)

        val request = InquiryCreateRequest(
            InquiryType.ORDER, "주문 문의", "주문에 대해 문의합니다.", null, "L9999999999"
        )

        given(memberUtil.currentMember).willReturn(member)
        given(orderRepository.findByOrderWithItems("L9999999999")).willReturn(Optional.of(otherOrder))

        // when & then
        assertThatThrownBy { inquiryService.createInquiry(request) }
            .isInstanceOf(CustomException::class.java)
            .extracting("errorCode")
            .isEqualTo(ErrorCode.ORDER_NOT_FOUND)
    }

    @Test
    @DisplayName("문의 조회 성공")
    fun `findInquiry shouldSucceed`() {
        // given
        val inquiry = Inquiry.createProductInquiry(member, "제목", "내용", 100L)
        ReflectionTestUtils.setField(inquiry, "id", 1L)

        given(memberUtil.currentMember).willReturn(member)
        given(inquiryRepository.findInquiryByIdWithMember(1L)).willReturn(Optional.of(inquiry))
        given(productRepository.findById(100L)).willReturn(Optional.of(product))

        // when
        val response = inquiryService.findInquiry(1L)

        // then
        assertThat(response.inquiryId()).isEqualTo(1L)
        assertThat(response.title()).isEqualTo("제목")
    }

    @Test
    @DisplayName("본인 문의가 아닌 경우 조회 시 예외")
    fun `findInquiry shouldThrow whenNotMyInquiry`() {
        // given
        val otherMember = Member.createMember("other", "1234", Profile.of("다른사용자", ""), "01099999999", "other@test.com")
        ReflectionTestUtils.setField(otherMember, "id", 999L)
        val inquiry = Inquiry.createProductInquiry(otherMember, "제목", "내용", 100L)
        ReflectionTestUtils.setField(inquiry, "id", 1L)

        given(memberUtil.currentMember).willReturn(member)
        given(inquiryRepository.findInquiryByIdWithMember(1L)).willReturn(Optional.of(inquiry))

        // when & then
        assertThatThrownBy { inquiryService.findInquiry(1L) }
            .isInstanceOf(CustomException::class.java)
            .extracting("errorCode")
            .isEqualTo(ErrorCode.INQUIRY_UNAUTHORIZED)
    }

    @Test
    @DisplayName("관리자는 다른 사람의 문의도 조회 가능")
    fun `findInquiry shouldSucceed whenAdmin`() {
        // given
        val inquiry = Inquiry.createProductInquiry(member, "제목", "내용", 100L)
        ReflectionTestUtils.setField(inquiry, "id", 1L)

        given(memberUtil.currentMember).willReturn(admin)
        given(inquiryRepository.findInquiryByIdWithMember(1L)).willReturn(Optional.of(inquiry))
        given(productRepository.findById(100L)).willReturn(Optional.of(product))

        // when
        val response = inquiryService.findInquiry(1L)

        // then
        assertThat(response.inquiryId()).isEqualTo(1L)
    }

    @Test
    @DisplayName("내 문의 목록 조회 성공")
    fun `findMyInquiries shouldSucceed`() {
        // given
        val inquiry1 = Inquiry.createProductInquiry(member, "제목1", "내용1", 100L)
        val inquiry2 = Inquiry.createGeneralInquiry(member, "제목2", "내용2")
        ReflectionTestUtils.setField(inquiry1, "id", 1L)
        ReflectionTestUtils.setField(inquiry2, "id", 2L)

        val pageable = PageRequest.of(0, 10)
        val inquiryPage = PageImpl(listOf(inquiry1, inquiry2), pageable, 2)

        given(memberUtil.currentMember).willReturn(member)
        given(inquiryRepository.findAllInquiries(member.id, null, null, pageable)).willReturn(inquiryPage)
        given(productRepository.findById(100L)).willReturn(Optional.of(product))

        // when
        val response = inquiryService.findMyInquiries(null, null, pageable)

        // then
        assertThat(response.totalElements).isEqualTo(2)
        assertThat(response.content).hasSize(2)
    }

    @Test
    @DisplayName("관리자 전체 문의 목록 조회 성공")
    fun `findAllInquiriesForAdmin shouldSucceed`() {
        // given
        val inquiry = Inquiry.createProductInquiry(member, "제목", "내용", 100L)
        ReflectionTestUtils.setField(inquiry, "id", 1L)

        val pageable = PageRequest.of(0, 10)
        val inquiryPage = PageImpl(listOf(inquiry), pageable, 1)

        given(memberUtil.currentMember).willReturn(admin)
        given(inquiryRepository.findAllInquiriesForAdmin(null, null, null, pageable)).willReturn(inquiryPage)
        given(productRepository.findById(100L)).willReturn(Optional.of(product))

        // when
        val response = inquiryService.findAllInquiriesForAdmin(null, null, null, pageable)

        // then
        assertThat(response.totalElements).isEqualTo(1)
    }

    @Test
    @DisplayName("일반 사용자가 관리자 기능 사용 시 예외")
    fun `findAllInquiriesForAdmin shouldThrow whenNotAdmin`() {
        // given
        val pageable = PageRequest.of(0, 10)

        given(memberUtil.currentMember).willReturn(member)

        // when & then
        assertThatThrownBy { inquiryService.findAllInquiriesForAdmin(null, null, null, pageable) }
            .isInstanceOf(CustomException::class.java)
            .extracting("errorCode")
            .isEqualTo(ErrorCode.FORBIDDEN)
    }

    @Test
    @DisplayName("문의 수정 성공")
    fun `updateInquiry shouldSucceed`() {
        // given
        val inquiry = Inquiry.createProductInquiry(member, "제목", "내용", 100L)
        ReflectionTestUtils.setField(inquiry, "id", 1L)
        val request = InquiryUpdateRequest("수정 제목", "수정 내용")

        given(memberUtil.currentMember).willReturn(member)
        given(inquiryRepository.findInquiryByIdWithMember(1L)).willReturn(Optional.of(inquiry))
        given(productRepository.findById(100L)).willReturn(Optional.of(product))

        // when
        val response = inquiryService.updateInquiry(1L, request)

        // then
        assertThat(response.title()).isEqualTo("수정 제목")
        assertThat(response.content()).isEqualTo("수정 내용")
    }

    @Test
    @DisplayName("답변 완료된 문의 수정 시 예외")
    fun `updateInquiry shouldThrow whenAlreadyAnswered`() {
        // given
        val inquiry = Inquiry.createProductInquiry(member, "제목", "내용", 100L)
        ReflectionTestUtils.setField(inquiry, "id", 1L)
        val reply = InquiryReply.createReply(inquiry, admin, "답변")
        inquiry.addReply(reply)
        val request = InquiryUpdateRequest("수정 제목", "수정 내용")

        given(memberUtil.currentMember).willReturn(member)
        given(inquiryRepository.findInquiryByIdWithMember(1L)).willReturn(Optional.of(inquiry))

        // when & then
        assertThatThrownBy { inquiryService.updateInquiry(1L, request) }
            .isInstanceOf(CustomException::class.java)
            .extracting("errorCode")
            .isEqualTo(ErrorCode.INQUIRY_ALREADY_ANSWERED)
    }

    @Test
    @DisplayName("문의 삭제 성공")
    fun `deleteInquiry shouldSucceed`() {
        // given
        val inquiry = Inquiry.createProductInquiry(member, "제목", "내용", 100L)
        ReflectionTestUtils.setField(inquiry, "id", 1L)

        given(memberUtil.currentMember).willReturn(member)
        given(inquiryRepository.findInquiryByIdWithMember(1L)).willReturn(Optional.of(inquiry))

        // when
        inquiryService.deleteInquiry(1L)

        // then
        verify(inquiryRepository).delete(inquiry)
    }

    @Test
    @DisplayName("답변 생성 성공")
    fun `createReply shouldSucceed`() {
        // given
        val inquiry = Inquiry.createProductInquiry(member, "제목", "내용", 100L)
        ReflectionTestUtils.setField(inquiry, "id", 1L)
        val request = InquiryReplyCreateRequest("답변 내용")

        given(memberUtil.currentMember).willReturn(admin)
        given(inquiryRepository.findInquiryByIdWithMember(1L)).willReturn(Optional.of(inquiry))
        given(inquiryReplyRepository.save(any(InquiryReply::class.java))).willAnswer { invocation ->
            val saved = invocation.getArgument<InquiryReply>(0)
            ReflectionTestUtils.setField(saved, "id", 1L)
            saved
        }
        given(productRepository.findById(100L)).willReturn(Optional.of(product))

        // when
        val response = inquiryService.createReply(1L, request)

        // then
        assertThat(response.reply()).isNotNull()
        assertThat(response.status()).isEqualTo(InquiryStatus.ANSWERED)
        verify(inquiryReplyRepository).save(any(InquiryReply::class.java))
    }

    @Test
    @DisplayName("일반 사용자가 답변 생성 시 예외")
    fun `createReply shouldThrow whenNotAdmin`() {
        // given
        val request = InquiryReplyCreateRequest("답변 내용")

        given(memberUtil.currentMember).willReturn(member)

        // when & then
        assertThatThrownBy { inquiryService.createReply(1L, request) }
            .isInstanceOf(CustomException::class.java)
            .extracting("errorCode")
            .isEqualTo(ErrorCode.FORBIDDEN)
    }

    @Test
    @DisplayName("이미 답변이 있는 문의에 답변 생성 시 예외")
    fun `createReply shouldThrow whenAlreadyAnswered`() {
        // given
        val inquiry = Inquiry.createProductInquiry(member, "제목", "내용", 100L)
        ReflectionTestUtils.setField(inquiry, "id", 1L)
        val existingReply = InquiryReply.createReply(inquiry, admin, "기존 답변")
        inquiry.addReply(existingReply)
        val request = InquiryReplyCreateRequest("새 답변")

        given(memberUtil.currentMember).willReturn(admin)
        given(inquiryRepository.findInquiryByIdWithMember(1L)).willReturn(Optional.of(inquiry))

        // when & then
        assertThatThrownBy { inquiryService.createReply(1L, request) }
            .isInstanceOf(CustomException::class.java)
            .extracting("errorCode")
            .isEqualTo(ErrorCode.INQUIRY_ALREADY_ANSWERED)
    }

    @Test
    @DisplayName("답변 수정 성공")
    fun `updateReply shouldSucceed`() {
        // given
        val inquiry = Inquiry.createProductInquiry(member, "제목", "내용", 100L)
        ReflectionTestUtils.setField(inquiry, "id", 1L)
        val reply = InquiryReply.createReply(inquiry, admin, "기존 답변")
        ReflectionTestUtils.setField(reply, "id", 1L)
        inquiry.addReply(reply)
        val request = InquiryReplyUpdateRequest("수정된 답변")

        given(memberUtil.currentMember).willReturn(admin)
        given(inquiryRepository.findInquiryByIdWithMember(1L)).willReturn(Optional.of(inquiry))
        given(productRepository.findById(100L)).willReturn(Optional.of(product))

        // when
        val response = inquiryService.updateReply(1L, request)

        // then
        assertThat(response.reply().content()).isEqualTo("수정된 답변")
    }

    @Test
    @DisplayName("답변이 없는 문의의 답변 수정 시 예외")
    fun `updateReply shouldThrow whenReplyNotFound`() {
        // given
        val inquiry = Inquiry.createProductInquiry(member, "제목", "내용", 100L)
        ReflectionTestUtils.setField(inquiry, "id", 1L)
        val request = InquiryReplyUpdateRequest("수정된 답변")

        given(memberUtil.currentMember).willReturn(admin)
        given(inquiryRepository.findInquiryByIdWithMember(1L)).willReturn(Optional.of(inquiry))

        // when & then
        assertThatThrownBy { inquiryService.updateReply(1L, request) }
            .isInstanceOf(CustomException::class.java)
            .extracting("errorCode")
            .isEqualTo(ErrorCode.INQUIRY_REPLY_NOT_FOUND)
    }

    @Test
    @DisplayName("답변 삭제 성공")
    fun `deleteReply shouldSucceed`() {
        // given
        val inquiry = Inquiry.createProductInquiry(member, "제목", "내용", 100L)
        ReflectionTestUtils.setField(inquiry, "id", 1L)
        val reply = InquiryReply.createReply(inquiry, admin, "답변")
        ReflectionTestUtils.setField(reply, "id", 1L)
        inquiry.addReply(reply)

        given(memberUtil.currentMember).willReturn(admin)
        given(inquiryRepository.findInquiryByIdWithMember(1L)).willReturn(Optional.of(inquiry))

        // when
        inquiryService.deleteReply(1L)

        // then
        verify(inquiryReplyRepository).delete(reply)
        assertThat(inquiry.status).isEqualTo(InquiryStatus.PENDING)
    }
}
