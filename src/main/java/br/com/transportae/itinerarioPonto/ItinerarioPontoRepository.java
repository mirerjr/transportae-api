package br.com.transportae.itinerarioPonto;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ItinerarioPontoRepository extends JpaRepository<ItinerarioPontoModel, Long> {

    List<ItinerarioPontoModel> findAllByItinerarioIdAndPontoParadaId(Long itinerarioId, Long pontoParadaId);
}
