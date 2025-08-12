package br.ars.match_service.repo;

import br.ars.match_service.domain.MatchAccept;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface MatchAcceptRepository extends CrudRepository<MatchAccept, UUID> { }
