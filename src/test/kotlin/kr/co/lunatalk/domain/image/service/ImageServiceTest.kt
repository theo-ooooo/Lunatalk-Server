package kr.co.lunatalk.domain.image.service

import kr.co.lunatalk.domain.image.domain.Image
import kr.co.lunatalk.domain.image.domain.ImageFileExtension
import kr.co.lunatalk.domain.image.domain.ImageStatus
import kr.co.lunatalk.domain.image.domain.ImageType
import kr.co.lunatalk.domain.image.dto.request.ProductImageCompletedRequest
import kr.co.lunatalk.domain.image.dto.request.ProductImageUploadRequest
import kr.co.lunatalk.domain.image.repository.ImageRepository
import kr.co.lunatalk.domain.product.domain.Product
import kr.co.lunatalk.domain.product.domain.ProductStatus
import kr.co.lunatalk.domain.product.domain.ProductVisibility
import kr.co.lunatalk.domain.product.repository.ProductRepository
import kr.co.lunatalk.global.exception.CustomException
import kr.co.lunatalk.global.exception.ErrorCode
import kr.co.lunatalk.global.util.SpringEnvironmentUtil
import kr.co.lunatalk.infra.config.s3.S3Properties
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.*
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.net.URL
import java.util.*

@ExtendWith(MockitoExtension::class)
class ImageServiceTest {

    @InjectMocks
    private lateinit var imageService: ImageService

    @Mock
    private lateinit var imageRepository: ImageRepository

    @Mock
    private lateinit var productRepository: ProductRepository

    @Mock
    private lateinit var s3Client: S3Client

    @Mock
    private lateinit var s3Presigner: S3Presigner

    @Mock
    private lateinit var s3Properties: S3Properties

    @Mock
    private lateinit var springEnvironmentUtil: SpringEnvironmentUtil

    private lateinit var testProduct: Product

    @BeforeEach
    fun setUp() {
        testProduct = Product.createProduct("테스트", 10000L, 100, ProductStatus.ACTIVE, ProductVisibility.VISIBLE)
    }

    @Test
    fun `상품 이미지 PresignedUrl 생성`() {
        val request = ProductImageUploadRequest(1L, ImageType.PRODUCT_THUMBNAIL, ImageFileExtension.PNG)
        given(productRepository.findById(anyLong())).willReturn(Optional.of(testProduct))

        val fakeUrl = mock(URL::class.java)
        given(fakeUrl.toString()).willReturn("http://fake-presigned-url")
        val presignedRequest = mock(PresignedPutObjectRequest::class.java)
        given(presignedRequest.url()).willReturn(fakeUrl)
        given(s3Presigner.presignPutObject(any(PutObjectPresignRequest::class.java))).willReturn(presignedRequest)

        val result = imageService.productImageUpload(request)

        assertThat(result.presignedUrl).isEqualTo("http://fake-presigned-url")
        verify(productRepository).findById(1L)
        verify(imageRepository).save(any(Image::class.java))
    }

    @Test
    fun `없는 상품으로 PresignedUrl 생성 예외`() {
        val request = ProductImageUploadRequest(999L, ImageType.PRODUCT_THUMBNAIL, ImageFileExtension.PNG)
        given(productRepository.findById(anyLong())).willReturn(Optional.empty())

        assertThatThrownBy { imageService.productImageUpload(request) }
            .isInstanceOf(CustomException::class.java)
            .hasMessage(ErrorCode.PRODUCT_NOT_FOUND.message)
        verify(productRepository).findById(999L)
    }

    @Test
    fun `상품 이미지 업로드 완료처리`() {
        val image = Image.createImage(ImageType.PRODUCT_THUMBNAIL, 1L, "image-key", "path", ImageFileExtension.PNG, 1)
        given(imageRepository.findByImageKey("image-key")).willReturn(Optional.of(image))

        imageService.productImageCompleteUpload(ProductImageCompletedRequest("image-key"))

        assertThat(image.imageStatus).isEqualTo(ImageStatus.COMPLETED)
        verify(imageRepository).findByImageKey("image-key")
    }

    @Test
    fun `없는 이미지 업로드 완료처리 예외`() {
        given(imageRepository.findByImageKey("invalid-key")).willReturn(Optional.empty())

        assertThatThrownBy { imageService.productImageCompleteUpload(ProductImageCompletedRequest("invalid-key")) }
            .isInstanceOf(CustomException::class.java)
            .hasMessage(ErrorCode.IMAGE_NOT_FOUND.message)
        verify(imageRepository).findByImageKey("invalid-key")
    }

    @Test
    fun `이미지 삭제 성공`() {
        val image = Image.createImage(
            ImageType.PRODUCT_THUMBNAIL, 1L, "image-key",
            "local/product/1/image-key.png", ImageFileExtension.PNG, 1
        )
        given(imageRepository.findByImageKey("image-key")).willReturn(Optional.of(image))

        imageService.deleteByImageKey("image-key")

        assertThat(image.imageStatus).isEqualTo(ImageStatus.DELETED)
        verify(imageRepository).findByImageKey("image-key")
        verify(s3Client).deleteObject(any(DeleteObjectRequest::class.java))
    }

    @Test
    fun `삭제된 이미지 다시 삭제시 예외`() {
        val image = Image.createImage(
            ImageType.PRODUCT_THUMBNAIL, 1L, "image-key",
            "local/product/1/image-key.png", ImageFileExtension.PNG, 1
        )
        image.deletedImage()
        given(imageRepository.findByImageKey("deleted-key")).willReturn(Optional.of(image))

        assertThatThrownBy { imageService.deleteByImageKey("deleted-key") }
            .isInstanceOf(CustomException::class.java)
            .hasMessage(ErrorCode.IMAGE_EXISTS_DELETED.message)
        verify(imageRepository).findByImageKey("deleted-key")
    }
}
