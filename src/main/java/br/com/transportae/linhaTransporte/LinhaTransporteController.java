package br.com.transportae.linhaTransporte;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

import br.com.transportae.itinerarioStatus.TipoItinerarioStatus;
import br.com.transportae.itinerario.ItinerarioModel;
import br.com.transportae.itinerario.ItinerarioService;
import br.com.transportae.pontoParada.PontoParadaService;
import br.com.transportae.usuario.Perfil;
import br.com.transportae.usuario.UsuarioService;
import br.com.transportae.usuarioLinha.UsuarioLinhaModel;
import br.com.transportae.usuarioLinha.UsuarioLinhaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/v1/linhas")
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class LinhaTransporteController {

    private final LinhaTransporteService linhaTransporteService;
    private final UsuarioLinhaService usuarioLinhaService;
    private final PontoParadaService pontoParadaService;
    private final ItinerarioService itinerarioService;
    private final UsuarioService usuarioService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<LinhaTransporteDto> cadastrar(@RequestBody @Valid LinhaTransporteDto linhaTransporteDto) {
        LinhaTransporteModel linhaTransporte = linhaTransporteService.cadastrarLinhaTransporte(linhaTransporteDto);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(linhaTransporteService.converterDomainParaDto(linhaTransporte));
    }

    @GetMapping
    public ResponseEntity<Page<LinhaTransporteDto>> listar(
        @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.DESC) Pageable pageable,
        @RequestParam(name = "search", defaultValue = "") String pesquisa,
        @RequestParam(name = "turno", required = false) Turno turno
    ) {

        Page<LinhaTransporteModel> linhasTransporte = Objects.nonNull(turno)
            ? linhaTransporteService.listarLinhasTransportePorTurno(pageable, turno, pesquisa)
            : linhaTransporteService.listarLinhasTransporte(pageable, pesquisa);

        return ResponseEntity.ok(linhasTransporte
            .map(linhaTransporteService::converterDomainParaDto)
            .map(linhaDto -> {
                linhaDto.setTotalUsuarios(usuarioLinhaService.contarUsuariosPorLinha(linhaDto.getId()));
                return linhaDto;
            }));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LinhaTransporteDto> exibir(@PathVariable Long id) {
        LinhaTransporteModel linhaTransporte = linhaTransporteService.getLinhaTransporte(id);
        LinhaTransporteDto linhaDto = linhaTransporteService.converterDomainParaDto(linhaTransporte);

        linhaDto.setTotalUsuarios(usuarioLinhaService.contarUsuariosPorLinha(linhaDto.getId()));
        
        return ResponseEntity.ok(linhaDto);
    }

    @GetMapping("/{id}/usuarios")
    public ResponseEntity<?> exibirUsuarios(
        @PathVariable Long id,
        @RequestParam(name = "perfil", defaultValue = "") Perfil perfil
    ) {
        List<UsuarioLinhaModel> usuarios = Objects.nonNull(perfil)
            ? usuarioLinhaService.listarUsuariosPorLinhaEPerfil(id, perfil)
            : usuarioLinhaService.listarUsuariosPorLinha(id);

        return ResponseEntity.ok(usuarios.stream()
            .map(UsuarioLinhaModel::getUsuario)
            .map(usuarioService::converterDomainParaDto)
            .toList());
    }

    @GetMapping("/{id}/pontos-parada")
    public ResponseEntity<?> exibirPontosParada(@PathVariable Long id) {
        return ResponseEntity.ok(pontoParadaService
            .listarPontosParadaPorLinha(id, "ASC").stream()
            .map(pontoParadaService::converterDomainParaDto)
            .toList());
    }

    @GetMapping("/{id}/itinerarios")
    public ResponseEntity<?> listarItinerarios(
        @PathVariable Long id,
        @PageableDefault(page = 0, size = 10, sort = "dataCadastro", direction = Direction.DESC) Pageable pageable,
        @RequestParam(name = "search", defaultValue = "") String pesquisa

    ) {      
        return ResponseEntity.ok(itinerarioService
            .listarItinerariosPorLinha(pageable, id, pesquisa)
            .map(itinerarioService::converterDomainParaDto));
    }
    
    @GetMapping("/{id}/itinerarios/status")
    public ResponseEntity<?> exibirItinerarioPorStatus(
        @PathVariable Long id,
        @RequestParam(name = "status") TipoItinerarioStatus status
    ) {

        ItinerarioModel itinerario = itinerarioService.getItinerarioIniciadoPorLinha(id).orElse(null);

        return Objects.nonNull(itinerario)
            ? ResponseEntity.ok(itinerarioService.converterDomainParaDto(itinerario))
            : ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<LinhaTransporteDto> atualizar(@PathVariable Long id, @RequestBody @Valid LinhaTransporteDto linhaTransporteDto) {
        LinhaTransporteModel linhaTransporte = linhaTransporteService.atualizarLinhaTransporte(id, linhaTransporteDto);
        return ResponseEntity.ok(linhaTransporteService.converterDomainParaDto(linhaTransporte));
    }

    @PostMapping("/{id}/ativar")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<LinhaTransporteDto> ativarLinhaTransporte(@PathVariable Long id) {
        LinhaTransporteModel linhaTransporte = linhaTransporteService.ativarLinhaTransporte(id);
        return ResponseEntity.ok(linhaTransporteService.converterDomainParaDto(linhaTransporte));
    }
}