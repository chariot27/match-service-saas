package br.ars.match_service.repo;

import br.ars.match_service.domain.MatchInvite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MatchInviteRepository extends JpaRepository<MatchInvite, UUID> {

    Optional<MatchInvite> findByInviterIdAndTargetId(UUID inviterId, UUID targetId);

    boolean existsByInviterIdAndTargetId(UUID inviterId, UUID targetId);

    // se precisar limpar duplicados antigos, útil em migrações
    long deleteByInviterIdAndTargetId(UUID inviterId, UUID targetId);
}
