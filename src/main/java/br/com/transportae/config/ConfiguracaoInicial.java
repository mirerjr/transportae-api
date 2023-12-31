package br.com.transportae.config;

import java.util.List;

import org.springframework.stereotype.Component;

import br.com.transportae.instituicao.InstituicaoModel;
import br.com.transportae.instituicao.InstituicaoService;
import br.com.transportae.linhaTransporte.LinhaTransporteModel;
import br.com.transportae.linhaTransporte.LinhaTransporteService;
import br.com.transportae.pontoParada.PontoParadaModel;
import br.com.transportae.pontoParada.PontoParadaService;
import br.com.transportae.usuario.Perfil;
import br.com.transportae.usuario.UsuarioModel;
import br.com.transportae.usuario.UsuarioService;
import br.com.transportae.usuarioLinha.UsuarioLinhaService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;

@Component
@RequiredArgsConstructor
public class ConfiguracaoInicial {

    private final UsuarioService usuarioService;
    private final InstituicaoService instituicaoService;
    private final LinhaTransporteService linhaTransporteService;
    private final UsuarioLinhaService usuarioLinhaService;
    private final PontoParadaService pontoParadaService;

    @PostConstruct
    public void iniciar() {
        if (!usuarioService.hasUsuarioAdmin()) {
            usuarioService.cadastrarUsuarioAdmin();
            
            UsuarioModel aluno = usuarioService.cadastrarUsuarioAluno();
            UsuarioModel motorista = usuarioService.cadastrarUsuarioMotorista();

            List<UsuarioModel> usuarios = usuarioService.cadastrarUsuarioMock(20);
            List<InstituicaoModel> instituicoes = instituicaoService.cadastrarInstituicaoMock(5);
            List<LinhaTransporteModel> linhas = linhaTransporteService.cadastrarLinhaTransporteMock(3);          

            Faker faker = new Faker();

            linhas.forEach(linha -> {
                usuarioLinhaService.vincularUsuarioLinha(aluno, linha.getId());
                usuarioLinhaService.vincularUsuarioLinha(motorista, linha.getId());

                List<PontoParadaModel> pontosParada = pontoParadaService.cadastrarPontoParadaMock(faker.number().numberBetween(5, 10));
                pontosParada.forEach(pontoParada -> pontoParadaService.vincularLinhaTransporte(pontoParada, linha));
            });

            usuarios.forEach(usuario -> {
                LinhaTransporteModel linha = linhas.get(faker.number().numberBetween(0, linhas.size()));
                usuarioLinhaService.vincularUsuarioLinha(usuario, linha.getId());

                if (usuario.getPerfil().equals(Perfil.ALUNO)) {
                    InstituicaoModel instituicao = instituicoes.get(faker.number().numberBetween(0, instituicoes.size()));
                    usuarioService.atualizarInstituicao(usuario, instituicao);
                }
            });
        }
    }
}
