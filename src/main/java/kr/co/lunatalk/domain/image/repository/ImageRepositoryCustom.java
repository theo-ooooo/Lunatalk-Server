package kr.co.lunatalk.domain.image.repository;

import kr.co.lunatalk.domain.image.domain.Image;

import java.util.List;

public interface ImageRepositoryCustom {
    List<Image> fetchProductImagesByProductId(Long productId);
    List<Image> fetchProductImagesByProductIds(List<Long> productIds);

}
