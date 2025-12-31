package kr.co.lunatalk.domain.inquiry.service;

import kr.co.lunatalk.domain.inquiry.domain.Inquiry;
import kr.co.lunatalk.domain.inquiry.domain.InquiryReply;
import kr.co.lunatalk.domain.inquiry.domain.InquiryStatus;
import kr.co.lunatalk.domain.inquiry.domain.InquiryType;
import kr.co.lunatalk.domain.inquiry.dto.request.InquiryCreateRequest;
import kr.co.lunatalk.domain.inquiry.dto.request.InquiryReplyCreateRequest;
import kr.co.lunatalk.domain.inquiry.dto.request.InquiryReplyUpdateRequest;
import kr.co.lunatalk.domain.inquiry.dto.request.InquiryUpdateRequest;
import kr.co.lunatalk.domain.inquiry.dto.response.InquiryResponse;
import kr.co.lunatalk.domain.inquiry.repository.InquiryReplyRepository;
import kr.co.lunatalk.domain.inquiry.repository.InquiryRepository;
import kr.co.lunatalk.domain.member.domain.Member;
import kr.co.lunatalk.domain.member.domain.MemberRole;
import kr.co.lunatalk.domain.member.domain.Profile;
import kr.co.lunatalk.domain.order.domain.Order;
import kr.co.lunatalk.domain.order.repository.OrderRepository;
import kr.co.lunatalk.domain.product.domain.Product;
import kr.co.lunatalk.domain.product.domain.ProductStatus;
import kr.co.lunatalk.domain.product.domain.ProductVisibility;
import kr.co.lunatalk.domain.product.repository.ProductRepository;
import kr.co.lunatalk.global.exception.CustomException;
import kr.co.lunatalk.global.exception.ErrorCode;
import kr.co.lunatalk.global.util.MemberUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("InquiryService 테스트")
class InquiryServiceTest {

	@Mock
	private InquiryRepository inquiryRepository;

	@Mock
	private InquiryReplyRepository inquiryReplyRepository;

	@Mock
	private ProductRepository productRepository;

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private MemberUtil memberUtil;

	@InjectMocks
	private InquiryService inquiryService;

	private Member member;
	private Member admin;
	private Product product;
	private Order order;

	@BeforeEach
	void setUp() {
		member = Member.createMember("testuser", "1234", Profile.of("테스트", ""), "01012341234", "test@test.com");
		ReflectionTestUtils.setField(member, "id", 1L);
		ReflectionTestUtils.setField(member, "role", MemberRole.USER);

		admin = Member.createMember("admin", "1234", Profile.of("관리자", ""), "01012341234", "admin@test.com");
		ReflectionTestUtils.setField(admin, "id", 2L);
		ReflectionTestUtils.setField(admin, "role", MemberRole.ADMIN);

		product = Product.createProduct("테스트 상품", 10000L, 10, ProductStatus.ACTIVE, ProductVisibility.VISIBLE);
		ReflectionTestUtils.setField(product, "id", 100L);

		order = Order.createOrder("L1234567890", member, 10000L);
		ReflectionTestUtils.setField(order, "id", 200L);
	}

	@Test
	@DisplayName("상품 문의 생성 성공")
	void createProductInquiry_shouldSucceed() {
		// given
		InquiryCreateRequest request = new InquiryCreateRequest(
			InquiryType.PRODUCT, "상품 문의", "상품에 대해 문의합니다.", 100L, null
		);

		given(memberUtil.getCurrentMember()).willReturn(member);
		given(productRepository.findById(100L)).willReturn(Optional.of(product));
		given(inquiryRepository.save(any(Inquiry.class))).willAnswer(invocation -> {
			Inquiry saved = invocation.getArgument(0);
			ReflectionTestUtils.setField(saved, "id", 1L);
			return saved;
		});

		// when
		InquiryResponse response = inquiryService.createInquiry(request);

		// then
		assertThat(response.type()).isEqualTo(InquiryType.PRODUCT);
		assertThat(response.title()).isEqualTo("상품 문의");
		assertThat(response.referenceId()).isEqualTo(100L);
		assertThat(response.referenceName()).isEqualTo("테스트 상품");
		verify(inquiryRepository).save(any(Inquiry.class));
	}

	@Test
	@DisplayName("주문 문의 생성 성공 - orderNumber 사용")
	void createOrderInquiry_withOrderNumber_shouldSucceed() {
		// given
		InquiryCreateRequest request = new InquiryCreateRequest(
			InquiryType.ORDER, "주문 문의", "주문에 대해 문의합니다.", null, "L1234567890"
		);

		given(memberUtil.getCurrentMember()).willReturn(member);
		given(orderRepository.findByOrderWithItems("L1234567890")).willReturn(Optional.of(order));
		given(inquiryRepository.save(any(Inquiry.class))).willAnswer(invocation -> {
			Inquiry saved = invocation.getArgument(0);
			ReflectionTestUtils.setField(saved, "id", 1L);
			return saved;
		});

		// when
		InquiryResponse response = inquiryService.createInquiry(request);

		// then
		assertThat(response.type()).isEqualTo(InquiryType.ORDER);
		assertThat(response.referenceId()).isEqualTo(200L);
		assertThat(response.referenceName()).isEqualTo("L1234567890");
		verify(inquiryRepository).save(any(Inquiry.class));
	}

	@Test
	@DisplayName("주문 문의 생성 성공 - referenceId 사용")
	void createOrderInquiry_withReferenceId_shouldSucceed() {
		// given
		InquiryCreateRequest request = new InquiryCreateRequest(
			InquiryType.ORDER, "주문 문의", "주문에 대해 문의합니다.", 200L, null
		);

		given(memberUtil.getCurrentMember()).willReturn(member);
		given(orderRepository.findById(200L)).willReturn(Optional.of(order));
		given(inquiryRepository.save(any(Inquiry.class))).willAnswer(invocation -> {
			Inquiry saved = invocation.getArgument(0);
			ReflectionTestUtils.setField(saved, "id", 1L);
			return saved;
		});

		// when
		InquiryResponse response = inquiryService.createInquiry(request);

		// then
		assertThat(response.type()).isEqualTo(InquiryType.ORDER);
		assertThat(response.referenceId()).isEqualTo(200L);
		verify(inquiryRepository).save(any(Inquiry.class));
	}

	@Test
	@DisplayName("일반 문의 생성 성공")
	void createGeneralInquiry_shouldSucceed() {
		// given
		InquiryCreateRequest request = new InquiryCreateRequest(
			InquiryType.GENERAL, "일반 문의", "일반 문의입니다.", null, null
		);

		given(memberUtil.getCurrentMember()).willReturn(member);
		given(inquiryRepository.save(any(Inquiry.class))).willAnswer(invocation -> {
			Inquiry saved = invocation.getArgument(0);
			ReflectionTestUtils.setField(saved, "id", 1L);
			return saved;
		});

		// when
		InquiryResponse response = inquiryService.createInquiry(request);

		// then
		assertThat(response.type()).isEqualTo(InquiryType.GENERAL);
		assertThat(response.referenceId()).isNull();
		assertThat(response.referenceName()).isNull();
		verify(inquiryRepository).save(any(Inquiry.class));
	}

	@Test
	@DisplayName("존재하지 않는 상품으로 문의 생성 시 예외")
	void createProductInquiry_shouldThrow_whenProductNotFound() {
		// given
		InquiryCreateRequest request = new InquiryCreateRequest(
			InquiryType.PRODUCT, "상품 문의", "상품에 대해 문의합니다.", 999L, null
		);

		given(memberUtil.getCurrentMember()).willReturn(member);
		given(productRepository.findById(999L)).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> inquiryService.createInquiry(request))
			.isInstanceOf(CustomException.class)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.PRODUCT_NOT_FOUND);
	}

	@Test
	@DisplayName("상품 문의 생성 시 referenceId가 null이면 예외")
	void createProductInquiry_shouldThrow_whenReferenceIdIsNull() {
		// given
		InquiryCreateRequest request = new InquiryCreateRequest(
			InquiryType.PRODUCT, "상품 문의", "상품에 대해 문의합니다.", null, null
		);

		given(memberUtil.getCurrentMember()).willReturn(member);

		// when & then
		assertThatThrownBy(() -> inquiryService.createInquiry(request))
			.isInstanceOf(CustomException.class)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.BAD_REQUEST);
	}

	@Test
	@DisplayName("존재하지 않는 주문으로 문의 생성 시 예외")
	void createOrderInquiry_shouldThrow_whenOrderNotFound() {
		// given
		InquiryCreateRequest request = new InquiryCreateRequest(
			InquiryType.ORDER, "주문 문의", "주문에 대해 문의합니다.", null, "INVALID"
		);

		given(memberUtil.getCurrentMember()).willReturn(member);
		given(orderRepository.findByOrderWithItems("INVALID")).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> inquiryService.createInquiry(request))
			.isInstanceOf(CustomException.class)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.ORDER_NOT_FOUND);
	}

	@Test
	@DisplayName("다른 사람의 주문으로 문의 생성 시 예외")
	void createOrderInquiry_shouldThrow_whenNotMyOrder() {
		// given
		Member otherMember = Member.createMember("other", "1234", Profile.of("다른사용자", ""), "01099999999", "other@test.com");
		ReflectionTestUtils.setField(otherMember, "id", 999L);
		Order otherOrder = Order.createOrder("L9999999999", otherMember, 5000L);
		ReflectionTestUtils.setField(otherOrder, "id", 300L);

		InquiryCreateRequest request = new InquiryCreateRequest(
			InquiryType.ORDER, "주문 문의", "주문에 대해 문의합니다.", null, "L9999999999"
		);

		given(memberUtil.getCurrentMember()).willReturn(member);
		given(orderRepository.findByOrderWithItems("L9999999999")).willReturn(Optional.of(otherOrder));

		// when & then
		assertThatThrownBy(() -> inquiryService.createInquiry(request))
			.isInstanceOf(CustomException.class)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.ORDER_NOT_FOUND);
	}

	@Test
	@DisplayName("문의 조회 성공")
	void findInquiry_shouldSucceed() {
		// given
		Inquiry inquiry = Inquiry.createProductInquiry(member, "제목", "내용", 100L);
		ReflectionTestUtils.setField(inquiry, "id", 1L);

		given(memberUtil.getCurrentMember()).willReturn(member);
		given(inquiryRepository.findInquiryByIdWithMember(1L)).willReturn(Optional.of(inquiry));
		given(productRepository.findById(100L)).willReturn(Optional.of(product));

		// when
		InquiryResponse response = inquiryService.findInquiry(1L);

		// then
		assertThat(response.inquiryId()).isEqualTo(1L);
		assertThat(response.title()).isEqualTo("제목");
	}

	@Test
	@DisplayName("본인 문의가 아닌 경우 조회 시 예외")
	void findInquiry_shouldThrow_whenNotMyInquiry() {
		// given
		Member otherMember = Member.createMember("other", "1234", Profile.of("다른사용자", ""), "01099999999", "other@test.com");
		ReflectionTestUtils.setField(otherMember, "id", 999L);
		Inquiry inquiry = Inquiry.createProductInquiry(otherMember, "제목", "내용", 100L);
		ReflectionTestUtils.setField(inquiry, "id", 1L);

		given(memberUtil.getCurrentMember()).willReturn(member);
		given(inquiryRepository.findInquiryByIdWithMember(1L)).willReturn(Optional.of(inquiry));

		// when & then
		assertThatThrownBy(() -> inquiryService.findInquiry(1L))
			.isInstanceOf(CustomException.class)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.INQUIRY_UNAUTHORIZED);
	}

	@Test
	@DisplayName("관리자는 다른 사람의 문의도 조회 가능")
	void findInquiry_shouldSucceed_whenAdmin() {
		// given
		Inquiry inquiry = Inquiry.createProductInquiry(member, "제목", "내용", 100L);
		ReflectionTestUtils.setField(inquiry, "id", 1L);

		given(memberUtil.getCurrentMember()).willReturn(admin);
		given(inquiryRepository.findInquiryByIdWithMember(1L)).willReturn(Optional.of(inquiry));
		given(productRepository.findById(100L)).willReturn(Optional.of(product));

		// when
		InquiryResponse response = inquiryService.findInquiry(1L);

		// then
		assertThat(response.inquiryId()).isEqualTo(1L);
	}

	@Test
	@DisplayName("내 문의 목록 조회 성공")
	void findMyInquiries_shouldSucceed() {
		// given
		Inquiry inquiry1 = Inquiry.createProductInquiry(member, "제목1", "내용1", 100L);
		Inquiry inquiry2 = Inquiry.createGeneralInquiry(member, "제목2", "내용2");
		ReflectionTestUtils.setField(inquiry1, "id", 1L);
		ReflectionTestUtils.setField(inquiry2, "id", 2L);

		Pageable pageable = PageRequest.of(0, 10);
		Page<Inquiry> inquiryPage = new PageImpl<>(List.of(inquiry1, inquiry2), pageable, 2);

		given(memberUtil.getCurrentMember()).willReturn(member);
		given(inquiryRepository.findAllInquiries(member.getId(), null, null, pageable)).willReturn(inquiryPage);
		given(productRepository.findById(100L)).willReturn(Optional.of(product));

		// when
		Page<InquiryResponse> response = inquiryService.findMyInquiries(null, null, pageable);

		// then
		assertThat(response.getTotalElements()).isEqualTo(2);
		assertThat(response.getContent()).hasSize(2);
	}

	@Test
	@DisplayName("관리자 전체 문의 목록 조회 성공")
	void findAllInquiriesForAdmin_shouldSucceed() {
		// given
		Inquiry inquiry = Inquiry.createProductInquiry(member, "제목", "내용", 100L);
		ReflectionTestUtils.setField(inquiry, "id", 1L);

		Pageable pageable = PageRequest.of(0, 10);
		Page<Inquiry> inquiryPage = new PageImpl<>(List.of(inquiry), pageable, 1);

		given(memberUtil.getCurrentMember()).willReturn(admin);
		given(inquiryRepository.findAllInquiriesForAdmin(null, null, null, pageable)).willReturn(inquiryPage);
		given(productRepository.findById(100L)).willReturn(Optional.of(product));

		// when
		Page<InquiryResponse> response = inquiryService.findAllInquiriesForAdmin(null, null, null, pageable);

		// then
		assertThat(response.getTotalElements()).isEqualTo(1);
	}

	@Test
	@DisplayName("일반 사용자가 관리자 기능 사용 시 예외")
	void findAllInquiriesForAdmin_shouldThrow_whenNotAdmin() {
		// given
		Pageable pageable = PageRequest.of(0, 10);

		given(memberUtil.getCurrentMember()).willReturn(member);

		// when & then
		assertThatThrownBy(() -> inquiryService.findAllInquiriesForAdmin(null, null, null, pageable))
			.isInstanceOf(CustomException.class)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.FORBIDDEN);
	}

	@Test
	@DisplayName("문의 수정 성공")
	void updateInquiry_shouldSucceed() {
		// given
		Inquiry inquiry = Inquiry.createProductInquiry(member, "제목", "내용", 100L);
		ReflectionTestUtils.setField(inquiry, "id", 1L);
		InquiryUpdateRequest request = new InquiryUpdateRequest("수정 제목", "수정 내용");

		given(memberUtil.getCurrentMember()).willReturn(member);
		given(inquiryRepository.findInquiryByIdWithMember(1L)).willReturn(Optional.of(inquiry));
		given(productRepository.findById(100L)).willReturn(Optional.of(product));

		// when
		InquiryResponse response = inquiryService.updateInquiry(1L, request);

		// then
		assertThat(response.title()).isEqualTo("수정 제목");
		assertThat(response.content()).isEqualTo("수정 내용");
	}

	@Test
	@DisplayName("답변 완료된 문의 수정 시 예외")
	void updateInquiry_shouldThrow_whenAlreadyAnswered() {
		// given
		Inquiry inquiry = Inquiry.createProductInquiry(member, "제목", "내용", 100L);
		ReflectionTestUtils.setField(inquiry, "id", 1L);
		InquiryReply reply = InquiryReply.createReply(inquiry, admin, "답변");
		inquiry.addReply(reply);
		InquiryUpdateRequest request = new InquiryUpdateRequest("수정 제목", "수정 내용");

		given(memberUtil.getCurrentMember()).willReturn(member);
		given(inquiryRepository.findInquiryByIdWithMember(1L)).willReturn(Optional.of(inquiry));

		// when & then
		assertThatThrownBy(() -> inquiryService.updateInquiry(1L, request))
			.isInstanceOf(CustomException.class)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.INQUIRY_ALREADY_ANSWERED);
	}

	@Test
	@DisplayName("문의 삭제 성공")
	void deleteInquiry_shouldSucceed() {
		// given
		Inquiry inquiry = Inquiry.createProductInquiry(member, "제목", "내용", 100L);
		ReflectionTestUtils.setField(inquiry, "id", 1L);

		given(memberUtil.getCurrentMember()).willReturn(member);
		given(inquiryRepository.findInquiryByIdWithMember(1L)).willReturn(Optional.of(inquiry));

		// when
		inquiryService.deleteInquiry(1L);

		// then
		verify(inquiryRepository).delete(inquiry);
	}

	@Test
	@DisplayName("답변 생성 성공")
	void createReply_shouldSucceed() {
		// given
		Inquiry inquiry = Inquiry.createProductInquiry(member, "제목", "내용", 100L);
		ReflectionTestUtils.setField(inquiry, "id", 1L);
		InquiryReplyCreateRequest request = new InquiryReplyCreateRequest("답변 내용");

		given(memberUtil.getCurrentMember()).willReturn(admin);
		given(inquiryRepository.findInquiryByIdWithMember(1L)).willReturn(Optional.of(inquiry));
		given(inquiryReplyRepository.save(any(InquiryReply.class))).willAnswer(invocation -> {
			InquiryReply saved = invocation.getArgument(0);
			ReflectionTestUtils.setField(saved, "id", 1L);
			return saved;
		});
		given(productRepository.findById(100L)).willReturn(Optional.of(product));

		// when
		InquiryResponse response = inquiryService.createReply(1L, request);

		// then
		assertThat(response.reply()).isNotNull();
		assertThat(response.status()).isEqualTo(InquiryStatus.ANSWERED);
		verify(inquiryReplyRepository).save(any(InquiryReply.class));
	}

	@Test
	@DisplayName("일반 사용자가 답변 생성 시 예외")
	void createReply_shouldThrow_whenNotAdmin() {
		// given
		InquiryReplyCreateRequest request = new InquiryReplyCreateRequest("답변 내용");

		given(memberUtil.getCurrentMember()).willReturn(member);

		// when & then
		assertThatThrownBy(() -> inquiryService.createReply(1L, request))
			.isInstanceOf(CustomException.class)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.FORBIDDEN);
	}

	@Test
	@DisplayName("이미 답변이 있는 문의에 답변 생성 시 예외")
	void createReply_shouldThrow_whenAlreadyAnswered() {
		// given
		Inquiry inquiry = Inquiry.createProductInquiry(member, "제목", "내용", 100L);
		ReflectionTestUtils.setField(inquiry, "id", 1L);
		InquiryReply existingReply = InquiryReply.createReply(inquiry, admin, "기존 답변");
		inquiry.addReply(existingReply);
		InquiryReplyCreateRequest request = new InquiryReplyCreateRequest("새 답변");

		given(memberUtil.getCurrentMember()).willReturn(admin);
		given(inquiryRepository.findInquiryByIdWithMember(1L)).willReturn(Optional.of(inquiry));

		// when & then
		assertThatThrownBy(() -> inquiryService.createReply(1L, request))
			.isInstanceOf(CustomException.class)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.INQUIRY_ALREADY_ANSWERED);
	}

	@Test
	@DisplayName("답변 수정 성공")
	void updateReply_shouldSucceed() {
		// given
		Inquiry inquiry = Inquiry.createProductInquiry(member, "제목", "내용", 100L);
		ReflectionTestUtils.setField(inquiry, "id", 1L);
		InquiryReply reply = InquiryReply.createReply(inquiry, admin, "기존 답변");
		ReflectionTestUtils.setField(reply, "id", 1L);
		inquiry.addReply(reply);
		InquiryReplyUpdateRequest request = new InquiryReplyUpdateRequest("수정된 답변");

		given(memberUtil.getCurrentMember()).willReturn(admin);
		given(inquiryRepository.findInquiryByIdWithMember(1L)).willReturn(Optional.of(inquiry));
		given(productRepository.findById(100L)).willReturn(Optional.of(product));

		// when
		InquiryResponse response = inquiryService.updateReply(1L, request);

		// then
		assertThat(response.reply().content()).isEqualTo("수정된 답변");
	}

	@Test
	@DisplayName("답변이 없는 문의의 답변 수정 시 예외")
	void updateReply_shouldThrow_whenReplyNotFound() {
		// given
		Inquiry inquiry = Inquiry.createProductInquiry(member, "제목", "내용", 100L);
		ReflectionTestUtils.setField(inquiry, "id", 1L);
		InquiryReplyUpdateRequest request = new InquiryReplyUpdateRequest("수정된 답변");

		given(memberUtil.getCurrentMember()).willReturn(admin);
		given(inquiryRepository.findInquiryByIdWithMember(1L)).willReturn(Optional.of(inquiry));

		// when & then
		assertThatThrownBy(() -> inquiryService.updateReply(1L, request))
			.isInstanceOf(CustomException.class)
			.extracting("errorCode")
			.isEqualTo(ErrorCode.INQUIRY_REPLY_NOT_FOUND);
	}

	@Test
	@DisplayName("답변 삭제 성공")
	void deleteReply_shouldSucceed() {
		// given
		Inquiry inquiry = Inquiry.createProductInquiry(member, "제목", "내용", 100L);
		ReflectionTestUtils.setField(inquiry, "id", 1L);
		InquiryReply reply = InquiryReply.createReply(inquiry, admin, "답변");
		ReflectionTestUtils.setField(reply, "id", 1L);
		inquiry.addReply(reply);

		given(memberUtil.getCurrentMember()).willReturn(admin);
		given(inquiryRepository.findInquiryByIdWithMember(1L)).willReturn(Optional.of(inquiry));

		// when
		inquiryService.deleteReply(1L);

		// then
		verify(inquiryReplyRepository).delete(reply);
		assertThat(inquiry.getStatus()).isEqualTo(InquiryStatus.PENDING);
	}
}

