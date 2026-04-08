package br.anhembi.groove_aplication.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.anhembi.groove_aplication.entities.User;

public interface UserRepo extends JpaRepository<User, String> {

}

