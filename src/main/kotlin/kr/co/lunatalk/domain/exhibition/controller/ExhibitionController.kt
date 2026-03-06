package kr.co.lunatalk.domain.exhibition.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import kr.co.lunatalk.domain.exhibition.dto.request.ExhibitionCreateRequest
import kr.co.lunatalk.domain.exhibition.dto.request.ExhibitionUpdateRequest
import kr.co.lunatalk.domain.exhibition.dto.response.ExhibitionCreateResponse
import kr.co.lunatalk.domain.exhibition.dto.response.ExhibitionFindOneResponse
import kr.co.lunatalk.domain.exhibition.service.ExhibitionService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/exhibitions")
@Tag(name = "기획전", description = "기획전 관련 API")
class ExhibitionController(
    private val exhibitionService: ExhibitionService
) {

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "기획전 생성", description = "기획전을 생성합니다.")
    fun createExhibition(@Valid @RequestBody request: ExhibitionCreateRequest): ExhibitionCreateResponse {
        return exhibitionService.createExhibition(request)
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "기획전 조회", description = "기획전을 조회합니다.")
    fun findExhibition(@PathVariable id: Long): ExhibitionFindOneResponse {
        return exhibitionService.getExhibitionById(id)
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "기획전 수정", description = "기획전을 수정합니다.")
    fun updateExhibition(@PathVariable id: Long, @Valid @RequestBody request: ExhibitionUpdateRequest) {
        exhibitionService.updateExhibition(id, request)
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "기획전 삭제", description = "기획전을 삭제 합니다.")
    fun deleteExhibition(@PathVariable id: Long) {
        exhibitionService.deleteExhibition(id)
    }

    @GetMapping
    @Operation(summary = "전체 기획전 조회", description = "전체 기획전을 조회합니다.")
    fun getAllExhibitions(): List<ExhibitionFindOneResponse> {
        return exhibitionService.getAllExhibitions()
    }
}
