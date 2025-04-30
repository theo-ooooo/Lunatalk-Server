package kr.co.lunatalk.domain.image.repository;

import kr.co.lunatalk.domain.image.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long>, ImageRepositoryCustom {
	Optional<Image> findByImageKey(String imageKey);
}
