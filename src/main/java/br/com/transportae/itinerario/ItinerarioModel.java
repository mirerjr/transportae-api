package br.com.transportae.itinerario;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import br.com.transportae.itinerarioPonto.ItinerarioPontoModel;
import br.com.transportae.itinerarioStatus.ItinerarioStatusModel;
import br.com.transportae.itinerarioStatus.TipoItinerarioStatus;
import br.com.transportae.linhaTransporte.LinhaTransporteModel;
import br.com.transportae.usuario.UsuarioModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity(name = "itinerario")
@AllArgsConstructor
@NoArgsConstructor
public class ItinerarioModel {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(length = 10)
    private String codigoVeiculo;

    @Enumerated(EnumType.STRING)
    TipoItinerario tipoItinerario;

    @CreationTimestamp
    private LocalDateTime dataCadastro;

    @UpdateTimestamp
    private LocalDateTime dataAtualizacao;

    @ManyToOne
    private UsuarioModel motorista;

    @Enumerated(EnumType.STRING)
    private TipoItinerarioStatus ultimoStatus;

    @ManyToOne
    private LinhaTransporteModel linhaTransporte;

    @OneToMany(mappedBy = "itinerario")
    private List<ItinerarioStatusModel> status;

    @OneToMany(mappedBy = "itinerario")
    private List<ItinerarioPontoModel> pontosItinerario;
}
