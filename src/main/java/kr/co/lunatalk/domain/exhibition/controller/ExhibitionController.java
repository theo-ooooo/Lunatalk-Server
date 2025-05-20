package kr.co.lunatalk.domain.exhibition.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.lunatalk.domain.exhibition.dto.request.ExhibitionCreateRequest;
import kr.co.lunatalk.domain.exhibition.dto.request.ExhibitionUpdateRequest;
import kr.co.lunatalk.domain.exhibition.dto.response.ExhibitionCreateResponse;
import kr.co.lunatalk.domain.exhibition.dto.response.ExhibitionFindOneResponse;
import kr.co.lunatalk.domain.exhibition.service.ExhibitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exhibitions")
@RequiredArgsConstructor
@Tag(name = "기획전", description = "기획전 관련 API")
public class ExhibitionController {
	private final ExhibitionService exhibitionService;

	@PostMapping()
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "기획전 생성", description = "기획전을 생성합니다.")
	public ExhibitionCreateResponse createExhibition(@Valid  @RequestBody ExhibitionCreateRequest request) {
		return exhibitionService.createExhibition(request);
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "기획전 조회", description = "기획전을 조회합니다.")
	public ExhibitionFindOneResponse findExhibition(@PathVariable Long id) {
		return exhibitionService.getExhibitionById(id);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "기획전 수정", description = "기획전을 수정합니다.")
	public void updateExhibition(@PathVariable Long id, @Valid @RequestBody ExhibitionUpdateRequest request) {
		exhibitionService.updateExhibition(id, request);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "기획전 삭제", description = "기획전을 삭제 합니다.")
	public void deleteExhibition(@PathVariable Long id) {
		exhibitionService.deleteExhibition(id);
	}

	@GetMapping()
	@Operation(summary = "전체 기획전 조회", description = "전체 기획전을 조회합니다.")
	public List<ExhibitionFindOneResponse> getAllExhibitions() {
		return exhibitionService.getAllExhibitions();
	}
}
