package kr.co.lunatalk.domain.exhibition.service;

import kr.co.lunatalk.domain.exhibition.domain.Exhibition;
import kr.co.lunatalk.domain.exhibition.domain.ExhibitionProduct;
import kr.co.lunatalk.domain.exhibition.dto.ExhibitionProductDto;
import kr.co.lunatalk.domain.exhibition.dto.request.ExhibitionCreateRequest;
import kr.co.lunatalk.domain.exhibition.dto.request.ExhibitionUpdateRequest;
import kr.co.lunatalk.domain.exhibition.dto.response.ExhibitionCreateResponse;
import kr.co.lunatalk.domain.exhibition.dto.response.ExhibitionFindOneResponse;
import kr.co.lunatalk.domain.exhibition.dto.response.ExhibitionListResponse;
import kr.co.lunatalk.domain.exhibition.repository.ExhibitionRepository;
import kr.co.lunatalk.domain.image.domain.Image;
import kr.co.lunatalk.domain.image.repository.ImageRepository;
import kr.co.lunatalk.domain.product.domain.Product;
import kr.co.lunatalk.domain.product.dto.FindProductDto;
import kr.co.lunatalk.domain.product.dto.ProductWithImagesResult;
import kr.co.lunatalk.domain.product.dto.response.ProductFindResponse;
import kr.co.lunatalk.domain.productlike.service.ProductLikeService;
import kr.co.lunatalk.global.exception.CustomException;
import kr.co.lunatalk.global.exception.ErrorCode;
import kr.co.lunatalk.global.util.ProductUtil;
import kr.co.lunatalk.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Transactional
@RequiredArgsConstructor
public class ExhibitionService {
	private final ExhibitionRepository exhibitionRepository;
	private final ProductUtil productUtil;
	private final ImageRepository imageRepository;
	private final ProductLikeService productLikeService;
	private final SecurityUtil securityUtil;


	public ExhibitionCreateResponse createExhibition(ExhibitionCreateRequest request) {
		Exhibition exhibition = Exhibition.createExhibition(
			request.title(),
			request.description(),
			request.visibility(),
			request.startAt(),
			request.endAt());

		makeExhibitionProducts(request.productIds(), exhibition);

		exhibitionRepository.save(exhibition);

		return new ExhibitionCreateResponse(exhibition.getId());
	}

	@Transactional(readOnly = true)
	public List<ExhibitionFindOneResponse> getAllExhibitions() {
		List<Exhibition> exhibitions = exhibitionRepository.findAll();

		Map<Long, List<ExhibitionProductDto>> productMap = exhibitions.stream()
			.collect(Collectors.toMap(
				Exhibition::getId,
				exhibition -> {
					// 1. 상품 ID 추출
					List<Long> productIds = exhibition.getExhibitionProducts().stream()
						.map(ep -> ep.getProduct().getId())
						.toList();

					// 2. 상품 + 이미지 조회
					ProductWithImagesResult productWithImages = productUtil.findAllProducts(productIds);
					List<Product> products = productWithImages.products();
					Map<Long, List<Image>> imageMap = productWithImages.imageMap();

					// 3. 좋아요 정보 조회
					Map<Long, Long> likeCountMap = productLikeService.getLikeCounts(productIds);
					Long currentMemberId = getCurrentMemberId();
					Map<Long, Boolean> likedStatusMap = productLikeService.getLikedStatus(productIds, currentMemberId);

					// 4. ExhibitionProductDto 구성
					return exhibition.getExhibitionProducts().stream()
						.sorted(Comparator.comparingInt(ExhibitionProduct::getSortOrder))
						.map(ep -> {
							Product product = products.stream()
								.filter(p -> p.getId().equals(ep.getProduct().getId()))
								.findFirst()
								.orElseThrow(
									() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND)
								);

							List<Image> images = imageMap.getOrDefault(product.getId(), List.of());
							Long likeCount = likeCountMap.getOrDefault(product.getId(), 0L);
							Boolean isLiked = likedStatusMap.getOrDefault(product.getId(), false);

							return ExhibitionProductDto.from(product, images, ep.getSortOrder(), likeCount, isLiked);
						})
						.toList();
				}
			));

		return exhibitions.stream().map(exhibition -> ExhibitionFindOneResponse.from(exhibition, productMap.get(exhibition.getId()))).toList();
	}

	@Transactional(readOnly = true)
	public ExhibitionFindOneResponse getExhibitionById(Long id) {
		Exhibition exhibition = findById(id);

		// 1. 연결된 상품 ID 목록 추출
		List<Product> products = exhibition.getExhibitionProducts().stream()
			.map(ExhibitionProduct::getProduct)
			.toList();

		List<Long> productIds = products.stream()
			.map(Product::getId)
			.toList();

		// 2. 이미지들 일괄 조회
		List<Image> images = imageRepository.fetchProductImagesByProductIds(productIds);

		// 3. 좋아요 정보 조회
		Map<Long, Long> likeCountMap = productLikeService.getLikeCounts(productIds);
		Long currentMemberId = getCurrentMemberId();
		Map<Long, Boolean> likedStatusMap = productLikeService.getLikedStatus(productIds, currentMemberId);

		// 4. ExhibitionProductDto 생성
		List<ExhibitionProductDto> exhibitionProductDtos = exhibition.getExhibitionProducts().stream()
			.map(exhibitionProduct -> {
				Product product = exhibitionProduct.getProduct();
				int sortOrder = exhibitionProduct.getSortOrder();

				List<Image> productImages = images.stream()
					.filter(img -> img.getReferenceId().equals(product.getId()))
					.toList();

				Long likeCount = likeCountMap.getOrDefault(product.getId(), 0L);
				Boolean isLiked = likedStatusMap.getOrDefault(product.getId(), false);

				return ExhibitionProductDto.from(product, productImages, sortOrder, likeCount, isLiked);
			})
			.toList();

		// 4. Response 반환
		return ExhibitionFindOneResponse.from(exhibition, exhibitionProductDtos);
	}



	public void updateExhibition(Long exhibitionId, ExhibitionUpdateRequest request) {
		Exhibition findExhibition = findById(exhibitionId);

		exhibitionRepository.deleteProductByExhibitionId(findExhibition.getId());

		Exhibition exhibition = findById(exhibitionId);

		exhibition.updateExhibition(request.title(), request.description(), request.visibility(), request.startAt(), request.endAt());

		if(!request.productIds().isEmpty()) {
			makeExhibitionProducts(request.productIds(), exhibition);
		}

	}

	public void deleteExhibition(Long exhibitionId) {
		Exhibition findExhibition = findById(exhibitionId);

		exhibitionRepository.deleteById(findExhibition.getId());
	}

	private Exhibition findById(Long exhibitionId) {
		return exhibitionRepository.findById(exhibitionId).orElseThrow(
			() -> new CustomException(ErrorCode.EXHIBITION_NOT_FOUND)
		);
	}

	private void makeExhibitionProducts(List<Long> request, Exhibition exhibition) {
		List<Product> products = productUtil.findAllProductByProductIdIn(request);

		List<ExhibitionProduct> exhibitionProducts = IntStream.range(0, products.size())
			.mapToObj(i -> {
				Product product = products.get(i);
				return ExhibitionProduct.createExhibitionProduct(
					exhibition,
					product,
					i + 1
				);
			}).toList();

		exhibition.addProducts(exhibitionProducts);
	}

	private Long getCurrentMemberId() {
		try {
			return securityUtil.getCurrentMemberId();
		} catch (Exception e) {
			return null; // 비회원인 경우
		}
	}


}
