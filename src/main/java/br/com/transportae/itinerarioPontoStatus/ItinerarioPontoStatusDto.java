package br.com.transportae.itinerarioPontoStatus;

import java.time.LocalDateTime;

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
public class ItinerarioPontoStatusDto {

    private Long id;

    @Enumerated(EnumType.STRING)
    private TipoItinerarioPontoStatus status;

    private Long itinerarioPontoId;

    private UsuarioDto usuario;
    
    private Long usuarioId;
    private String usuarioNome;
    private LocalDateTime dataCadastro;
    private LocalDateTime dataAtualizacao;
}
