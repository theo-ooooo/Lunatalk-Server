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
import kr.co.lunatalk.domain.order.domain.Order;
import kr.co.lunatalk.domain.order.repository.OrderRepository;
import kr.co.lunatalk.domain.product.domain.Product;
import kr.co.lunatalk.domain.product.repository.ProductRepository;
import kr.co.lunatalk.global.exception.CustomException;
import kr.co.lunatalk.global.exception.ErrorCode;
import kr.co.lunatalk.global.util.MemberUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class InquiryService {

	private final InquiryRepository inquiryRepository;
	private final InquiryReplyRepository inquiryReplyRepository;
	private final ProductRepository productRepository;
	private final OrderRepository orderRepository;
	private final MemberUtil memberUtil;

	public InquiryResponse createInquiry(InquiryCreateRequest request) {
		Member member = memberUtil.getCurrentMember();

		Inquiry inquiry;
		String referenceName = null;

		if (request.type() == InquiryType.PRODUCT) {
			if (request.referenceId() == null) {
				throw new CustomException(ErrorCode.BAD_REQUEST);
			}
			Product product = productRepository.findById(request.referenceId())
				.orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
			inquiry = Inquiry.createProductInquiry(member, request.title(), request.content(), product.getId());
			referenceName = product.getName();
		} else if (request.type() == InquiryType.ORDER) {
			Order order;
			if (request.orderNumber() != null && !request.orderNumber().isBlank()) {
				order = orderRepository.findByOrderWithItems(request.orderNumber())
					.orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));
			} else if (request.referenceId() != null) {
				order = orderRepository.findById(request.referenceId())
					.orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));
			} else {
				throw new CustomException(ErrorCode.BAD_REQUEST);
			}
			// 본인의 주문인지 확인
			if (!order.getMember().getId().equals(member.getId())) {
				throw new CustomException(ErrorCode.ORDER_NOT_FOUND);
			}
			inquiry = Inquiry.createOrderInquiry(member, request.title(), request.content(), order.getId());
			referenceName = order.getOrderNumber();
		} else if (request.type() == InquiryType.GENERAL) {
			inquiry = Inquiry.createGeneralInquiry(member, request.title(), request.content());
			referenceName = null;
		} else {
			throw new CustomException(ErrorCode.BAD_REQUEST);
		}

		inquiryRepository.save(inquiry);
		return InquiryResponse.from(inquiry, referenceName);
	}

	@Transactional(readOnly = true)
	public InquiryResponse findInquiry(Long inquiryId) {
		Member currentMember = memberUtil.getCurrentMember();
		Inquiry inquiry = inquiryRepository.findInquiryByIdWithMember(inquiryId)
			.orElseThrow(() -> new CustomException(ErrorCode.INQUIRY_NOT_FOUND));

		// 본인의 문의이거나 관리자인지 확인
		if (!isMyInquiryOrAdmin(inquiry, currentMember)) {
			throw new CustomException(ErrorCode.INQUIRY_UNAUTHORIZED);
		}

		String referenceName = getReferenceName(inquiry);
		return InquiryResponse.from(inquiry, referenceName);
	}

	@Transactional(readOnly = true)
	public Page<InquiryResponse> findMyInquiries(InquiryType type, InquiryStatus status, Pageable pageable) {
		Member member = memberUtil.getCurrentMember();
		Page<Inquiry> inquiries = inquiryRepository.findAllInquiries(member.getId(), type, status, pageable);
		return inquiries.map(inquiry -> InquiryResponse.from(inquiry, getReferenceName(inquiry)));
	}

	@Transactional(readOnly = true)
	public Page<InquiryResponse> findAllInquiriesForAdmin(InquiryType type, InquiryStatus status, String memberUsername, Pageable pageable) {
		Member admin = memberUtil.getCurrentMember();
		if (!admin.getRole().equals(MemberRole.ADMIN)) {
			throw new CustomException(ErrorCode.FORBIDDEN);
		}

		Page<Inquiry> inquiries = inquiryRepository.findAllInquiriesForAdmin(type, status, memberUsername, pageable);
		return inquiries.map(inquiry -> InquiryResponse.from(inquiry, getReferenceName(inquiry)));
	}

	public InquiryResponse updateInquiry(Long inquiryId, InquiryUpdateRequest request) {
		Member currentMember = memberUtil.getCurrentMember();
		Inquiry inquiry = inquiryRepository.findInquiryByIdWithMember(inquiryId)
			.orElseThrow(() -> new CustomException(ErrorCode.INQUIRY_NOT_FOUND));

		// 본인의 문의인지 확인
		if (!inquiry.getMember().getId().equals(currentMember.getId())) {
			throw new CustomException(ErrorCode.INQUIRY_UNAUTHORIZED);
		}

		// 이미 답변이 완료된 문의는 수정 불가
		if (inquiry.getStatus() == InquiryStatus.ANSWERED) {
			throw new CustomException(ErrorCode.INQUIRY_ALREADY_ANSWERED);
		}

		inquiry.update(request.title(), request.content());
		String referenceName = getReferenceName(inquiry);
		return InquiryResponse.from(inquiry, referenceName);
	}

	public void deleteInquiry(Long inquiryId) {
		Member currentMember = memberUtil.getCurrentMember();
		Inquiry inquiry = inquiryRepository.findInquiryByIdWithMember(inquiryId)
			.orElseThrow(() -> new CustomException(ErrorCode.INQUIRY_NOT_FOUND));

		// 본인의 문의이거나 관리자인지 확인
		if (!isMyInquiryOrAdmin(inquiry, currentMember)) {
			throw new CustomException(ErrorCode.INQUIRY_UNAUTHORIZED);
		}

		inquiryRepository.delete(inquiry);
	}

	public InquiryResponse createReply(Long inquiryId, InquiryReplyCreateRequest request) {
		Member admin = memberUtil.getCurrentMember();
		if (!admin.getRole().equals(MemberRole.ADMIN)) {
			throw new CustomException(ErrorCode.FORBIDDEN);
		}

		Inquiry inquiry = inquiryRepository.findInquiryByIdWithMember(inquiryId)
			.orElseThrow(() -> new CustomException(ErrorCode.INQUIRY_NOT_FOUND));

		if (inquiry.getReply() != null) {
			throw new CustomException(ErrorCode.INQUIRY_ALREADY_ANSWERED);
		}

		InquiryReply reply = InquiryReply.createReply(inquiry, admin, request.content());
		inquiry.addReply(reply);
		inquiryReplyRepository.save(reply);

		String referenceName = getReferenceName(inquiry);
		return InquiryResponse.from(inquiry, referenceName);
	}

	public InquiryResponse updateReply(Long inquiryId, InquiryReplyUpdateRequest request) {
		Member admin = memberUtil.getCurrentMember();
		if (!admin.getRole().equals(MemberRole.ADMIN)) {
			throw new CustomException(ErrorCode.FORBIDDEN);
		}

		Inquiry inquiry = inquiryRepository.findInquiryByIdWithMember(inquiryId)
			.orElseThrow(() -> new CustomException(ErrorCode.INQUIRY_NOT_FOUND));

		InquiryReply reply = inquiry.getReply();
		if (reply == null) {
			throw new CustomException(ErrorCode.INQUIRY_REPLY_NOT_FOUND);
		}

		reply.update(request.content());
		String referenceName = getReferenceName(inquiry);
		return InquiryResponse.from(inquiry, referenceName);
	}

	public void deleteReply(Long inquiryId) {
		Member admin = memberUtil.getCurrentMember();
		if (!admin.getRole().equals(MemberRole.ADMIN)) {
			throw new CustomException(ErrorCode.FORBIDDEN);
		}

		Inquiry inquiry = inquiryRepository.findInquiryByIdWithMember(inquiryId)
			.orElseThrow(() -> new CustomException(ErrorCode.INQUIRY_NOT_FOUND));

		InquiryReply reply = inquiry.getReply();
		if (reply == null) {
			throw new CustomException(ErrorCode.INQUIRY_REPLY_NOT_FOUND);
		}

		inquiry.updateStatus(InquiryStatus.PENDING);
		inquiryReplyRepository.delete(reply);
	}

	private boolean isMyInquiryOrAdmin(Inquiry inquiry, Member member) {
		boolean isAdmin = member.getRole().equals(MemberRole.ADMIN);
		return isAdmin || inquiry.getMember().getId().equals(member.getId());
	}

	private String getReferenceName(Inquiry inquiry) {
		if (inquiry.getReferenceId() == null) {
			return null;
		}

		if (inquiry.getType() == InquiryType.PRODUCT) {
			return productRepository.findById(inquiry.getReferenceId())
				.map(product -> product.getName())
				.orElse(null);
		} else if (inquiry.getType() == InquiryType.ORDER) {
			return orderRepository.findById(inquiry.getReferenceId())
				.map(order -> order.getOrderNumber())
				.orElse(null);
		}

		return null;
	}
}

