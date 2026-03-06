package kr.co.lunatalk.domain.inquiry.repository

import kr.co.lunatalk.domain.inquiry.domain.Inquiry
import kr.co.lunatalk.domain.inquiry.domain.InquiryStatus
import kr.co.lunatalk.domain.inquiry.domain.InquiryType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.Optional

interface InquiryRepositoryCustom {
    fun findInquiryByIdWithMember(inquiryId: Long): Optional<Inquiry>
    fun findAllInquiries(memberId: Long, type: InquiryType?, status: InquiryStatus?, pageable: Pageable): Page<Inquiry>
    fun findAllInquiriesForAdmin(type: InquiryType?, status: InquiryStatus?, memberUsername: String?, pageable: Pageable): Page<Inquiry>
}
