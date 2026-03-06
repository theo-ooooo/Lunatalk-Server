package kr.co.lunatalk.global.util

import kr.co.lunatalk.domain.image.domain.Image
import kr.co.lunatalk.domain.image.repository.ImageRepository
import kr.co.lunatalk.domain.product.domain.Product
import kr.co.lunatalk.domain.product.dto.ProductWithImagesResult
import kr.co.lunatalk.domain.product.repository.ProductRepository
import kr.co.lunatalk.global.exception.CustomException
import kr.co.lunatalk.global.exception.ErrorCode
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ProductUtil(
    private val productRepository: ProductRepository,
    private val imageRepository: ImageRepository
) {

    @Transactional
    fun findProductId(productId: Long): Product =
        productRepository.findProductById(productId).orElseThrow {
            CustomException(ErrorCode.PRODUCT_NOT_FOUND)
        }

    @Transactional
    fun findAllProductByProductIdIn(productIds: List<Long>): List<Product> =
        productRepository.findAllProductsByProductIds(productIds)

    @Transactional(readOnly = true)
    fun findAllProducts(productIds: List<Long>): ProductWithImagesResult {
        val products = productRepository.findAllProductDtoByIdsWithJoin(productIds)

        if (products.isEmpty()) {
            return ProductWithImagesResult(emptyList(), emptyMap())
        }

        val images = imageRepository.fetchProductImagesByProductIds(productIds)

        val imageMap: Map<Long, List<Image>> = images.groupBy { it.referenceId!! }

        return ProductWithImagesResult(products, imageMap)
    }
}
