package br.com.transportae.itinerario;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.transportae.itinerarioStatus.TipoItinerarioStatus;
import br.com.transportae.linhaTransporte.LinhaTransporteModel;

public interface ItinerarioRepository extends JpaRepository<ItinerarioModel, Long> {


    @Query("SELECT iti FROM itinerario iti WHERE iti.linhaTransporte.id = ?1 AND EXISTS (SELECT status FROM itinerario_status status WHERE status.itinerario.id = iti.id AND status.tipoItinerarioStatus = ?2) ORDER BY iti.dataCadastro DESC limit 1")
    Optional<ItinerarioModel> findFirstByLinhaTransporteIdAndTipoItinerarioStatus(Long linhaTransporteId, TipoItinerarioStatus tipoItinerarioStatus);

    Page<ItinerarioModel> findAllByCodigoVeiculoContainingIgnoreCase(String pesquisa, Pageable pagina);

    Page<ItinerarioModel> findAllByLinhaTransporteId(Long linhaTransporteId, Pageable pagina);
    Page<ItinerarioModel> findAllByLinhaTransporteIdAndCodigoVeiculoContainingIgnoreCase(Long linhaTransporteId, String pesquisa, Pageable pagina);

    boolean existsByLinhaTransporteAndDataCadastroBetween(LinhaTransporteModel linhaTransporte, LocalDateTime inicioDoDia, LocalDateTime finalDoDia);

    Optional<ItinerarioModel> findFirstByLinhaTransporteOrderByDataCadastroDesc(LinhaTransporteModel linhaTransporte);

    Optional<ItinerarioModel> findFirstByLinhaTransporteAndDataCadastroBetweenOrderByDataCadastroDesc(LinhaTransporteModel linhaTransporte, LocalDateTime inicioDoDia, LocalDateTime finalDoDia);
}
