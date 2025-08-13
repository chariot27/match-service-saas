package br.ars.match_service.service;

import br.ars.match_service.domain.*;
import br.ars.match_service.dto.*;
import br.ars.match_service.mapper.*;
import br.ars.match_service.repo.*;
import br.ars.match_service.util.PairKeyUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchService {

    private final MatchInviteRepository inviteRepo;
    private final MatchRepository matchRepo;
    private final MatchAcceptRepository acceptRepo;
    private final MatchInviteMapper inviteMapper;
    private final MatchMapper matchMapper;
    private final MatchAcceptMapper acceptMapper;

    // ---------- INVITE ----------
    @Transactional
    public InviteResponse invite(InviteRequest request) {
        long t0 = System.currentTimeMillis();
        log.info("[MATCH][INVITE][IN] payload={{inviterId:{}, targetId:{}, inviterName:{}, inviterPhone:{}, inviterAvatar:{}}}",
                safe(request.getInviterId()), safe(request.getTargetId()),
                request.getInviterName(), request.getInviterPhone(), request.getInviterAvatar());

        if (Objects.equals(request.getInviterId(), request.getTargetId())) {
            log.warn("[MATCH][INVITE] inviterId == targetId ({}). Abortando.", request.getInviterId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "inviterId == targetId");
        }

        UUID low  = PairKeyUtil.low(request.getInviterId(), request.getTargetId());
        UUID high = PairKeyUtil.high(request.getInviterId(), request.getTargetId());
        log.debug("[MATCH][INVITE] pair computed -> low={}, high={}", low, high);

        // Já existe match pronto?
        if (matchRepo.existsByPairLowAndPairHigh(low, high)) {
            UUID matchId = matchRepo.findByPairLowAndPairHigh(low, high).map(Match::getId).orElse(null);
            log.info("[MATCH][INVITE] match já existente para par low/high. matchId={}", matchId);
            return InviteResponse.builder().matched(true).matchId(matchId).build();
        }

        // Já existe convite idêntico?
        Optional<MatchInvite> existing = inviteRepo.findByInviterIdAndTargetId(request.getInviterId(), request.getTargetId());
        if (existing.isPresent()) {
            log.info("[MATCH][INVITE] convite já existente (mesmo inviter->target). inviteId={}", existing.get().getId());
            return InviteResponse.builder().matched(false).invite(inviteMapper.toDTO(existing.get())).build();
        }

        // Cria novo convite (JPA gera id/timestamps; status default = PENDING na entidade)
        MatchInvite entity = inviteMapper.toEntity(request);

        try {
            entity = inviteRepo.save(entity);
            log.info("[MATCH][INVITE] convite criado com sucesso. inviteId={}", entity.getId());
        } catch (DataIntegrityViolationException e) {
            // corrida contra unique (inviter_id, target_id)
            log.warn("[MATCH][INVITE] Unique violation ao salvar convite (race). Tentando recarregar. cause={}", e.getMessage());
            entity = inviteRepo.findByInviterIdAndTargetId(request.getInviterId(), request.getTargetId())
                    .orElseThrow(() -> {
                        log.error("[MATCH][INVITE] duplicate detectado e não consegui recarregar o convite.");
                        return new ResponseStatusException(HttpStatus.CONFLICT, "invite duplicate and not found");
                    });
        }

        // Existe convite oposto? (target -> inviter) -> vira match
        Optional<MatchInvite> opposite = inviteRepo.findByInviterIdAndTargetId(entity.getTargetId(), entity.getInviterId());
        if (opposite.isPresent()) {
            log.info("[MATCH][INVITE] convite oposto encontrado (target->inviter). Criando match… opInviteId={}", opposite.get().getId());
            Match m = createMatchIfAbsent(entity);
            long ms = System.currentTimeMillis() - t0;
            log.info("[MATCH][INVITE][OUT] matched=true matchId={} ({} ms)", m.getId(), ms);
            return InviteResponse.builder().matched(true).matchId(m.getId()).build();
        }

        long ms = System.currentTimeMillis() - t0;
        log.info("[MATCH][INVITE][OUT] matched=false inviteId={} ({} ms)", entity.getId(), ms);
        return InviteResponse.builder().matched(false).invite(inviteMapper.toDTO(entity)).build();
    }

    // ---------- ACCEPT ----------
    @Transactional
    public AcceptDTO accept(AcceptRequest req) {
        long t0 = System.currentTimeMillis();
        log.info("[MATCH][ACCEPT][IN] payload={{inviteId:{}, inviterId:{}, targetId:{}}}",
                safe(req.getInviteId()), safe(req.getInviterId()), safe(req.getTargetId()));

        MatchInvite invite = (req.getInviteId() != null)
                ? inviteRepo.findById(req.getInviteId()).orElse(null)
                : inviteRepo.findByInviterIdAndTargetId(req.getInviterId(), req.getTargetId()).orElse(null);

        if (invite == null) {
            log.warn("[MATCH][ACCEPT] convite não encontrado para criteria inviteId={}, pair={} -> 404",
                    req.getInviteId(), pairStr(req.getInviterId(), req.getTargetId()));
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "invite not found");
        }

        log.debug("[MATCH][ACCEPT] convite localizado inviteId={}, status={}", invite.getId(), invite.getStatus());

        if (invite.getStatus() == InviteStatus.PENDING) {
            invite.setStatus(InviteStatus.ACCEPTED); // dirty checking
            inviteRepo.save(invite);
            log.info("[MATCH][ACCEPT] convite marcado como ACCEPTED. inviteId={}", invite.getId());
        }

        Match m = createMatchIfAbsent(invite);
        log.info("[MATCH][ACCEPT] match garantido matchId={}", m.getId());

        // cria accept ligado ao invite (JPA gera id/timestamps)
        MatchAccept acc = MatchAccept.builder()
                .invite(invite)
                .inviterName(invite.getInviterName())
                .inviterPhone(invite.getInviterPhone())
                .inviterAvatar(invite.getInviterAvatar())
                .build();
        acceptRepo.save(acc);

        AcceptDTO out = acceptMapper.toDTO(acc);
        long ms = System.currentTimeMillis() - t0;
        log.info("[MATCH][ACCEPT][OUT] acceptId={} inviteId={} matchId={} ({} ms)",
                acc.getId(), invite.getId(), m.getId(), ms);
        return out;
    }

    // ---------- CORE (cria match se não existir) ----------
    private Match createMatchIfAbsent(MatchInvite invite) {
        UUID low  = PairKeyUtil.low(invite.getInviterId(), invite.getTargetId());
        UUID high = PairKeyUtil.high(invite.getInviterId(), invite.getTargetId());
        log.debug("[MATCH][CREATE] pair low={}, high={}, fromInviteId={}", low, high, invite.getId());

        Optional<Match> existing = matchRepo.findByPairLowAndPairHigh(low, high);
        if (existing.isPresent()) {
            log.debug("[MATCH][CREATE] match já existia. matchId={}", existing.get().getId());
            return existing.get();
        }

        Match m = Match.builder()
                .userA(invite.getInviterId())
                .userB(invite.getTargetId())
                .pairLow(low)
                .pairHigh(high)
                .fromInvite(invite)      // <<< relação JPA
                .conviteMutuo(true)
                .build();

        try {
            Match saved = matchRepo.save(m);
            log.info("[MATCH][CREATE] match criado com sucesso. matchId={}", saved.getId());
            return saved;
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // corrida contra unique (pair_low, pair_high)
            log.warn("[MATCH][CREATE] Unique violation (race). Recarregando… cause={}", e.getMessage());
            return matchRepo.findByPairLowAndPairHigh(low, high)
                    .orElseThrow(() -> {
                        log.error("[MATCH][CREATE] match inserido concorrente mas não encontrado (inconsistência).");
                        return new ResponseStatusException(HttpStatus.CONFLICT, "match inserted concurrently but not found");
                    });
        }
    }

    // ---------- LIST ----------
    @Transactional(readOnly = true)
    public List<MatchDTO> listForUser(UUID userId) {
        log.info("[MATCH][LIST] listForUser userId={}", userId);
        List<MatchDTO> out = matchRepo.findAllByUserAOrUserB(userId, userId)
                .stream().map(matchMapper::toDTO).toList();
        log.info("[MATCH][LIST][OUT] userId={} total={}", userId, out.size());
        return out;
    }

    // ---------- helpers de log ----------
    private static Object safe(Object o) {
        return o == null ? "null" : o;
    }

    private static String pairStr(UUID a, UUID b) {
        return "(" + (a == null ? "null" : a) + " -> " + (b == null ? "null" : b) + ")";
    }
}
