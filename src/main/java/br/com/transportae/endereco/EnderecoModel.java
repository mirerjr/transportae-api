package br.com.transportae.endereco;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import br.com.transportae.instituicao.InstituicaoModel;
import br.com.transportae.pontoParada.PontoParadaModel;
import br.com.transportae.usuario.UsuarioModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity(name = "endereco")
@AllArgsConstructor
@NoArgsConstructor
public class EnderecoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(length = 255)
    private String descricao;

    @Column(length = 10)
    private String numero;

    @Column(length = 255)
    private String complemento;

    @Column(length = 9)
    private String cep;

    @Column(length = 35)
    private String bairro;

    @Column(length = 35)
    private String cidade;

    @Column(nullable = true)
    private Double latitude;

    @Column(nullable = true)
    private Double longitude;

    @CreationTimestamp
    private LocalDateTime dataCadastro;

    @UpdateTimestamp
    private LocalDateTime dataAtualizacao;

    @OneToOne(mappedBy = "endereco")
    private UsuarioModel usuario;

    @OneToOne(mappedBy = "endereco")
    private InstituicaoModel instituicao;

    @OneToOne(mappedBy = "endereco")
    private PontoParadaModel pontoParada;
}
