package com.example.Prova1AdminBrunoViola;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name="item_cardapio")
public class ItemCardapio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name="id_restaurante", nullable=false)
    private Restaurante restaurante;

    @NotBlank(message = "O nome é obrigatório")
    private String nome;
    private Integer quantidade;
    private String descricao;

    @DecimalMin(value = "0.1", message = "O preço deve ser maior que zero")
    private double preco;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

   public void setRestaurante(Restaurante restaurante) {
       this.restaurante = restaurante;
    }
    public Restaurante getRestaurante(){return restaurante;}

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }
}
