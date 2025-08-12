package br.ars.match_service.repo;

import br.ars.match_service.domain.Match;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface MatchRepository extends ReactiveCrudRepository<Match, UUID> {
    Mono<Boolean> existsByPairLowAndPairHigh(UUID pairLow, UUID pairHigh);
    Mono<Match> findByPairLowAndPairHigh(UUID pairLow, UUID pairHigh);
    Flux<Match> findAllByUserAOrUserB(UUID userA, UUID userB);
}
