package br.ars.match_service.repo;

import br.ars.match_service.domain.MatchInvite;
import br.ars.match_service.domain.InviteStatus;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface MatchInviteRepository extends ReactiveCrudRepository<MatchInvite, UUID> {
    Mono<MatchInvite> findByInviterIdAndTargetId(UUID inviterId, UUID targetId);
    Flux<MatchInvite> findAllByTargetIdAndStatus(UUID targetId, InviteStatus status);
}
