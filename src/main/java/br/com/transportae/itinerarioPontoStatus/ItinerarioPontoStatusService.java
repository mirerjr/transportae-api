package br.com.transportae.itinerarioPontoStatus;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import br.com.transportae.itinerarioPonto.ItinerarioPontoModel;
import br.com.transportae.usuario.UsuarioDto;
import br.com.transportae.usuario.UsuarioModel;
import br.com.transportae.usuario.UsuarioService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItinerarioPontoStatusService {

    private final ItinerarioPontoStatusRepository itinerarioPontoStatusRepository;
    private final UsuarioService usuarioService;

    public ItinerarioPontoStatusModel cadastrarItinerarioPontoStatus(ItinerarioPontoStatusDto itinerarioPontoStatusDto, ItinerarioPontoModel itinerarioPonto) {
        ItinerarioPontoStatusModel novoItinerarioPontoStatus = converterDtoParaDomain(itinerarioPontoStatusDto);

        UsuarioModel usuario = usuarioService.getUsuario(itinerarioPontoStatusDto.getUsuarioId());
        novoItinerarioPontoStatus.setUsuario(usuario);
        novoItinerarioPontoStatus.setItinerarioPonto(itinerarioPonto);

        return itinerarioPontoStatusRepository.save(novoItinerarioPontoStatus);
    }

    public List<ItinerarioPontoStatusModel> getConfirmacoesUsuarioPorItinerarioPonto(ItinerarioPontoModel itinerarioPonto) {
        return itinerarioPontoStatusRepository.findAllByItinerarioPontoAndUsuarioStatus(
            itinerarioPonto, List.of(TipoItinerarioPontoStatus.ALUNO_DESMARCOU, TipoItinerarioPontoStatus.ALUNO_PRESENTE));
    }

    public ItinerarioPontoStatusModel converterDtoParaDomain(ItinerarioPontoStatusDto itinerarioPontoStatusDto) {
        ItinerarioPontoStatusModel itinerarioPontoStatus = ItinerarioPontoStatusModel.builder()
            .id(itinerarioPontoStatusDto.getId())
            .status(itinerarioPontoStatusDto.getStatus())
            .dataCadastro(itinerarioPontoStatusDto.getDataCadastro())
            .dataAtualizacao(itinerarioPontoStatusDto.getDataAtualizacao())
            .build();

        return itinerarioPontoStatus;
    }

    public ItinerarioPontoStatusDto converterDomainParaDto(ItinerarioPontoStatusModel itinerarioPontoStatus) {
        ItinerarioPontoStatusDto itinerarioPontoStatusDto = new ItinerarioPontoStatusDto();
        BeanUtils.copyProperties(itinerarioPontoStatus, itinerarioPontoStatusDto);

        if (Objects.nonNull(itinerarioPontoStatus.getUsuario())) {
            UsuarioDto usuario = usuarioService.converterDomainParaDto(itinerarioPontoStatus.getUsuario());
            itinerarioPontoStatusDto.setUsuario(usuario);
        }

        return itinerarioPontoStatusDto;
    }
}
