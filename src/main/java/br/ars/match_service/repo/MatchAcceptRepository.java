package br.ars.match_service.repo;

import br.ars.match_service.domain.MatchAccept;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface MatchAcceptRepository extends ReactiveCrudRepository<MatchAccept, UUID> { }
