package kr.co.lunatalk.domain.exhibition.service

import kr.co.lunatalk.domain.exhibition.domain.Exhibition
import kr.co.lunatalk.domain.exhibition.domain.ExhibitionProduct
import kr.co.lunatalk.domain.exhibition.dto.ExhibitionProductDto
import kr.co.lunatalk.domain.exhibition.dto.request.ExhibitionCreateRequest
import kr.co.lunatalk.domain.exhibition.dto.request.ExhibitionUpdateRequest
import kr.co.lunatalk.domain.exhibition.dto.response.ExhibitionCreateResponse
import kr.co.lunatalk.domain.exhibition.dto.response.ExhibitionFindOneResponse
import kr.co.lunatalk.domain.exhibition.repository.ExhibitionRepository
import kr.co.lunatalk.domain.image.repository.ImageRepository
import kr.co.lunatalk.domain.product.domain.Product
import kr.co.lunatalk.domain.productlike.service.ProductLikeService
import kr.co.lunatalk.global.exception.CustomException
import kr.co.lunatalk.global.exception.ErrorCode
import kr.co.lunatalk.global.util.ProductUtil
import kr.co.lunatalk.global.util.SecurityUtil
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ExhibitionService(
    private val exhibitionRepository: ExhibitionRepository,
    private val productUtil: ProductUtil,
    private val imageRepository: ImageRepository,
    private val productLikeService: ProductLikeService,
    private val securityUtil: SecurityUtil
) {

    fun createExhibition(request: ExhibitionCreateRequest): ExhibitionCreateResponse {
        val exhibition = Exhibition.createExhibition(
            request.title!!,
            request.description,
            request.visibility,
            request.startAt!!,
            request.endAt
        )

        makeExhibitionProducts(request.productIds!!, exhibition)

        exhibitionRepository.save(exhibition)

        return ExhibitionCreateResponse(exhibition.id)
    }

    @Transactional(readOnly = true)
    fun getAllExhibitions(): List<ExhibitionFindOneResponse> {
        val exhibitions = exhibitionRepository.findAll()

        val productMap = exhibitions.associate { exhibition ->
            exhibition.id to run {
                // 1. 상품 ID 추출
                val productIds = exhibition.exhibitionProducts.map { ep -> ep.product!!.id!! }

                // 2. 상품 + 이미지 조회
                val productWithImages = productUtil.findAllProducts(productIds)
                val products = productWithImages.products
                val imageMap = productWithImages.imageMap

                // 3. 좋아요 정보 조회
                val likeCountMap = productLikeService.getLikeCounts(productIds)
                val currentMemberId = getCurrentMemberId()
                val likedStatusMap = productLikeService.getLikedStatus(productIds, currentMemberId)

                // 4. ExhibitionProductDto 구성
                exhibition.exhibitionProducts
                    .sortedBy { it.sortOrder }
                    .map { ep ->
                        val product = products.firstOrNull { p -> p.id == ep.product!!.id }
                            ?: throw CustomException(ErrorCode.PRODUCT_NOT_FOUND)

                        val images = imageMap.getOrDefault(product.id, emptyList())
                        val likeCount = likeCountMap.getOrDefault(product.id, 0L)
                        val isLiked = likedStatusMap.getOrDefault(product.id, false)

                        ExhibitionProductDto.from(product, images, ep.sortOrder, likeCount, isLiked)
                    }
            }
        }

        return exhibitions.map { exhibition ->
            ExhibitionFindOneResponse.from(exhibition, productMap[exhibition.id] ?: emptyList())
        }
    }

    @Transactional(readOnly = true)
    fun getExhibitionById(id: Long): ExhibitionFindOneResponse {
        val exhibition = findById(id)

        // 1. 연결된 상품 ID 목록 추출
        val products = exhibition.exhibitionProducts.map { it.product!! }
        val productIds = products.map { it.id!! }

        // 2. 이미지들 일괄 조회
        val images = imageRepository.fetchProductImagesByProductIds(productIds)

        // 3. 좋아요 정보 조회
        val likeCountMap = productLikeService.getLikeCounts(productIds)
        val currentMemberId = getCurrentMemberId()
        val likedStatusMap = productLikeService.getLikedStatus(productIds, currentMemberId)

        // 4. ExhibitionProductDto 생성
        val exhibitionProductDtos = exhibition.exhibitionProducts.map { exhibitionProduct ->
            val product = exhibitionProduct.product!!
            val sortOrder = exhibitionProduct.sortOrder

            val productImages = images.filter { img -> img.referenceId == product.id }

            val likeCount = likeCountMap.getOrDefault(product.id, 0L)
            val isLiked = likedStatusMap.getOrDefault(product.id, false)

            ExhibitionProductDto.from(product, productImages, sortOrder, likeCount, isLiked)
        }

        // 4. Response 반환
        return ExhibitionFindOneResponse.from(exhibition, exhibitionProductDtos)
    }

    fun updateExhibition(exhibitionId: Long, request: ExhibitionUpdateRequest) {
        val findExhibition = findById(exhibitionId)

        exhibitionRepository.deleteProductByExhibitionId(findExhibition.id!!)

        val exhibition = findById(exhibitionId)

        exhibition.updateExhibition(
            request.title!!,
            request.description,
            request.visibility,
            request.startAt!!,
            request.endAt
        )

        if (request.productIds?.isNotEmpty() == true) {
            makeExhibitionProducts(request.productIds, exhibition)
        }
    }

    fun deleteExhibition(exhibitionId: Long) {
        val findExhibition = findById(exhibitionId)
        exhibitionRepository.deleteById(findExhibition.id!!)
    }

    private fun findById(exhibitionId: Long): Exhibition {
        return exhibitionRepository.findById(exhibitionId).orElseThrow {
            CustomException(ErrorCode.EXHIBITION_NOT_FOUND)
        }
    }

    private fun makeExhibitionProducts(request: List<Long>, exhibition: Exhibition) {
        val products = productUtil.findAllProductByProductIdIn(request)

        val exhibitionProducts = products.mapIndexed { i, product ->
            ExhibitionProduct.createExhibitionProduct(exhibition, product, i + 1)
        }

        exhibition.addProducts(exhibitionProducts)
    }

    private fun getCurrentMemberId(): Long? {
        return try {
            securityUtil.getCurrentMemberId()
        } catch (e: Exception) {
            null // 비회원인 경우
        }
    }
}
