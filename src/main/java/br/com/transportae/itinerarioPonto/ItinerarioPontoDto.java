package br.com.transportae.itinerarioPonto;

import java.util.List;

import br.com.transportae.itinerarioPontoStatus.ItinerarioPontoStatusDto;
import br.com.transportae.pontoParada.PontoParadaDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItinerarioPontoDto {
    
    private Long id;
    private Long itinerarioId;
    private Long pontoParadaId;

    private PontoParadaDto pontoParada;
    List<ItinerarioPontoStatusDto> itinerarioPontoStatus;
}
