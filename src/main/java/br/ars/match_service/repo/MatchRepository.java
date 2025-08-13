package br.ars.match_service.repo;

import br.ars.match_service.domain.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface MatchRepository extends JpaRepository<Match, UUID> {

    boolean existsByPairLowAndPairHigh(UUID pairLow, UUID pairHigh);

    Optional<Match> findByPairLowAndPairHigh(UUID pairLow, UUID pairHigh);

    // usada no listForUser(userId)
    List<Match> findAllByUserAOrUserB(UUID userA, UUID userB);

    // variação se quiser ordenado (não quebra o que já existe)
    List<Match> findAllByUserAOrUserBOrderByCreatedAtDesc(UUID userA, UUID userB);
}
