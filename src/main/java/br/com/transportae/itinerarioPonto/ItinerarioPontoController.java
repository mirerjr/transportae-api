package br.com.transportae.itinerarioPonto;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.transportae.itinerarioPontoStatus.ItinerarioPontoStatusDto;
import br.com.transportae.itinerarioPontoStatus.ItinerarioPontoStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/itinerario-ponto")
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class ItinerarioPontoController {

    private final ItinerarioPontoService itinerarioPontoService;
    private final ItinerarioPontoStatusService itinerarioPontoStatusService;

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('MOTORISTA')")
    public ResponseEntity<ItinerarioPontoStatusDto> atualizarStatusItinerarioPonto(
        @PathVariable Long id, 
        @RequestBody @Valid ItinerarioPontoStatusDto itinerarioPontoStatusDto
    ) {

        return ResponseEntity.ok(itinerarioPontoService.adicionarPontoStatus(id, itinerarioPontoStatusDto));
    }

    @PutMapping("/{id}/aluno/status")
    public ResponseEntity<ItinerarioPontoStatusDto> alterarStatusPontoAluno(
        @PathVariable Long id, 
        @RequestBody @Valid ItinerarioPontoStatusDto itinerarioPontoStatusDto
    ) {
        return ResponseEntity.ok(itinerarioPontoService.alterarStatusPontoAluno(id, itinerarioPontoStatusDto));
    }

    @GetMapping("/{id}/alunos")
    public ResponseEntity<?> getAlunosConfirmados(@PathVariable Long id) {
        return ResponseEntity.ok(itinerarioPontoService
            .getAlunosConfirmados(id).stream()
            .map(itinerarioPontoStatusService::converterDomainParaDto)
            .toList());
    }
}
