package com.example.Prova1ConsumidorBrunoViola;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

@Entity
@Table(name="item_cardapio")
public class ItemCardapio implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name="id_restaurante", nullable=false)
    private Restaurante restaurante;

    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    private String descricao;

    @DecimalMin(value = "0.0", message = "O preço deve ser um número positivo")
    private double preco;

    private Integer quantidade=0;

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

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }
    @Override
    public boolean equals(Object o){
    if (o == null || this.getClass() != o.getClass()) {
        return false;
    }
     return((ItemCardapio)o).id==(this.id);
    }
    @Override
    public int hashCode(){
        return id*12345;
    }
}
