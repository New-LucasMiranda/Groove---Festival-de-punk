package br.anhembi.spring_proja3.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.anhembi.spring_proja3.entities.User;

public interface UserRepo extends JpaRepository<User, String> {

     void save(Optional<User> entity);

}
