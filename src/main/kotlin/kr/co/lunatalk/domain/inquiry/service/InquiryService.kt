package kr.co.lunatalk.domain.inquiry.service

import kr.co.lunatalk.domain.inquiry.domain.Inquiry
import kr.co.lunatalk.domain.inquiry.domain.InquiryReply
import kr.co.lunatalk.domain.inquiry.domain.InquiryStatus
import kr.co.lunatalk.domain.inquiry.domain.InquiryType
import kr.co.lunatalk.domain.inquiry.dto.request.InquiryCreateRequest
import kr.co.lunatalk.domain.inquiry.dto.request.InquiryReplyCreateRequest
import kr.co.lunatalk.domain.inquiry.dto.request.InquiryReplyUpdateRequest
import kr.co.lunatalk.domain.inquiry.dto.request.InquiryUpdateRequest
import kr.co.lunatalk.domain.inquiry.dto.response.InquiryResponse
import kr.co.lunatalk.domain.inquiry.repository.InquiryReplyRepository
import kr.co.lunatalk.domain.inquiry.repository.InquiryRepository
import kr.co.lunatalk.domain.member.domain.Member
import kr.co.lunatalk.domain.member.domain.MemberRole
import kr.co.lunatalk.domain.order.repository.OrderRepository
import kr.co.lunatalk.domain.product.repository.ProductRepository
import kr.co.lunatalk.global.exception.CustomException
import kr.co.lunatalk.global.exception.ErrorCode
import kr.co.lunatalk.global.util.MemberUtil
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class InquiryService(
    private val inquiryRepository: InquiryRepository,
    private val inquiryReplyRepository: InquiryReplyRepository,
    private val productRepository: ProductRepository,
    private val orderRepository: OrderRepository,
    private val memberUtil: MemberUtil
) {

    fun createInquiry(request: InquiryCreateRequest): InquiryResponse {
        val member = memberUtil.currentMember

        val inquiry: Inquiry
        var referenceName: String? = null

        when (request.type) {
            InquiryType.PRODUCT -> {
                if (request.referenceId == null) {
                    throw CustomException(ErrorCode.BAD_REQUEST)
                }
                val product = productRepository.findById(request.referenceId).orElseThrow {
                    CustomException(ErrorCode.PRODUCT_NOT_FOUND)
                }
                inquiry = Inquiry.createProductInquiry(member, request.title!!, request.content!!, product.id!!)
                referenceName = product.name
            }
            InquiryType.ORDER -> {
                val order = if (!request.orderNumber.isNullOrBlank()) {
                    orderRepository.findByOrderWithItems(request.orderNumber).orElseThrow {
                        CustomException(ErrorCode.ORDER_NOT_FOUND)
                    }
                } else if (request.referenceId != null) {
                    orderRepository.findById(request.referenceId).orElseThrow {
                        CustomException(ErrorCode.ORDER_NOT_FOUND)
                    }
                } else {
                    throw CustomException(ErrorCode.BAD_REQUEST)
                }
                // 본인의 주문인지 확인
                if (order.member?.id != member.id) {
                    throw CustomException(ErrorCode.ORDER_NOT_FOUND)
                }
                inquiry = Inquiry.createOrderInquiry(member, request.title!!, request.content!!, order.id!!)
                referenceName = order.orderNumber
            }
            InquiryType.GENERAL -> {
                inquiry = Inquiry.createGeneralInquiry(member, request.title!!, request.content!!)
                referenceName = null
            }
            else -> throw CustomException(ErrorCode.BAD_REQUEST)
        }

        inquiryRepository.save(inquiry)
        return InquiryResponse.from(inquiry, referenceName)
    }

    @Transactional(readOnly = true)
    fun findInquiry(inquiryId: Long): InquiryResponse {
        val currentMember = memberUtil.currentMember
        val inquiry = inquiryRepository.findInquiryByIdWithMember(inquiryId).orElseThrow {
            CustomException(ErrorCode.INQUIRY_NOT_FOUND)
        }

        // 본인의 문의이거나 관리자인지 확인
        if (!isMyInquiryOrAdmin(inquiry, currentMember)) {
            throw CustomException(ErrorCode.INQUIRY_UNAUTHORIZED)
        }

        val referenceName = getReferenceName(inquiry)
        return InquiryResponse.from(inquiry, referenceName)
    }

    @Transactional(readOnly = true)
    fun findMyInquiries(type: InquiryType?, status: InquiryStatus?, pageable: Pageable): Page<InquiryResponse> {
        val member = memberUtil.currentMember
        val inquiries = inquiryRepository.findAllInquiries(member.id!!, type, status, pageable)
        return inquiries.map { inquiry -> InquiryResponse.from(inquiry, getReferenceName(inquiry)) }
    }

    @Transactional(readOnly = true)
    fun findAllInquiriesForAdmin(
        type: InquiryType?,
        status: InquiryStatus?,
        memberUsername: String?,
        pageable: Pageable
    ): Page<InquiryResponse> {
        val admin = memberUtil.currentMember
        if (admin.role != MemberRole.ADMIN) {
            throw CustomException(ErrorCode.FORBIDDEN)
        }

        val inquiries = inquiryRepository.findAllInquiriesForAdmin(type, status, memberUsername, pageable)
        return inquiries.map { inquiry -> InquiryResponse.from(inquiry, getReferenceName(inquiry)) }
    }

    fun updateInquiry(inquiryId: Long, request: InquiryUpdateRequest): InquiryResponse {
        val currentMember = memberUtil.currentMember
        val inquiry = inquiryRepository.findInquiryByIdWithMember(inquiryId).orElseThrow {
            CustomException(ErrorCode.INQUIRY_NOT_FOUND)
        }

        // 본인의 문의인지 확인
        if (inquiry.member!!.id != currentMember.id) {
            throw CustomException(ErrorCode.INQUIRY_UNAUTHORIZED)
        }

        // 이미 답변이 완료된 문의는 수정 불가
        if (inquiry.status == InquiryStatus.ANSWERED) {
            throw CustomException(ErrorCode.INQUIRY_ALREADY_ANSWERED)
        }

        inquiry.update(request.title!!, request.content!!)
        val referenceName = getReferenceName(inquiry)
        return InquiryResponse.from(inquiry, referenceName)
    }

    fun deleteInquiry(inquiryId: Long) {
        val currentMember = memberUtil.currentMember
        val inquiry = inquiryRepository.findInquiryByIdWithMember(inquiryId).orElseThrow {
            CustomException(ErrorCode.INQUIRY_NOT_FOUND)
        }

        // 본인의 문의이거나 관리자인지 확인
        if (!isMyInquiryOrAdmin(inquiry, currentMember)) {
            throw CustomException(ErrorCode.INQUIRY_UNAUTHORIZED)
        }

        inquiryRepository.delete(inquiry)
    }

    fun createReply(inquiryId: Long, request: InquiryReplyCreateRequest): InquiryResponse {
        val admin = memberUtil.currentMember
        if (admin.role != MemberRole.ADMIN) {
            throw CustomException(ErrorCode.FORBIDDEN)
        }

        val inquiry = inquiryRepository.findInquiryByIdWithMember(inquiryId).orElseThrow {
            CustomException(ErrorCode.INQUIRY_NOT_FOUND)
        }

        if (inquiry.reply != null) {
            throw CustomException(ErrorCode.INQUIRY_ALREADY_ANSWERED)
        }

        val reply = InquiryReply.createReply(inquiry, admin, request.content!!)
        inquiry.addReply(reply)
        inquiryReplyRepository.save(reply)

        val referenceName = getReferenceName(inquiry)
        return InquiryResponse.from(inquiry, referenceName)
    }

    fun updateReply(inquiryId: Long, request: InquiryReplyUpdateRequest): InquiryResponse {
        val admin = memberUtil.currentMember
        if (admin.role != MemberRole.ADMIN) {
            throw CustomException(ErrorCode.FORBIDDEN)
        }

        val inquiry = inquiryRepository.findInquiryByIdWithMember(inquiryId).orElseThrow {
            CustomException(ErrorCode.INQUIRY_NOT_FOUND)
        }

        val reply = inquiry.reply ?: throw CustomException(ErrorCode.INQUIRY_REPLY_NOT_FOUND)

        reply.update(request.content!!)
        val referenceName = getReferenceName(inquiry)
        return InquiryResponse.from(inquiry, referenceName)
    }

    fun deleteReply(inquiryId: Long) {
        val admin = memberUtil.currentMember
        if (admin.role != MemberRole.ADMIN) {
            throw CustomException(ErrorCode.FORBIDDEN)
        }

        val inquiry = inquiryRepository.findInquiryByIdWithMember(inquiryId).orElseThrow {
            CustomException(ErrorCode.INQUIRY_NOT_FOUND)
        }

        val reply = inquiry.reply ?: throw CustomException(ErrorCode.INQUIRY_REPLY_NOT_FOUND)

        inquiry.updateStatus(InquiryStatus.PENDING)
        inquiryReplyRepository.delete(reply)
    }

    private fun isMyInquiryOrAdmin(inquiry: Inquiry, member: Member): Boolean {
        val isAdmin = member.role == MemberRole.ADMIN
        return isAdmin || inquiry.member!!.id == member.id
    }

    private fun getReferenceName(inquiry: Inquiry): String? {
        if (inquiry.referenceId == null) {
            return null
        }

        return when (inquiry.type) {
            InquiryType.PRODUCT -> productRepository.findById(inquiry.referenceId!!)
                .map { it.name }
                .orElse(null)
            InquiryType.ORDER -> orderRepository.findById(inquiry.referenceId!!)
                .map { it.orderNumber }
                .orElse(null)
            else -> null
        }
    }
}
