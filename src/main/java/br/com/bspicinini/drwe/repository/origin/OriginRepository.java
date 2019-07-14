package br.com.bspicinini.drwe.repository.origin;

import br.com.bspicinini.drwe.model.origin.UserOrigin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OriginRepository extends JpaRepository<UserOrigin, Long> {

    Optional<UserOrigin> findById(Long id);

}