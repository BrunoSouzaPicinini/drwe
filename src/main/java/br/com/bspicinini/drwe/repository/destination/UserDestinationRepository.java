package br.com.bspicinini.drwe.repository.destination;

import br.com.bspicinini.drwe.model.destination.UserDestination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDestinationRepository extends JpaRepository<UserDestination, Long> {

    Optional<UserDestination> findById(Long id);

}