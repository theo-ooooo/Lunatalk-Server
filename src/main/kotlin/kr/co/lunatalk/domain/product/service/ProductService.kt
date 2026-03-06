package kr.co.lunatalk.domain.product.service

import kr.co.lunatalk.domain.category.domain.CategoryStatus
import kr.co.lunatalk.domain.category.repository.CategoryRepository
import kr.co.lunatalk.domain.image.repository.ImageRepository
import kr.co.lunatalk.domain.product.domain.Product
import kr.co.lunatalk.domain.product.domain.ProductColor
import kr.co.lunatalk.domain.product.domain.ProductStatus
import kr.co.lunatalk.domain.product.dto.FindProductDto
import kr.co.lunatalk.domain.product.dto.request.ProductCreateRequest
import kr.co.lunatalk.domain.product.dto.request.ProductUpdateRequest
import kr.co.lunatalk.domain.product.dto.response.ProductFindResponse
import kr.co.lunatalk.domain.product.repository.ProductRepository
import kr.co.lunatalk.domain.productlike.service.ProductLikeService
import kr.co.lunatalk.global.exception.CustomException
import kr.co.lunatalk.global.exception.ErrorCode
import kr.co.lunatalk.global.util.ProductUtil
import kr.co.lunatalk.global.util.SecurityUtil
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ProductService(
    private val productRepository: ProductRepository,
    private val imageRepository: ImageRepository,
    private val categoryRepository: CategoryRepository,
    private val productUtil: ProductUtil,
    private val productLikeService: ProductLikeService,
    private val securityUtil: SecurityUtil
) {

    fun save(request: ProductCreateRequest): Product {
        val product = Product.createProduct(
            request.name, request.price, request.quantity,
            ProductStatus.ACTIVE, request.visibility
        )

        request.colors.forEach { color ->
            val productColor = ProductColor.createProductColor(product, color)
            product.addProductColor(productColor)
        }

        updateCategory(request.categoryId, product)

        productRepository.save(product)
        return product
    }

    fun update(productId: Long, request: ProductUpdateRequest) {
        val findProduct = productUtil.findProductId(productId)
        findProduct.updateProduct(request)

        updateCategory(request.categoryId, findProduct)
    }

    fun delete(productId: Long) {
        val findProduct = productUtil.findProductId(productId)
        findProduct.deleteProduct()
    }

    @Transactional(readOnly = true)
    fun findProductOne(productId: Long): ProductFindResponse {
        val findProduct = productUtil.findProductId(productId)
            ?: throw CustomException(ErrorCode.PRODUCT_NOT_FOUND)

        val images = imageRepository.fetchProductImagesByProductId(findProduct.id!!)

        val likeCount = productLikeService.getLikeCount(productId)
        val currentMemberId = getCurrentMemberId()
        val isLiked = productLikeService.isLiked(productId, currentMemberId)

        return ProductFindResponse.from(FindProductDto.from(findProduct, images, likeCount, isLiked))
    }

    @Transactional(readOnly = true)
    fun findAllProducts(productIds: List<Long>): List<ProductFindResponse> {
        val allProducts = productUtil.findAllProducts(productIds)

        val likeCountMap = productLikeService.getLikeCounts(productIds)
        val currentMemberId = getCurrentMemberId()
        val likedStatusMap = productLikeService.getLikedStatus(productIds, currentMemberId)

        return allProducts.products.map { product ->
            val productImages = allProducts.imageMap.getOrDefault(product.id, emptyList())
            val likeCount = likeCountMap.getOrDefault(product.id, 0L)
            val isLiked = likedStatusMap.getOrDefault(product.id, false)
            ProductFindResponse.from(FindProductDto.from(product, productImages, likeCount, isLiked))
        }
    }

    private fun updateCategory(categoryId: Long?, product: Product) {
        categoryId ?: return
        categoryRepository.findByIdAndStatus(categoryId, CategoryStatus.ACTIVE).ifPresentOrElse(
            { product.category = it },
            { throw CustomException(ErrorCode.CATEGORY_NOT_FOUND) }
        )
    }

    @Transactional(readOnly = true)
    fun findAll(productName: String?, pageable: Pageable): Page<ProductFindResponse> {
        val products = productRepository.findAll(productName, pageable)

        val productIds = products.map { it.id!! }.toList()

        val images = imageRepository.fetchProductImagesByProductIds(productIds)

        val imageMap = images.groupBy { it.referenceId!! }

        val likeCountMap = productLikeService.getLikeCounts(productIds)
        val currentMemberId = getCurrentMemberId()
        val likedStatusMap = productLikeService.getLikedStatus(productIds, currentMemberId)

        return products.map { product ->
            val productImages = imageMap.getOrDefault(product.id, emptyList())
            val likeCount = likeCountMap.getOrDefault(product.id, 0L)
            val isLiked = likedStatusMap.getOrDefault(product.id, false)
            ProductFindResponse.from(FindProductDto.from(product, productImages, likeCount, isLiked))
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
