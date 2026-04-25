package br.com.transportae.itinerarioPontoStatus;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.transportae.itinerarioPonto.ItinerarioPontoModel;
import br.com.transportae.usuario.UsuarioModel;

public interface ItinerarioPontoStatusRepository extends JpaRepository<ItinerarioPontoStatusModel, Long> {

    @Query("SELECT ips FROM ponto_status ips WHERE ips.itinerarioPonto = :itinerarioPonto AND ips.status IN :tipos")
    List<ItinerarioPontoStatusModel> findAllByItinerarioPontoAndUsuarioStatus(
        ItinerarioPontoModel itinerarioPonto,
        List<TipoItinerarioPontoStatus> tipos
    );

    @Query("SELECT ips FROM ponto_status ips WHERE ips.itinerarioPonto = :itinerarioPonto AND ips.usuario = :usuario AND ips.dataCadastro = (SELECT MAX(ips2.dataCadastro) FROM ponto_status ips2 WHERE ips2.itinerarioPonto = :itinerarioPonto AND ips2.usuario = :usuario)")
    ItinerarioPontoStatusModel findLatestByItinerarioPontoAndUsuario(ItinerarioPontoModel itinerarioPonto,UsuarioModel usuario);

    List<ItinerarioPontoStatusModel> findAllByUsuario(UsuarioModel usuario);
}
