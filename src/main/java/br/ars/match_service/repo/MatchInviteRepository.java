package br.ars.match_service.repo;

import br.ars.match_service.domain.InviteStatus;
import br.ars.match_service.domain.MatchInvite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MatchInviteRepository extends JpaRepository<MatchInvite, UUID> {

    Optional<MatchInvite> findByInviterIdAndTargetId(UUID inviterId, UUID targetId);

    // Listagens para tela (ordenado pelo campo de entidade "createdAt")
    List<MatchInvite> findAllByInviterIdOrderByCreatedAtDesc(UUID inviterId);
    List<MatchInvite> findAllByInviterIdAndStatusOrderByCreatedAtDesc(UUID inviterId, InviteStatus status);

    List<MatchInvite> findAllByTargetIdOrderByCreatedAtDesc(UUID targetId);
    List<MatchInvite> findAllByTargetIdAndStatusOrderByCreatedAtDesc(UUID targetId, InviteStatus status);
}
