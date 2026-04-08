package br.anhembi.spring_proja3.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

     @Id
     private String cpf;
     private String nome;
     private int idade;
     private String email;
     private String dia;
     private String primReserva;
     private String segReserva;
     private boolean situacão;

     public User() {
     }

     public User(String cpf, String nome, int idade, String email, String dia, String primReserva, String segReserva,
               boolean situacão) {
          this.cpf = cpf;
          this.nome = nome;
          this.idade = idade;
          this.email = email;
          this.dia = dia;
          this.primReserva = primReserva;
          this.segReserva = segReserva;
          this.situacão = situacão;
     }

     public String getPrimReserva() {
          return primReserva;
     }

     public void setPrimReserva(String primReserva) {
          this.primReserva = primReserva;
     }

     public String getSegReserva() {
          return segReserva;
     }

     public void setSegReserva(String secReserva) {
          this.segReserva = secReserva;
     }

     public String getCpf() {
          return cpf;
     }

     public void setCpf(String cpf) {
          this.cpf = cpf;
     }

     public String getNome() {
          return nome;
     }

     public void setNome(String nome) {
          this.nome = nome;
     }

     public int getIdade() {
          return idade;
     }

     public void setIdade(int idade) {
          this.idade = idade;
     }

     public String getEmail() {
          return email;
     }

     public void setEmail(String email) {
          this.email = email;
     }

     public String getDia() {
          return dia;
     }

     public void setDia(String dia) {
          this.dia = dia;
     }

     public boolean getSituação() {
          return situacão;
     }

     public void setSituação(boolean situacão) {
          this.situacão = situacão;
     }

     public User copy() {

          return new User(cpf, nome, idade, email, dia, primReserva, segReserva, situacão);

     }
}
