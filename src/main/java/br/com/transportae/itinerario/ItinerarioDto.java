package br.com.transportae.itinerario;

import java.util.List;

import br.com.transportae.itinerarioPonto.ItinerarioPontoDto;
import br.com.transportae.itinerarioStatus.ItinerarioStatusDto;
import br.com.transportae.itinerarioStatus.TipoItinerarioStatus;
import br.com.transportae.linhaTransporte.LinhaTransporteDto;
import br.com.transportae.usuario.UsuarioDto;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItinerarioDto {

    private Long id;
    private String codigoVeiculo;

    @Enumerated(EnumType.STRING)
    private TipoItinerario tipoItinerario;
    private TipoItinerarioStatus ultimoStatus;
    
    private List<ItinerarioStatusDto> itinerarioStatus;
    private List<ItinerarioPontoDto> itinerarioPonto;
    private LinhaTransporteDto linhaTransporte;
    private UsuarioDto motorista;

    private Long linhaTransporteId;
}
