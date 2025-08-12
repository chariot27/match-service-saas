package br.ars.match_service.web;

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

    @PostMapping(value = "/invite", consumes = MediaType.APPLICATION_JSON_VALUE)
    public InviteResponse invite(@Valid @RequestBody InviteRequest request) {
        return service.invite(request);
    }

    @PostMapping(value = "/accept", consumes = MediaType.APPLICATION_JSON_VALUE)
    public AcceptDTO accept(@RequestBody AcceptRequest request) {
        return service.accept(request);
    }

    @GetMapping("/user/{userId}")
    public List<MatchDTO> myMatches(@PathVariable UUID userId) {
        return service.listForUser(userId);
    }
}
