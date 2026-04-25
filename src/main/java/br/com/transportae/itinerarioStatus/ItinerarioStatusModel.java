package br.com.transportae.itinerarioStatus;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import br.com.transportae.itinerario.ItinerarioModel;
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
@Entity(name = "itinerario_status")
@AllArgsConstructor
@NoArgsConstructor
public class ItinerarioStatusModel {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipoItinerarioStatus tipoItinerarioStatus;

    //TODO: trocar o status para em atraso caso se atrase em um dos pontos, e requisitar/pedir justificaiva
    private String mensagem;
    
    @CreationTimestamp
    private LocalDateTime data;

    @ManyToOne
    private ItinerarioModel itinerario;
}
