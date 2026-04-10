package br.anhembi.groove_aplication.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @Pattern(regexp = "\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}", message = "CPF must have 11 digits or format XXX.XXX.XXX-XX")
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

    @JsonProperty("situacao")
    @JsonAlias("situação")
    @Column(name = "situacão", nullable = false, columnDefinition = "boolean default false")
    private boolean situacao = false;

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

    public String getCpf() {
        return cpf;
    }

    // Normalizes CPF on input: strips dots and dashes so DB always stores 11 digits
    public void setCpf(String cpf) {
        this.cpf = cpf == null ? null : cpf.replaceAll("[^\\d]", "");
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
