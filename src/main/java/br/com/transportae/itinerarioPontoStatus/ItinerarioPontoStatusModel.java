package br.com.transportae.itinerarioPontoStatus;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import br.com.transportae.itinerarioPonto.ItinerarioPontoModel;
import br.com.transportae.usuario.UsuarioModel;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity(name = "ponto_status")
@AllArgsConstructor
@NoArgsConstructor 
public class ItinerarioPontoStatusModel {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipoItinerarioPontoStatus status;

    @CreationTimestamp
    private LocalDateTime dataCadastro;

    @UpdateTimestamp
    private LocalDateTime dataAtualizacao;
    
    @ManyToOne
    private ItinerarioPontoModel itinerarioPonto;

    @ManyToOne
    private UsuarioModel usuario;
}
