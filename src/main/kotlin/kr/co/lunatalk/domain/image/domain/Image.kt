package kr.co.lunatalk.domain.image.domain

import jakarta.persistence.*
import kr.co.lunatalk.domain.common.domain.BaseTimeEntity

@Entity
open class Image protected constructor() : BaseTimeEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    open var id: Long? = null
        protected set

    @Enumerated(EnumType.STRING)
    open var imageType: ImageType? = null
        protected set

    open var referenceId: Long? = null
        protected set

    @Column(length = 36)
    open var imageKey: String? = null
        protected set

    open var imagePath: String? = null
        protected set

    open var imageOrder: Int? = null
        protected set

    @Enumerated(EnumType.STRING)
    open var imageFileExtension: ImageFileExtension? = null
        protected set

    @Enumerated(EnumType.STRING)
    open var imageStatus: ImageStatus? = null
        protected set

    private constructor(
        imageType: ImageType,
        referenceId: Long,
        imageKey: String,
        imagePath: String,
        imageFileExtension: ImageFileExtension,
        order: Int
    ) : this() {
        this.imageType = imageType
        this.referenceId = referenceId
        this.imageKey = imageKey
        this.imagePath = imagePath
        this.imageFileExtension = imageFileExtension
        this.imageStatus = ImageStatus.PENDING
        this.imageOrder = order
    }

    fun uploadedImage() {
        this.imageStatus = ImageStatus.COMPLETED
    }

    fun deletedImage() {
        this.imageStatus = ImageStatus.DELETED
    }

    companion object {
        fun createImage(
            imageType: ImageType,
            referenceId: Long,
            imageKey: String,
            imagePath: String,
            imageFileExtension: ImageFileExtension,
            order: Int
        ): Image {
            return Image(imageType, referenceId, imageKey, imagePath, imageFileExtension, order)
        }
    }
}
