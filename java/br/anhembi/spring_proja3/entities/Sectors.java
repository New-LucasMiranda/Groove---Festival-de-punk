package br.anhembi.spring_proja3.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Sectors {

     @Id
     String nome;
     String dia;
     int qtdTotal;
     int qtdDisp;

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

     public String getDia() {
          return dia;
     }

     public void setDia(String dia) {
          this.dia = dia;
     }

}
