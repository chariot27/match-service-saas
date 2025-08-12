package br.ars.match_service.repo;

import br.ars.match_service.domain.Match;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MatchRepository extends CrudRepository<Match, UUID> {
    boolean existsByPairLowAndPairHigh(UUID pairLow, UUID pairHigh);
    Optional<Match> findByPairLowAndPairHigh(UUID pairLow, UUID pairHigh);
    List<Match> findAllByUserAOrUserB(UUID userA, UUID userB);
}
