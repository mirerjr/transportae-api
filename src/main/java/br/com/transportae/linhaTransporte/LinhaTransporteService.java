package br.com.transportae.linhaTransporte;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.transportae.itinerario.ItinerarioDto;
import br.com.transportae.itinerario.ItinerarioModel;
import br.com.transportae.itinerario.ItinerarioService;
import br.com.transportae.pontoParada.PontoParadaModel;
import br.com.transportae.pontoParada.PontoParadaService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;

@Service
public class LinhaTransporteService {

    @Autowired
    private final LinhaTransporteRepository linhaTransporteRepository;

    private final PontoParadaService pontoParadaService;
    private final ItinerarioService itinerarioService;

    // TODO: Estudar alternativa para referência circular de PontoParadaService
    public LinhaTransporteService(LinhaTransporteRepository linhaTransporteRepository, @Lazy ItinerarioService itinerarioService, @Lazy PontoParadaService pontoParadaService) {
        this.linhaTransporteRepository = linhaTransporteRepository;
        this.pontoParadaService = pontoParadaService;
        this.itinerarioService = itinerarioService;
    }

    @Transactional
    public LinhaTransporteModel cadastrarLinhaTransporte(LinhaTransporteDto linhaTransporteDto) {
        LinhaTransporteModel novaLinhaTransporte = converterDtoParaDomain(linhaTransporteDto);
        return linhaTransporteRepository.save(novaLinhaTransporte);
    }


    public LinhaTransporteModel converterDtoParaDomain(LinhaTransporteDto linhaTransporteDto) {
        return LinhaTransporteModel.builder()
            .id(linhaTransporteDto.getId())
            .nome(linhaTransporteDto.getNome())
            .turno(linhaTransporteDto.getTurno())
            .totalAssentos(linhaTransporteDto.getTotalAssentos())
            .codigoVeiculo(linhaTransporteDto.getCodigoVeiculo())
            .build();
    }

    public LinhaTransporteDto converterDomainParaDto(LinhaTransporteModel linhaTransporte) {
        LinhaTransporteDto linhaTransporteDto = new LinhaTransporteDto();
        BeanUtils.copyProperties(linhaTransporte, linhaTransporteDto);

        linhaTransporteDto.setHorarioProximoItinerario(getHorarioProximoItinerario(linhaTransporte));
        Optional<ItinerarioModel> ultimoItinerario = itinerarioService.getUltimoItinerarioHoje(linhaTransporte);

        if (ultimoItinerario.isPresent()) {
            linhaTransporteDto.setUltimoItinerarioHoje(itinerarioService.converterDomainParaDto(ultimoItinerario.get()));
        }

        return linhaTransporteDto;
    }

    public LocalDateTime getHorarioProximoItinerario(LinhaTransporteModel linhaTransporte) {
        LocalDateTime dataAtual = LocalDateTime.now();        

        LocalTime horarioIda  = pontoParadaService.getPrimeiroPontoIda(linhaTransporte).getHorarioPrevistoIda();
        LocalTime horarioVolta = pontoParadaService.getPrimeiroPontoVolta(linhaTransporte).getHorarioPrevistoVolta();

        LocalDate hoje = dataAtual.toLocalDate();
        LocalDateTime proximoHorarioIda = LocalDateTime.of(hoje, horarioIda);
        LocalDateTime proximoHorarioVolta = LocalDateTime.of(hoje, horarioVolta);

        if (proximoHorarioIda.isBefore(dataAtual)) {
            proximoHorarioIda = proximoHorarioIda.plusDays(1);
        }

        if (proximoHorarioVolta.isBefore(dataAtual)) {
            proximoHorarioVolta = proximoHorarioVolta.plusDays(1);
        }

        return proximoHorarioIda.isBefore(proximoHorarioVolta) ? proximoHorarioIda : proximoHorarioVolta;
    }

    public List<LinhaTransporteModel> getLinhasTransporte() {
        return linhaTransporteRepository.findAll();
    }

    public LinhaTransporteModel getLinhaTransporte(Long id) {
        return linhaTransporteRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Linha de transporte não encontrada"));
    }

    public Page<LinhaTransporteModel> listarLinhasTransporte(Pageable pagina, String pesquisa) {
        Page<LinhaTransporteModel> linhasTransporte = pesquisa.length() > 0 
            ? linhaTransporteRepository.findAllByNomeContainingIgnoreCase(pesquisa, pagina)
            : linhaTransporteRepository.findAll(pagina);

        return linhasTransporte;
    }

    public Page<LinhaTransporteModel> listarLinhasTransportePorTurno(Pageable pagina, Turno turno, String pesquisa) {
        Page<LinhaTransporteModel> linhasTransporte = pesquisa.length() > 0 
            ? linhaTransporteRepository.findAllByTurnoAndNomeContainingIgnoreCase(turno, pesquisa, pagina)
            : linhaTransporteRepository.findAllByTurno(turno, pagina);

        return linhasTransporte;
    }

    public LinhaTransporteModel atualizarLinhaTransporte(Long id, LinhaTransporteDto linhaTransporteDto) {
        LinhaTransporteModel linhaTransporteAtual = getLinhaTransporte(id);

        BeanUtils.copyProperties(linhaTransporteDto, linhaTransporteAtual, "id");

        return linhaTransporteRepository.save(linhaTransporteAtual);
    }

    //TODO: Enviar email de ativação para o motorista e alunos da linha
    public LinhaTransporteModel ativarLinhaTransporte(Long id) {
        LinhaTransporteModel linhaTransporte = getLinhaTransporte(id);
        linhaTransporte.setAtiva(true);
        linhaTransporte.setAtivadaEm(LocalDateTime.now());
        
        return linhaTransporteRepository.save(linhaTransporte);
    }   

    public List<LinhaTransporteModel> cadastrarLinhaTransporteMock(int quantidade) {
        Faker faker = new Faker(Locale.forLanguageTag("pt_BR"));
        List<LinhaTransporteModel> linhasTransporte = new ArrayList<>();

        for (int pos = 0; pos < quantidade; pos++) {    
            int posicaoAleatoria = faker.number()
                .numberBetween(0, Turno.values().length);
            
            Turno turno = Turno.values()[posicaoAleatoria];

            LinhaTransporteModel linhaTransporte = LinhaTransporteModel.builder()
                .nome("Linha " + pos)
                .turno(turno)
                .totalAssentos((short) faker.number().numberBetween(10, 50))
                .codigoVeiculo(faker.code().ean8())
                .build();

            LinhaTransporteModel linhaCadastrada = linhaTransporteRepository.save(linhaTransporte);
            linhasTransporte.add(linhaCadastrada);
        }

        return linhasTransporte;
    }
}
