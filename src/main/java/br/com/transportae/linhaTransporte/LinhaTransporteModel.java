package br.com.transportae.linhaTransporte;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import br.com.transportae.itinerario.ItinerarioModel;
import br.com.transportae.pontoParada.PontoParadaModel;
import br.com.transportae.usuarioLinha.UsuarioLinhaModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity(name = "linha_transporte")
@AllArgsConstructor
@NoArgsConstructor
public class LinhaTransporteModel {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Builder.Default
    private boolean ativa = false;

    @Column(nullable = false)
    private String nome;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Turno turno;

    @Column(nullable = false)
    private Short totalAssentos;
    
    private String codigoVeiculo;
    
    @Column
    private LocalDateTime ativadaEm;

    @CreationTimestamp
    private LocalDateTime dataCadastro;

    @UpdateTimestamp
    private LocalDateTime dataAtualizacao;
    
    @OneToMany(mappedBy = "linhaTransporte")
    List<UsuarioLinhaModel> usuariosLinha;

    @OneToMany(mappedBy = "linhaTransporte")
    List<PontoParadaModel> pontos;

    @OneToMany(mappedBy = "linhaTransporte")
    List<ItinerarioModel> itinerarios;
}
