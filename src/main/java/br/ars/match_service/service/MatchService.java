package br.ars.match_service.service;

import br.ars.match_service.domain.*;
import br.ars.match_service.dto.*;
import br.ars.match_service.mapper.*;
import br.ars.match_service.repo.*;
import br.ars.match_service.util.PairKeyUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchInviteRepository inviteRepo;
    private final MatchRepository matchRepo;
    private final MatchAcceptRepository acceptRepo;
    private final MatchInviteMapper inviteMapper;
    private final MatchMapper matchMapper;
    private final MatchAcceptMapper acceptMapper;

    @Transactional
    public InviteResponse invite(InviteRequest request) {
        if (request.getInviterId().equals(request.getTargetId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "inviterId == targetId");
        }
        UUID low = PairKeyUtil.low(request.getInviterId(), request.getTargetId());
        UUID high = PairKeyUtil.high(request.getInviterId(), request.getTargetId());

        if (matchRepo.existsByPairLowAndPairHigh(low, high)) {
            UUID matchId = matchRepo.findByPairLowAndPairHigh(low, high)
                    .map(Match::getId)
                    .orElse(null);
            return InviteResponse.builder().matched(true).matchId(matchId).build();
        }

        Optional<MatchInvite> existing = inviteRepo.findByInviterIdAndTargetId(request.getInviterId(), request.getTargetId());
        if (existing.isPresent()) {
            return InviteResponse.builder().matched(false).invite(inviteMapper.toDTO(existing.get())).build();
        }

        MatchInvite entity = inviteMapper.toEntity(request);
        entity.setId(UUID.randomUUID());
        entity.setStatus(InviteStatus.PENDING);
        entity.setCreatedAt(OffsetDateTime.now());

        try {
            inviteRepo.save(entity);
        } catch (DuplicateKeyException e) {
            entity = inviteRepo.findByInviterIdAndTargetId(request.getInviterId(), request.getTargetId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "invite duplicate and not found"));
        }

        Optional<MatchInvite> opposite = inviteRepo.findByInviterIdAndTargetId(entity.getTargetId(), entity.getInviterId());
        if (opposite.isPresent()) {
            Match m = createMatchIfAbsent(entity);
            return InviteResponse.builder().matched(true).matchId(m.getId()).build();
        }

        return InviteResponse.builder().matched(false).invite(inviteMapper.toDTO(entity)).build();
    }

    @Transactional
    public AcceptDTO accept(AcceptRequest req) {
        MatchInvite invite = (req.getInviteId() != null)
                ? inviteRepo.findById(req.getInviteId()).orElse(null)
                : inviteRepo.findByInviterIdAndTargetId(req.getInviterId(), req.getTargetId()).orElse(null);

        if (invite == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "invite not found");

        if (invite.getStatus() == InviteStatus.PENDING) {
            invite.setStatus(InviteStatus.ACCEPTED);
            inviteRepo.save(invite);
        }

        Match m = createMatchIfAbsent(invite);

        MatchAccept acc = MatchAccept.builder()
                .id(UUID.randomUUID())
                .inviteId(invite.getId())
                .inviterName(invite.getInviterName())
                .inviterPhone(invite.getInviterPhone())
                .inviterAvatar(invite.getInviterAvatar())
                .createdAt(OffsetDateTime.now())
                .build();
        acceptRepo.save(acc);

        return acceptMapper.toDTO(acc);
    }

    private Match createMatchIfAbsent(MatchInvite invite) {
        UUID low = PairKeyUtil.low(invite.getInviterId(), invite.getTargetId());
        UUID high = PairKeyUtil.high(invite.getInviterId(), invite.getTargetId());

        Optional<Match> existing = matchRepo.findByPairLowAndPairHigh(low, high);
        if (existing.isPresent()) return existing.get();

        Match m = Match.builder()
                .id(UUID.randomUUID())
                .userA(invite.getInviterId())
                .userB(invite.getTargetId())
                .pairLow(low)
                .pairHigh(high)
                .fromInviteId(invite.getId())
                .conviteMutuo(true)
                .createdAt(OffsetDateTime.now())
                .build();

        try {
            return matchRepo.save(m);
        } catch (DuplicateKeyException e) {
            return matchRepo.findByPairLowAndPairHigh(low, high)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "match inserted concurrently but not found"));
        }
    }

    public List<MatchDTO> listForUser(UUID userId) {
        return matchRepo.findAllByUserAOrUserB(userId, userId)
                .stream().map(matchMapper::toDTO).toList();
    }
}
