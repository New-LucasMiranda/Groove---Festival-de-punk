package br.anhembi.groove_aplication.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.anhembi.groove_aplication.entities.Sectors;
import br.anhembi.groove_aplication.entities.SectorsId;

public interface SectorsRepo extends JpaRepository<Sectors, SectorsId> {

}
