package kr.co.lunatalk.domain.payment.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import kr.co.lunatalk.domain.payment.dto.request.PaymentCancelRequest
import kr.co.lunatalk.domain.payment.dto.request.PaymentConfirmRequest
import kr.co.lunatalk.domain.payment.dto.response.PaymentCancelResponse
import kr.co.lunatalk.domain.payment.dto.response.PaymentConfirmResponse
import kr.co.lunatalk.domain.payment.service.PaymentService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/payments")
@Tag(name = "결제", description = "결제(토스페이먼츠) 관련 API")
class PaymentController(
    private val paymentService: PaymentService
) {

    @PostMapping("/confirm")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "결제 승인(토스페이먼츠)",
        description = "토스페이먼츠 결제 완료 콜백에서 paymentKey / orderId / amount를 전달 받아 결제를 최종 승인합니다."
    )
    fun confirm(@Valid @RequestBody request: PaymentConfirmRequest): PaymentConfirmResponse {
        return paymentService.confirmPayment(request)
    }

    @PostMapping("/cancel")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "결제 취소(토스페이먼츠)",
        description = "결제 완료 건을 토스페이먼츠와 함께 취소합니다."
    )
    fun cancel(@Valid @RequestBody request: PaymentCancelRequest): PaymentCancelResponse {
        return paymentService.cancelPayment(request)
    }
}
