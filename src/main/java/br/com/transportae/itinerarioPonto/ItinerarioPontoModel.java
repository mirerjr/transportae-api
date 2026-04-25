package br.com.transportae.itinerarioPonto;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import br.com.transportae.itinerarioPontoStatus.ItinerarioPontoStatusModel;
import br.com.transportae.itinerario.ItinerarioModel;
import br.com.transportae.pontoParada.PontoParadaModel;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity(name = "itinerario_ponto")
@NoArgsConstructor
@AllArgsConstructor
public class ItinerarioPontoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @CreationTimestamp
    private LocalDateTime dataCadastro;

    @UpdateTimestamp
    private LocalDateTime dataAtualizacao;

    @ManyToOne
    private ItinerarioModel itinerario;

    @ManyToOne
    private PontoParadaModel pontoParada;

    @OneToMany(mappedBy = "itinerarioPonto")
    List<ItinerarioPontoStatusModel> pontoStatus;
}
