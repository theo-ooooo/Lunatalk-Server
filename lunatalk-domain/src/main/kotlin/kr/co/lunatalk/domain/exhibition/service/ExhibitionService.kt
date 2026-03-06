package kr.co.lunatalk.domain.exhibition.service

import kr.co.lunatalk.domain.exhibition.domain.Exhibition
import kr.co.lunatalk.domain.exhibition.domain.ExhibitionProduct
import kr.co.lunatalk.domain.exhibition.domain.ExhibitionVisibility
import kr.co.lunatalk.domain.exhibition.dto.ExhibitionProductDto
import kr.co.lunatalk.domain.exhibition.dto.request.ExhibitionCreateRequest
import kr.co.lunatalk.domain.exhibition.dto.request.ExhibitionUpdateRequest
import kr.co.lunatalk.domain.exhibition.dto.response.ExhibitionCreateResponse
import kr.co.lunatalk.domain.exhibition.dto.response.ExhibitionFindOneResponse
import kr.co.lunatalk.domain.exhibition.repository.ExhibitionRepository
import kr.co.lunatalk.domain.productlike.service.ProductLikeService
import kr.co.lunatalk.global.exception.CustomException
import kr.co.lunatalk.global.exception.ErrorCode
import kr.co.lunatalk.global.util.ProductUtil
import kr.co.lunatalk.global.util.SecurityUtil
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class ExhibitionService(
    private val exhibitionRepository: ExhibitionRepository,
    private val productUtil: ProductUtil,
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
        return convertToExhibitionFindOneResponseList(exhibitions)
    }

    @Transactional(readOnly = true)
    fun getActiveExhibitions(): List<ExhibitionFindOneResponse> {
        val now = LocalDateTime.now()
        val exhibitions = exhibitionRepository.findActiveExhibitions(ExhibitionVisibility.VISIBLE, now)
        return convertToExhibitionFindOneResponseList(exhibitions)
    }

    @Transactional(readOnly = true)
    fun getExhibitionById(id: Long): ExhibitionFindOneResponse {
        val exhibition = findById(id)
        val exhibitionProductDtos = buildExhibitionProductDtos(exhibition)
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

    private fun convertToExhibitionFindOneResponseList(exhibitions: List<Exhibition>): List<ExhibitionFindOneResponse> {
        val productMap = exhibitions.associate { exhibition ->
            exhibition.id to buildExhibitionProductDtos(exhibition)
        }

        return exhibitions.map { exhibition ->
            ExhibitionFindOneResponse.from(exhibition, productMap[exhibition.id] ?: emptyList())
        }
    }

    private fun buildExhibitionProductDtos(exhibition: Exhibition): List<ExhibitionProductDto> {
        val productIds = exhibition.exhibitionProducts.map { ep -> ep.product!!.id!! }

        val productWithImages = productUtil.findAllProducts(productIds)
        val products = productWithImages.products
        val imageMap = productWithImages.imageMap

        val likeCountMap = productLikeService.getLikeCounts(productIds)
        val currentMemberId = getCurrentMemberId()
        val likedStatusMap = productLikeService.getLikedStatus(productIds, currentMemberId)

        return exhibition.exhibitionProducts
            .sortedBy { it.sortOrder }
            .mapNotNull { ep ->
                val product = products.firstOrNull { p -> p.id == ep.product!!.id }
                    ?: return@mapNotNull null

                val images = imageMap.getOrDefault(product.id, emptyList())
                val likeCount = likeCountMap.getOrDefault(product.id, 0L)
                val isLiked = likedStatusMap.getOrDefault(product.id, false)

                ExhibitionProductDto.from(product, images, ep.sortOrder, likeCount, isLiked)
            }
    }

    private fun getCurrentMemberId(): Long? {
        return try {
            securityUtil.getCurrentMemberId()
        } catch (e: Exception) {
            null
        }
    }
}
