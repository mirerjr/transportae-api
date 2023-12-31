package br.com.transportae.auth;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.transportae.config.JwtService;
import br.com.transportae.usuario.UsuarioRepository;
import br.com.transportae.usuario.UsuarioModel;
import br.com.transportae.usuario.exceptions.UsuarioExistenteException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AutenticacaoService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    
    public AutenticacaoResponse logar(LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getSenha()
            )
        );

        UsuarioModel usuarioEncontrado = usuarioRepository
            .findByEmail(request.getEmail())
            .orElseThrow(() -> new UsuarioExistenteException("Usuário não encontrado"));

        Map<String, Object> claims = Map.of("id", usuarioEncontrado.getId());
        String jwtToken = jwtService.gerarToken(usuarioEncontrado, claims);

        if (!usuarioEncontrado.isEmailVerificado()) {
            return AutenticacaoResponse.builder()
                .isPrimeiroAcesso(true)
                .token(jwtToken)
                .build();
        }

        return AutenticacaoResponse.builder()
            .token(jwtToken)
            .build();
    }

    public void alterarSenha(AlterarSenhaRequest request, Principal principal) {
        UsuarioModel usuarioLogado = getUsuarioLogado(principal);

        Boolean isSenhaAtualCorreta = passwordEncoder.matches(request.getSenhaAtual(), usuarioLogado.getSenha());
        Boolean isSenhaNovaConfirmada = request.getSenhaNova().equals(request.getSenhaNovaConfirmada());

        if (!isSenhaAtualCorreta) {
            throw new IllegalStateException("Senha atual incorreta");
        }
        
        if (!isSenhaNovaConfirmada) {
            throw new IllegalStateException("As senhas não conferem");
        }

        if (Objects.isNull(usuarioLogado.getDataPrimeiroAcesso())) {
            usuarioLogado.setDataPrimeiroAcesso(LocalDateTime.now());
        }

        if (!usuarioLogado.isEmailVerificado()) {
            usuarioLogado.setEmailVerificado(true);
        }

        usuarioLogado.setSenha(passwordEncoder.encode(request.getSenhaNova()));
        usuarioRepository.save(usuarioLogado);
    }

    public UsuarioModel getUsuarioLogado (Principal principal) {
        var usuarioPrincipal = ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        UsuarioModel usuarioLogado = (UsuarioModel) usuarioPrincipal;

        return usuarioLogado;
    }    
}
