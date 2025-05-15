package kr.co.lunatalk.domain.exhibition.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.lunatalk.domain.exhibition.dto.request.ExhibitionCreateRequest;
import kr.co.lunatalk.domain.exhibition.dto.request.ExhibitionUpdateRequest;
import kr.co.lunatalk.domain.exhibition.dto.response.ExhibitionCreateResponse;
import kr.co.lunatalk.domain.exhibition.dto.response.ExhibitionFindOneResponse;
import kr.co.lunatalk.domain.exhibition.dto.response.ExhibitionListResponse;
import kr.co.lunatalk.domain.exhibition.service.ExhibitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/exhibitions")
@RequiredArgsConstructor
@Tag(name = "기획전", description = "기획전 관련 API")
public class ExhibitionController {
	private final ExhibitionService exhibitionService;

	@PostMapping()
	@PreAuthorize("hasRole('ADMIN')")
	public ExhibitionCreateResponse createExhibition(@Valid  @RequestBody ExhibitionCreateRequest request) {
		return exhibitionService.createExhibition(request);
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ExhibitionFindOneResponse findExhibition(@PathVariable Long id) {
		return exhibitionService.getExhibitionById(id);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public void updateExhibition(@PathVariable Long id, @Valid @RequestBody ExhibitionUpdateRequest request) {
		exhibitionService.updateExhibition(id, request);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public void deleteExhibition(@PathVariable Long id) {
		exhibitionService.deleteExhibition(id);
	}

	@GetMapping()
	public ExhibitionListResponse getAllExhibitions() {
		return exhibitionService.getAllExhibitions();
	}
}
