package br.com.transportae.usuario;

import java.security.Principal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.transportae.itinerario.ItinerarioService;
import br.com.transportae.linhaTransporte.LinhaTransporteService;
import br.com.transportae.usuarioLinha.UsuarioLinhaModel;
import br.com.transportae.usuarioLinha.UsuarioLinhaService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController()
@RequestMapping("/api/v1/usuarios")
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class UsuarioController {
    
    private final UsuarioService usuarioService;
    private final UsuarioLinhaService usuarioLinhaService;
    private final LinhaTransporteService linhaTransporteService;
    private final ItinerarioService itinerarioService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UsuarioDto> cadastrar(@Valid @RequestBody UsuarioDto usuarioDto) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(usuarioService.cadastrarUsuario(usuarioDto));
    }

    @GetMapping
    public ResponseEntity<Page<UsuarioDto>> listar(
        @PageableDefault(page = 0, size = 10, sort =  "id", direction = Direction.DESC) Pageable pageable,
        @RequestParam(name = "search", defaultValue = "") String pesquisa
    ){
        return ResponseEntity.ok(usuarioService.listar(pageable, pesquisa));
    }

    // TODO: Flexibilizar para listar por tipo de usuário
    @GetMapping("/motoristas")
    public ResponseEntity<?> listarMotoristas() {
        List<UsuarioModel> motoristas = usuarioService.listarMotoristas();

        return ResponseEntity.ok()
            .body(motoristas.stream()
                .map(usuarioService::converterDomainParaDto)
                .toList()
        );
    }
    

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDto> exibir(@PathVariable Long id) throws EntityNotFoundException {
        UsuarioModel usuario = usuarioService.getUsuario(id);

        return ResponseEntity.ok()
            .body(usuarioService.converterDomainParaDto(usuario));
    }

    @GetMapping("/{id}/linhas")
    public ResponseEntity<?> listarLinhasPorUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioLinhaService
            .listarLinhasPorUsuario(id).stream()
            .map(UsuarioLinhaModel::getLinhaTransporte)
            .map(linhaTransporteService::converterDomainParaDto)
            .map(linhaDto -> {
                linhaDto.setTotalUsuarios(usuarioLinhaService.contarUsuariosPorLinha(linhaDto.getId()));
                return linhaDto;
            })
            .toList()            
        );
    }

    @PutMapping("/{id}/linhas/{idLinha}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> vincularLinha(@PathVariable Long id, @PathVariable Long idLinha) {
        UsuarioModel usuario = usuarioService.getUsuario(id);
        return ResponseEntity.ok(usuarioLinhaService.vincularUsuarioLinha(usuario, idLinha));
    }

    @DeleteMapping("/{id}/linhas/{idLinha}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> desvincularLinha(@PathVariable Long id, @PathVariable Long idLinha) {
        UsuarioModel usuario = usuarioService.getUsuario(id);
        usuarioLinhaService.desvincularUsuarioLinha(usuario, idLinha);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/logado")
    public ResponseEntity<UsuarioDto> getUsuarioLogado(Principal principal) {
        UsuarioModel usuarioLogado = usuarioService.getUsuarioLogado(principal);
        return ResponseEntity.ok(usuarioService.converterDomainParaDto(usuarioLogado));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Object> atualizar(@PathVariable Long id, @Valid @RequestBody UsuarioDto usuario) {
        return ResponseEntity
            .ok()
            .body(usuarioService.atualizarUsuario(id, usuario));
    }

    @PatchMapping("/{id}/acesso")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Object> liberarAcesso(@PathVariable Long id) {
        usuarioService.liberarAcessoUsuario(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/ativar")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Object> ativar(@PathVariable Long id) {
        usuarioService.liberarAcessoUsuario(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/itinerarios")
    public ResponseEntity<?> listarItinerarios(
        @PathVariable Long id,
        @PageableDefault(page = 0, size = 10, sort = "dataCadastro", direction = Direction.DESC) Pageable pageable,
        @RequestParam(name = "search", defaultValue = "") String pesquisa

    ) {      
        return ResponseEntity.ok(itinerarioService
            .listarItinerariosPorUsuario(pageable, id, pesquisa)
            .map(itinerarioService::converterDomainParaDto).toList());
    }
}
