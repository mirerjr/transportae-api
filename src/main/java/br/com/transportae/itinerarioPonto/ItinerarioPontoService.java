package br.com.transportae.itinerarioPonto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import br.com.transportae.itinerarioPontoStatus.ItinerarioPontoStatusDto;
import br.com.transportae.itinerarioPontoStatus.ItinerarioPontoStatusModel;
import br.com.transportae.itinerarioPontoStatus.ItinerarioPontoStatusRepository;
import br.com.transportae.itinerarioPontoStatus.ItinerarioPontoStatusService;
import br.com.transportae.itinerarioPontoStatus.TipoItinerarioPontoStatus;
import br.com.transportae.itinerario.ItinerarioModel;
import br.com.transportae.pontoParada.PontoParadaService;
import br.com.transportae.usuario.UsuarioModel;
import br.com.transportae.usuario.UsuarioService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItinerarioPontoService {

    private final ItinerarioPontoRepository itinerarioPontoRepository;
    private final ItinerarioPontoStatusRepository itinerarioPontoStatusRepository;
    private final ItinerarioPontoStatusService itinerarioPontoStatusService;
    private final PontoParadaService pontoParadaService;
    private final UsuarioService usuarioService;

    public List<ItinerarioPontoModel> cadastrarPontosDoItinerario(ItinerarioModel itinerario) {
        List<ItinerarioPontoModel> itinerarioPontos = new ArrayList<>();

        itinerario.getLinhaTransporte().getPontos().forEach(ponto ->{
            ItinerarioPontoModel novoItinerarioPonto = ItinerarioPontoModel.builder()
                .itinerario(itinerario)
                .pontoParada(ponto)
                .build();

            itinerarioPontos.add(novoItinerarioPonto);
        });

        return itinerarioPontoRepository.saveAll(itinerarioPontos);
    }

    public ItinerarioPontoDto converterDomainParaDto(ItinerarioPontoModel itinerarioPonto) {
        ItinerarioPontoDto itinerarioPontoDto = new ItinerarioPontoDto();
        BeanUtils.copyProperties(itinerarioPonto, itinerarioPontoDto);

        itinerarioPontoDto.setItinerarioId(itinerarioPonto.getItinerario().getId());
        itinerarioPontoDto.setPontoParadaId(itinerarioPonto.getPontoParada().getId());
        itinerarioPontoDto.setPontoParada(pontoParadaService.converterDomainParaDto(itinerarioPonto.getPontoParada()));

        List<ItinerarioPontoStatusDto> itinerarioPontoStatusDto =  itinerarioPonto.getPontoStatus().stream()
            .map(itinerarioPontoStatusService::converterDomainParaDto)
            .toList();

        itinerarioPontoDto.setItinerarioPontoStatus(itinerarioPontoStatusDto);

        return itinerarioPontoDto;
    }

    public ItinerarioPontoModel getItinerarioPonto(Long id) {
        return itinerarioPontoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("ItinerarioPonto não encontrado"));
    }

    public List<ItinerarioPontoModel> listarPorItinerarioIdEPontoId(Long itinerarioId, Long pontoParadaId) {
        return itinerarioPontoRepository.findAllByItinerarioIdAndPontoParadaId(itinerarioId, pontoParadaId);
    }

    public ItinerarioPontoStatusDto adicionarPontoStatus(Long id, ItinerarioPontoStatusDto itinerarioPontoStatusDto) {
        ItinerarioPontoModel itinerarioPonto = getItinerarioPonto(id);
        ItinerarioPontoStatusModel itinerarioPontoStatus = itinerarioPontoStatusService.cadastrarItinerarioPontoStatus(itinerarioPontoStatusDto, itinerarioPonto);

        return itinerarioPontoStatusService.converterDomainParaDto(itinerarioPontoStatus);        
    }

    // TODO: Revisar lógica, pois o status devem ser sequenciais e não podem ser modificados
    public ItinerarioPontoStatusDto alterarStatusPontoAluno(Long id, ItinerarioPontoStatusDto itinerarioPontoStatusDto) {
        ItinerarioPontoModel itinerarioPonto = getItinerarioPonto(id);
        UsuarioModel usuario = usuarioService.getUsuario(itinerarioPontoStatusDto.getUsuarioId());

        ItinerarioPontoStatusModel ultimoStatus = itinerarioPontoStatusRepository.findLatestByItinerarioPontoAndUsuario(itinerarioPonto, usuario);

        if (ultimoStatus == null) {
            itinerarioPontoStatusDto.setStatus(itinerarioPontoStatusDto.getStatus());
            
        } else {
            // Optional<ItinerarioPontoStatusModel> pontoStatusOpt = Optional.empty();

            // if (itinerarioPontoStatusDto.getId() != null) {
            //     pontoStatusOpt = itinerarioPontoStatusRepository.findById(itinerarioPontoStatusDto.getId());
            // }

            // if (pontoStatusOpt.isPresent()) {
            //     ItinerarioPontoStatusModel pontoStatus = pontoStatusOpt.get();
            //     pontoStatus.setStatus(itinerarioPontoStatusDto.getStatus());

            //     ItinerarioPontoStatusModel pontoStatusAtualizado = itinerarioPontoStatusRepository.save(pontoStatus);
            //     return itinerarioPontoStatusService.converterDomainParaDto(pontoStatusAtualizado);

            // } else
            
            if (ultimoStatus.getStatus().equals(TipoItinerarioPontoStatus.ALUNO_PRESENTE)) {
                itinerarioPontoStatusDto.setStatus(TipoItinerarioPontoStatus.ALUNO_DESMARCOU);

            } else if (ultimoStatus.getStatus().equals(TipoItinerarioPontoStatus.ALUNO_DESMARCOU)) {
                itinerarioPontoStatusDto.setStatus(TipoItinerarioPontoStatus.ALUNO_PRESENTE);
            }
        }

        ItinerarioPontoStatusModel itinerarioPontoStatus = itinerarioPontoStatusService.cadastrarItinerarioPontoStatus(itinerarioPontoStatusDto, itinerarioPonto);
        return itinerarioPontoStatusService.converterDomainParaDto(itinerarioPontoStatus);
    }

    public List<ItinerarioPontoStatusModel> getAlunosConfirmados(Long id) {
        ItinerarioPontoModel itinerarioPonto = getItinerarioPonto(id);
        return itinerarioPontoStatusService.getConfirmacoesUsuarioPorItinerarioPonto(itinerarioPonto);
    }
}
