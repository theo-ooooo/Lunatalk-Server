package kr.co.lunatalk.domain.image.repository;

import kr.co.lunatalk.domain.image.domain.Image;
import kr.co.lunatalk.domain.image.domain.ImageType;

import java.util.List;

public interface ImageRepositoryCustom {
    List<Image> fetchProductImagesByProductId(Long productId);
    List<Image> fetchProductImagesByProductIds(List<Long> productIds);
	List<Image> findAllByReferenceIdAndImageType(Long referenceId, ImageType imageType);


}
