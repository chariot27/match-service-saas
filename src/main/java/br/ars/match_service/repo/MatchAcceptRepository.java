package br.ars.match_service.repo;

import br.ars.match_service.domain.MatchAccept;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface MatchAcceptRepository extends JpaRepository<MatchAccept, UUID> {

    // consultas por FK (agora é relação): use o sufixo _Id
    List<MatchAccept> findAllByInvite_Id(UUID inviteId);

    Optional<MatchAccept> findFirstByInvite_IdOrderByCreatedAtDesc(UUID inviteId);
}
