package br.com.transportae.itinerario;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.util.Streamable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import br.com.transportae.itinerarioPonto.ItinerarioPontoModel;
import br.com.transportae.itinerarioPonto.ItinerarioPontoService;
import br.com.transportae.itinerarioPontoStatus.ItinerarioPontoStatusModel;
import br.com.transportae.itinerarioPontoStatus.ItinerarioPontoStatusRepository;
import br.com.transportae.itinerarioPontoStatus.ItinerarioPontoStatusService;
import br.com.transportae.itinerarioStatus.ItinerarioStatusDto;
import br.com.transportae.itinerarioStatus.ItinerarioStatusModel;
import br.com.transportae.itinerarioStatus.ItinerarioStatusService;
import br.com.transportae.itinerarioStatus.TipoItinerarioStatus;
import br.com.transportae.linhaTransporte.LinhaTransporteModel;
import br.com.transportae.linhaTransporte.LinhaTransporteService;
import br.com.transportae.usuario.UsuarioDto;
import br.com.transportae.usuario.UsuarioModel;
import br.com.transportae.usuario.UsuarioService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItinerarioService {

    private final ItinerarioRepository itinerarioRepository;
    private final LinhaTransporteService linhaTransporteService;
    private final ItinerarioStatusService itinerarioStatusService;
    private final ItinerarioPontoService itinerarioPontoService;
    private final ItinerarioPontoStatusRepository itinerarioPontoStatusRepository;
    private final UsuarioService usuarioService;

    @Transactional
    public ItinerarioModel cadastrarItinerario(ItinerarioDto itinerarioDto, Principal principal) {
        ItinerarioModel novoItinerario = converterDtoParaDomain(itinerarioDto);

        vincularLinhaTransporte(itinerarioDto, novoItinerario);
        vincularItinerarioStatus(itinerarioDto, novoItinerario);
        vincularMotorista(itinerarioDto, novoItinerario, principal);

        itinerarioPontoService.cadastrarPontosDoItinerario(novoItinerario);

        return itinerarioRepository.save(novoItinerario);
    }

    private void vincularLinhaTransporte(ItinerarioDto itinerarioDto, ItinerarioModel novoItinerario) {
        if (Objects.isNull(itinerarioDto.getLinhaTransporteId())) return;

        LinhaTransporteModel linhaTransporte = linhaTransporteService.getLinhaTransporte(itinerarioDto.getLinhaTransporteId());
        
        novoItinerario.setLinhaTransporte(linhaTransporte);
    }

    private void vincularItinerarioStatus(ItinerarioDto itinerarioDto, ItinerarioModel itinerarioModel) {
        ItinerarioStatusModel itinerarioStatus = itinerarioStatusService.gerarStatusIniciado(itinerarioModel);
        itinerarioModel.setUltimoStatus(itinerarioStatus.getTipoItinerarioStatus());
        itinerarioModel.setStatus(List.of(itinerarioStatus));
    }

    private void vincularMotorista(ItinerarioDto itinerarioDto, ItinerarioModel novoItinerario, Principal principal) {
        UsuarioModel motorista = usuarioService.getUsuarioLogado(principal);
        novoItinerario.setMotorista(motorista);        
    }

    public ItinerarioModel converterDtoParaDomain(ItinerarioDto itinerarioDto) {
        return ItinerarioModel.builder()
            .id(itinerarioDto.getId())
            .codigoVeiculo(itinerarioDto.getCodigoVeiculo())
            .tipoItinerario(itinerarioDto.getTipoItinerario())
            .build();
    }

    public ItinerarioDto converterDomainParaDto(ItinerarioModel itinerario) {
        ItinerarioDto itinerarioDto = new ItinerarioDto();
        BeanUtils.copyProperties(itinerario, itinerarioDto);

        UsuarioModel motorista = itinerario.getMotorista();

        itinerarioDto.setLinhaTransporteId(itinerario.getLinhaTransporte().getId());
        itinerarioDto.setMotorista(usuarioService.converterDomainParaDto(motorista));
        
        if (Objects.nonNull(itinerario.getPontosItinerario())) {
            itinerarioDto.setItinerarioPonto(itinerario
                .getPontosItinerario().stream()
                .map(itinerarioPontoService::converterDomainParaDto)
                .toList());
        }
        
        itinerarioDto.setItinerarioStatus(itinerario
            .getStatus().stream()
            .map(itinerarioStatusService::converterDomainParaDto)
            .toList());

        return itinerarioDto;
    }

    public ItinerarioModel getItinerario(Long id) {
        return itinerarioRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Itinerario não encontrado"));
    }

    public Page<ItinerarioModel> listarItinerariosPorLinha(Pageable pagina, Long linhaTransporteId, String pesquisa) {
        Page<ItinerarioModel> itinerarios = pesquisa.length() > 0 
            ? itinerarioRepository.findAllByLinhaTransporteIdAndCodigoVeiculoContainingIgnoreCase(linhaTransporteId, pesquisa, pagina)
            : itinerarioRepository.findAllByLinhaTransporteId(linhaTransporteId, pagina);
        
        return itinerarios;
    }

    public Page<ItinerarioModel> listarItinerarios(Pageable pagina, String pesquisa) {
        Page<ItinerarioModel> itinerarios = pesquisa.length() > 0 
            ? itinerarioRepository.findAllByCodigoVeiculoContainingIgnoreCase(pesquisa, pagina)
            : itinerarioRepository.findAll(pagina);

        return itinerarios;
    }

    public List<ItinerarioStatusDto> listarItinerarioStatusPorItinerario(Long id) {
        return itinerarioStatusService.listarItinerarioStatusPorItinerario(id).stream()
            .map(itinerarioStatusService::converterDomainParaDto)
            .toList();
    }

    public Optional<ItinerarioModel> getItinerarioIniciadoPorLinha(Long linhaId) {
        return itinerarioRepository.findFirstByLinhaTransporteIdAndTipoItinerarioStatus(linhaId, TipoItinerarioStatus.INICIADO);
    }

    public ResponseEntity<ItinerarioStatusDto> concluirItinerario(Long id) {
        ItinerarioModel itinerario = getItinerario(id);

        ItinerarioStatusModel itinerarioStatus = itinerarioStatusService.gerarStatusConcluido(itinerario);
        itinerario.getStatus().add(itinerarioStatus);
        itinerario.setUltimoStatus(itinerarioStatus.getTipoItinerarioStatus());

        itinerarioRepository.save(itinerario);

        return ResponseEntity.ok(itinerarioStatusService.converterDomainParaDto(itinerarioStatus));
    }

    public Optional<ItinerarioModel> getUltimoItinerarioHoje(LinhaTransporteModel linhaTransporte) {
        LocalDate hoje = LocalDate.now();
        LocalDateTime inicioDoDia = hoje.atStartOfDay();
        LocalDateTime finalDoDia = hoje.atTime(23, 59, 59);

        return itinerarioRepository.findFirstByLinhaTransporteAndDataCadastroBetweenOrderByDataCadastroDesc(linhaTransporte, inicioDoDia, finalDoDia);
    }

    public Page<ItinerarioModel> listarItinerariosPorUsuario(Pageable pageable, Long id, String pesquisa) {
        UsuarioModel usuario = usuarioService.getUsuario(id);

        List<ItinerarioPontoStatusModel> itinerarioPontoStatuses = itinerarioPontoStatusRepository.findAllByUsuario(usuario);

        List<ItinerarioModel> itinerarios = itinerarioPontoStatuses.stream()
            .map(ItinerarioPontoStatusModel::getItinerarioPonto)
            .map(ItinerarioPontoModel::getItinerario)
            .distinct()
            .collect(Collectors.toList());

        int inicio = (int) pageable.getOffset();
        int fim = Math.min((inicio + pageable.getPageSize()), itinerarios.size());

        return new PageImpl<>(itinerarios.subList(inicio, fim), pageable, itinerarios.size());
    }
}
