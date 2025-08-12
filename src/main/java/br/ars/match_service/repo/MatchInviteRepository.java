package br.ars.match_service.repo;

import br.ars.match_service.domain.MatchInvite;
import br.ars.match_service.domain.InviteStatus;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MatchInviteRepository extends CrudRepository<MatchInvite, UUID> {
    Optional<MatchInvite> findByInviterIdAndTargetId(UUID inviterId, UUID targetId);
    List<MatchInvite> findAllByTargetIdAndStatus(UUID targetId, InviteStatus status);
}
