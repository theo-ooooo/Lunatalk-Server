package kr.co.lunatalk.domain.image.service

import kr.co.lunatalk.domain.image.domain.Image
import kr.co.lunatalk.domain.image.domain.ImageFileExtension
import kr.co.lunatalk.domain.image.domain.ImageStatus
import kr.co.lunatalk.domain.image.domain.ImageType
import kr.co.lunatalk.domain.image.dto.request.ProductImageCompletedRequest
import kr.co.lunatalk.domain.image.dto.request.ProductImageUploadRequest
import kr.co.lunatalk.domain.image.dto.response.PresignedUrlResponse
import kr.co.lunatalk.domain.image.repository.ImageRepository
import kr.co.lunatalk.domain.product.domain.Product
import kr.co.lunatalk.domain.product.repository.ProductRepository
import kr.co.lunatalk.global.exception.CustomException
import kr.co.lunatalk.global.exception.ErrorCode
import kr.co.lunatalk.global.util.SpringEnvironmentUtil
import kr.co.lunatalk.infra.config.s3.S3Properties
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.net.URL
import java.time.Duration
import java.util.UUID

@Service
@Transactional
class ImageService(
    private val imageRepository: ImageRepository,
    private val productRepository: ProductRepository,
    private val s3Client: S3Client,
    private val s3Presigner: S3Presigner,
    private val s3Properties: S3Properties,
    private val springEnvironmentUtil: SpringEnvironmentUtil
) {

    fun productImageUpload(request: ProductImageUploadRequest): PresignedUrlResponse {
        val product = findProductByProductId(request.productId)

        val list = imageRepository.findAllByReferenceIdAndImageType(request.productId, request.imageType)

        val order = if (list.isEmpty()) 1 else list.size + 1

        val imageKey = createImageKey()
        val imagePath = createImagePath(request.imageType, product.id!!, imageKey, request.imageFileExtension)

        val url = generatePresignedUrl(imagePath).toString()

        val image = Image.createImage(request.imageType, product.id!!, imageKey, imagePath, request.imageFileExtension, order)
        imageRepository.save(image)

        return PresignedUrlResponse.of(url, image.imageKey!!)
    }

    fun productImageCompleteUpload(request: ProductImageCompletedRequest) {
        val findImage = findImageByImageKey(request.imageKey)
        findImage.uploadedImage()
    }

    fun deleteByImageKey(imageKey: String) {
        val findImage = findImageByImageKey(imageKey)

        if (findImage.imageStatus == ImageStatus.DELETED) {
            throw CustomException(ErrorCode.IMAGE_EXISTS_DELETED)
        }

        findImage.deletedImage()

        // 프로덕션 환경이면, S3에 이미지 지우지 않는다.
        if (!springEnvironmentUtil.isProdProfile()) {
            deleteObject(findImage)
        }
    }

    private fun deleteObject(findImage: Image) {
        val builder = DeleteObjectRequest.builder()
            .bucket(s3Properties.bucket)
            .key(findImage.imagePath)
            .build()
        s3Client.deleteObject(builder)
    }

    private fun findImageByImageKey(imageKey: String): Image {
        return imageRepository.findByImageKey(imageKey)
            .orElseThrow { CustomException(ErrorCode.IMAGE_NOT_FOUND) }
    }

    private fun findProductByProductId(productId: Long): Product {
        return productRepository.findById(productId)
            .orElseThrow { CustomException(ErrorCode.PRODUCT_NOT_FOUND) }
    }

    private fun generatePresignedUrl(imagePath: String): URL {
        val putObjectRequest = PutObjectRequest.builder()
            .bucket(s3Properties.bucket)
            .key(imagePath)
            .build()

        val presignRequest = PutObjectPresignRequest.builder()
            .putObjectRequest(putObjectRequest)
            .signatureDuration(Duration.ofMinutes(10))
            .build()

        return s3Presigner.presignPutObject(presignRequest).url()
    }

    private fun createImageKey(): String {
        return UUID.randomUUID().toString()
    }

    private fun createImagePath(
        imageType: ImageType,
        referenceId: Long,
        imageKey: String,
        imageFileExtension: ImageFileExtension
    ): String {
        return "${springEnvironmentUtil.getCurrentProfile()}/${imageType.name.lowercase()}/$referenceId/$imageKey.${imageFileExtension.uploadExtension}"
    }
}
