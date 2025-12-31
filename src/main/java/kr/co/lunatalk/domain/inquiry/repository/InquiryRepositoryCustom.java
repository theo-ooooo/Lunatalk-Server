package kr.co.lunatalk.domain.inquiry.repository;

import kr.co.lunatalk.domain.inquiry.domain.Inquiry;
import kr.co.lunatalk.domain.inquiry.domain.InquiryStatus;
import kr.co.lunatalk.domain.inquiry.domain.InquiryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface InquiryRepositoryCustom {
	Optional<Inquiry> findInquiryByIdWithMember(Long inquiryId);
	Page<Inquiry> findAllInquiries(Long memberId, InquiryType type, InquiryStatus status, Pageable pageable);
	Page<Inquiry> findAllInquiriesForAdmin(InquiryType type, InquiryStatus status, String memberUsername, Pageable pageable);
}

