package kr.co.lunatalk.domain.auth.repository;

import kr.co.lunatalk.domain.auth.domain.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshRepository extends CrudRepository<RefreshToken, Long> {

}
