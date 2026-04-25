package br.com.transportae.itinerarioStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TipoItinerarioStatus {
    EM_ESPERA(false),
    INICIADO(false),
    EM_ATRASO(false),
    CANCELADO(true),
    ACIDENTE(true),
    CONCLUIDO(true);

    private final boolean isConcluido;
}
