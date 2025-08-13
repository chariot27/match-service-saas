package br.ars.match_service.web;

import br.ars.match_service.domain.InviteStatus;
import br.ars.match_service.dto.*;
import br.ars.match_service.service.MatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/matches", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class MatchController {

    private final MatchService service;

    // --- Criar convite ---
    @PostMapping(value = "/invite", consumes = MediaType.APPLICATION_JSON_VALUE)
    public InviteResponse invite(@Valid @RequestBody InviteRequest request) {
        return service.invite(request);
    }

    // --- Aceitar convite ---
    @PostMapping(value = "/accept", consumes = MediaType.APPLICATION_JSON_VALUE)
    public AcceptDTO accept(@Valid @RequestBody AcceptRequest request) {
        return service.accept(request);
    }

    // --- Listar matches do usuário ---
    @GetMapping("/user/{userId}")
    public List<MatchDTO> myMatches(@PathVariable UUID userId) {
        return service.listForUser(userId);
    }

    // --- NOVOS: Listar convites enviados/recebidos (dados reais, sem fictícios) ---
    @GetMapping("/invites/sent")
    public List<InviteDTO> listSentInvites(
            @RequestParam("userId") UUID userId,
            @RequestParam(value = "status", required = false) InviteStatus status
    ) {
        return service.listSentInvites(userId, status);
    }

    @GetMapping("/invites/received")
    public List<InviteDTO> listReceivedInvites(
            @RequestParam("userId") UUID userId,
            @RequestParam(value = "status", required = false) InviteStatus status
    ) {
        return service.listReceivedInvites(userId, status);
    }
}
