package br.anhembi.groove_aplication.entities;

import java.io.Serializable;
import java.util.Objects;

public class SectorsId implements Serializable {

    private String nome;
    private String dia;

    public SectorsId() {
    }

    public SectorsId(String nome, String dia) {
        this.nome = nome;
        this.dia = dia;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SectorsId)) return false;
        SectorsId that = (SectorsId) o;
        return Objects.equals(nome, that.nome) && Objects.equals(dia, that.dia);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, dia);
    }
}
