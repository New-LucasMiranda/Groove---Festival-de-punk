package br.anhembi.groove_aplication.entities;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite primary key class for the Sectors entity.
 * Represents the combination of sector name and day.
 */
public class SectorId implements Serializable {
     private static final long serialVersionUID = 1L;

     private String nome;
     private String dia;

     public SectorId() {
     }

     public SectorId(String nome, String dia) {
          this.nome = nome;
          this.dia = dia;
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

     @Override
     public boolean equals(Object o) {
          if (this == o)
               return true;
          if (o == null || getClass() != o.getClass())
               return false;
          SectorId sectorId = (SectorId) o;
          return Objects.equals(nome, sectorId.nome) &&
                    Objects.equals(dia, sectorId.dia);
     }

     @Override
     public int hashCode() {
          return Objects.hash(nome, dia);
     }

     @Override
     public String toString() {
          return "SectorId{" +
                    "nome='" + nome + '\'' +
                    ", dia='" + dia + '\'' +
                    '}';
     }
}
