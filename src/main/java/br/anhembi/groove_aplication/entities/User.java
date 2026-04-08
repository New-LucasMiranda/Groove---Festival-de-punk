package br.anhembi.groove_aplication.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "users")
public class User {

     @Id
     @NotBlank(message = "CPF is required")
     @Pattern(regexp = "\\d{11}", message = "CPF must have 11 digits")
     private String cpf;

     @NotBlank(message = "Name is required")
     @Column(nullable = false)
     private String nome;

     @Min(value = 0, message = "Age must be positive")
     @Max(value = 150, message = "Age must be less than 150")
     @Column(nullable = false)
     private int idade;

     @NotBlank(message = "Email is required")
     @Email(message = "Email should be valid")
     @Column(nullable = false)
     private String email;

     @NotBlank(message = "Day/Ticket type is required")
     @Column(nullable = false)
     private String dia;

     @Column(name = "prim_reserva")
     private String primReserva;

     @Column(name = "seg_reserva")
     private String segReserva;

     @Column(nullable = false)
     private boolean situacao;

     public User() {
     }

     public User(String cpf, String nome, int idade, String email, String dia, String primReserva, String segReserva,
               boolean situacao) {
          this.cpf = cpf;
          this.nome = nome;
          this.idade = idade;
          this.email = email;
          this.dia = dia;
          this.primReserva = primReserva;
          this.segReserva = segReserva;
          this.situacao = situacao;
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

     public void setSegReserva(String segReserva) {
          this.segReserva = segReserva;
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

     public boolean getSituacao() {
          return situacao;
     }

     public void setSituacao(boolean situacao) {
          this.situacao = situacao;
     }

     public User copy() {
          return new User(cpf, nome, idade, email, dia, primReserva, segReserva, situacao);
     }
}
