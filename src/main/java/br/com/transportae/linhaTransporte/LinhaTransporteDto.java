package br.com.transportae.linhaTransporte;

import java.time.LocalDateTime;

import br.com.transportae.itinerario.ItinerarioDto;
import br.com.transportae.pontoParada.PontoParadaDto;
import br.com.transportae.usuario.UsuarioDto;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LinhaTransporteDto {

    private Long id;
    private boolean ativa;

    @NotBlank(message = "O nome é um campo obrigatório")
    private String nome;

    @NotNull(message = "O turno é um campo obrigatório")
    @Enumerated(EnumType.STRING)
    private Turno turno;

    @Min(value = 3, message = "O total de assentos deve ser maior que 2")
    @NotNull(message = "O total de assentos é um campo obrigatório")
    private Short totalAssentos;
    
    private String codigoVeiculo;
    private Integer totalUsuarios;

    private ItinerarioDto ultimoItinerarioHoje;
    private LocalDateTime horarioProximoItinerario;
    //TODO Retornar quantitativo de pontos vinculados
}