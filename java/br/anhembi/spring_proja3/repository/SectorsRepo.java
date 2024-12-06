package br.anhembi.spring_proja3.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.anhembi.spring_proja3.entities.Sectors;

public interface SectorsRepo extends JpaRepository<Sectors, String> {

        void save(Optional<Sectors> entity);
}
