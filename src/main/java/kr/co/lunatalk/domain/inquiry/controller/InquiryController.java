package kr.co.lunatalk.domain.inquiry.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.lunatalk.domain.inquiry.domain.InquiryStatus;
import kr.co.lunatalk.domain.inquiry.domain.InquiryType;
import kr.co.lunatalk.domain.inquiry.dto.request.InquiryCreateRequest;
import kr.co.lunatalk.domain.inquiry.dto.request.InquiryReplyCreateRequest;
import kr.co.lunatalk.domain.inquiry.dto.request.InquiryReplyUpdateRequest;
import kr.co.lunatalk.domain.inquiry.dto.request.InquiryUpdateRequest;
import kr.co.lunatalk.domain.inquiry.dto.response.InquiryResponse;
import kr.co.lunatalk.domain.inquiry.service.InquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/inquiries")
@Tag(name = "문의", description = "문의 관련 API")
public class InquiryController {

	private final InquiryService inquiryService;

	@PostMapping
	@Operation(summary = "문의 생성", description = "상품, 주문 또는 일반 문의를 생성합니다.")
	public InquiryResponse createInquiry(@Valid @RequestBody InquiryCreateRequest request) {
		return inquiryService.createInquiry(request);
	}

	@GetMapping("/{inquiryId}")
	@Operation(summary = "문의 상세 조회", description = "문의 상세 정보를 조회합니다.")
	public InquiryResponse getInquiry(@PathVariable Long inquiryId) {
		return inquiryService.findInquiry(inquiryId);
	}

	@GetMapping("/my")
	@Operation(summary = "내 문의 목록 조회", description = "본인의 문의 목록을 조회합니다.")
	public Page<InquiryResponse> getMyInquiries(
		@RequestParam(required = false) InquiryType type,
		@RequestParam(required = false) InquiryStatus status,
		@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	) {
		return inquiryService.findMyInquiries(type, status, pageable);
	}

	@GetMapping("/admin")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "전체 문의 목록 조회 (관리자)", description = "전체 문의 목록을 조회합니다. (관리자 전용)")
	public Page<InquiryResponse> getAllInquiries(
		@RequestParam(required = false) InquiryType type,
		@RequestParam(required = false) InquiryStatus status,
		@RequestParam(required = false) String memberUsername,
		@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	) {
		return inquiryService.findAllInquiriesForAdmin(type, status, memberUsername, pageable);
	}

	@PutMapping("/{inquiryId}")
	@Operation(summary = "문의 수정", description = "본인의 문의를 수정합니다.")
	public InquiryResponse updateInquiry(
		@PathVariable Long inquiryId,
		@Valid @RequestBody InquiryUpdateRequest request
	) {
		return inquiryService.updateInquiry(inquiryId, request);
	}

	@DeleteMapping("/{inquiryId}")
	@Operation(summary = "문의 삭제", description = "본인의 문의를 삭제합니다.")
	public ResponseEntity<Void> deleteInquiry(@PathVariable Long inquiryId) {
		inquiryService.deleteInquiry(inquiryId);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/{inquiryId}/reply")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "문의 답변 생성", description = "문의에 대한 답변을 생성합니다. (관리자 전용)")
	public InquiryResponse createReply(
		@PathVariable Long inquiryId,
		@Valid @RequestBody InquiryReplyCreateRequest request
	) {
		return inquiryService.createReply(inquiryId, request);
	}

	@PutMapping("/{inquiryId}/reply")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "문의 답변 수정", description = "문의 답변을 수정합니다. (관리자 전용)")
	public InquiryResponse updateReply(
		@PathVariable Long inquiryId,
		@Valid @RequestBody InquiryReplyUpdateRequest request
	) {
		return inquiryService.updateReply(inquiryId, request);
	}

	@DeleteMapping("/{inquiryId}/reply")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "문의 답변 삭제", description = "문의 답변을 삭제합니다. (관리자 전용)")
	public ResponseEntity<Void> deleteReply(@PathVariable Long inquiryId) {
		inquiryService.deleteReply(inquiryId);
		return ResponseEntity.ok().build();
	}
}

