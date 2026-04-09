package br.anhembi.groove_aplication.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "sectors")
@IdClass(SectorsId.class)
public class Sectors {

    @Id
    @NotBlank(message = "Sector name is required")
    @Column(nullable = false)
    private String nome;

    @Id
    @NotBlank(message = "Day is required")
    @Column(nullable = false)
    private String dia;

    @Min(value = 0, message = "Total quantity must be positive")
    @Column(name = "qtd_total", nullable = false)
    private int qtdTotal;

    @Min(value = 0, message = "Available quantity must be positive")
    @Column(name = "qtd_disp", nullable = false)
    private int qtdDisp;

    public Sectors() {
    }

    public Sectors(String nome, String dia, int qtdTotal, int qtdDisp) {
        this.nome = nome;
        this.dia = dia;
        this.qtdTotal = qtdTotal;
        this.qtdDisp = qtdDisp;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public int getQtdTotal() {
        return qtdTotal;
    }

    public void setQtdTotal(int qtdTotal) {
        this.qtdTotal = qtdTotal;
    }

    public int getQtdDisp() {
        return qtdDisp;
    }

    public void setQtdDisp(int qtdDisp) {
        this.qtdDisp = qtdDisp;
    }
}
