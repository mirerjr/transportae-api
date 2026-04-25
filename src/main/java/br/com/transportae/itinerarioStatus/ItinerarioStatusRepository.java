package br.com.transportae.itinerarioStatus;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ItinerarioStatusRepository extends JpaRepository<ItinerarioStatusModel, Long> {

    List<ItinerarioStatusModel> findAllByItinerarioId(Long itinerarioId);

    Optional<ItinerarioStatusModel> findFirstByItinerarioIdOrderByDataDesc(Long itinerarioId);
}
