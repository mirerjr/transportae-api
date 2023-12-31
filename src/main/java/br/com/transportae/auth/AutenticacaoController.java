package br.com.transportae.auth;

import java.net.URI;
import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AutenticacaoController {
    
    private final AutenticacaoService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<AutenticacaoResponse> logar(@Valid @RequestBody LoginRequest request) {
        AutenticacaoResponse autenticacaoResponse = authenticationService.logar(request);

        if (autenticacaoResponse.isPrimeiroAcesso()) {
            URI uriAlteracaoSenha = URI.create("/api/v1/usuario/senha");

            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .location(uriAlteracaoSenha)
                .body(autenticacaoResponse);
        }
                   
        return ResponseEntity.ok(autenticacaoResponse);
    }

    @PatchMapping("/senha")
    public ResponseEntity<Object> alterarSenha(
        @Valid @RequestBody AlterarSenhaRequest request,
        Principal principal
    ) {
        authenticationService.alterarSenha(request, principal);
        return ResponseEntity.ok().build();
    }
}
