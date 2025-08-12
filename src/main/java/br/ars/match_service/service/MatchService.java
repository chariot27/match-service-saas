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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
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
    public Mono<InviteResponse> invite(InviteRequest request) {
        if (request.getInviterId().equals(request.getTargetId())) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "inviterId == targetId"));
        }
        UUID low = PairKeyUtil.low(request.getInviterId(), request.getTargetId());
        UUID high = PairKeyUtil.high(request.getInviterId(), request.getTargetId());

        return matchRepo.existsByPairLowAndPairHigh(low, high)
            .flatMap(exists -> exists
                ? matchRepo.findByPairLowAndPairHigh(low, high)
                    .map(m -> InviteResponse.builder().matched(true).matchId(m.getId()).build())
                : processInvite(request));
    }

    private Mono<InviteResponse> processInvite(InviteRequest request) {
        return inviteRepo.findByInviterIdAndTargetId(request.getInviterId(), request.getTargetId())
            .flatMap(existing -> Mono.just(InviteResponse.builder()
                .matched(false)
                .invite(inviteMapper.toDTO(existing))
                .build()))
            .switchIfEmpty(Mono.defer(() -> {
                MatchInvite entity = inviteMapper.toEntity(request);
                entity.setId(UUID.randomUUID());
                entity.setStatus(InviteStatus.PENDING);
                entity.setCreatedAt(OffsetDateTime.now());
                return inviteRepo.save(entity)
                    .onErrorResume(DuplicateKeyException.class, ex ->
                        inviteRepo.findByInviterIdAndTargetId(request.getInviterId(), request.getTargetId())
                    )
                    .flatMap(this::checkOppositeOrReturn);
            }));
    }

    private Mono<InviteResponse> checkOppositeOrReturn(MatchInvite savedInvite) {
        return inviteRepo.findByInviterIdAndTargetId(savedInvite.getTargetId(), savedInvite.getInviterId())
            .flatMap(opposite -> createMatchFromPair(savedInvite, opposite))
            .switchIfEmpty(Mono.just(InviteResponse.builder()
                .matched(false)
                .invite(inviteMapper.toDTO(savedInvite))
                .build()));
    }

    @Transactional
    public Mono<AcceptDTO> accept(AcceptRequest req) {
        Mono<MatchInvite> find = (req.getInviteId() != null)
            ? inviteRepo.findById(req.getInviteId())
            : inviteRepo.findByInviterIdAndTargetId(req.getInviterId(), req.getTargetId());

        return find.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "invite not found")))
            .flatMap(invite -> {
                if (invite.getStatus() == InviteStatus.PENDING) {
                    invite.setStatus(InviteStatus.ACCEPTED);
                }
                return inviteRepo.save(invite)
                    .then(createMatchIfAbsent(invite))
                    .flatMap(m -> {
                        MatchAccept acc = MatchAccept.builder()
                            .id(UUID.randomUUID())
                            .inviteId(invite.getId())
                            .inviterName(invite.getInviterName())
                            .inviterPhone(invite.getInviterPhone())
                            .inviterAvatar(invite.getInviterAvatar())
                            .createdAt(OffsetDateTime.now())
                            .build();
                        return acceptRepo.save(acc).map(acceptMapper::toDTO);
                    });
            });
    }

    private Mono<InviteResponse> createMatchFromPair(MatchInvite a, MatchInvite b) {
        return createMatchIfAbsent(a)
            .map(m -> InviteResponse.builder().matched(true).matchId(m.getId()).build());
    }

    private Mono<Match> createMatchIfAbsent(MatchInvite invite) {
        UUID low = PairKeyUtil.low(invite.getInviterId(), invite.getTargetId());
        UUID high = PairKeyUtil.high(invite.getInviterId(), invite.getTargetId());

        return matchRepo.existsByPairLowAndPairHigh(low, high)
            .flatMap(exists -> exists
                ? matchRepo.findByPairLowAndPairHigh(low, high)
                : tryInsertMatch(invite, low, high));
    }

    private Mono<Match> tryInsertMatch(MatchInvite invite, UUID low, UUID high) {
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

        return matchRepo.save(m)
            .onErrorResume(DuplicateKeyException.class, ex ->
                matchRepo.findByPairLowAndPairHigh(low, high));
    }

    public Flux<MatchDTO> listForUser(UUID userId) {
        return matchRepo.findAllByUserAOrUserB(userId, userId)
            .map(matchMapper::toDTO);
    }
}
