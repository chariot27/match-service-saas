package br.ars.match_service.web;

import br.ars.match_service.dto.*;
import br.ars.match_service.service.MatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping(value = "/api/matches", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class MatchController {

    private final MatchService service;

    @PostMapping(value = "/invite", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<InviteResponse> invite(@Valid @RequestBody InviteRequest request) {
        return service.invite(request);
    }

    @PostMapping(value = "/accept", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<AcceptDTO> accept(@RequestBody AcceptRequest request) {
        return service.accept(request);
    }

    @GetMapping("/user/{userId}")
    public Flux<MatchDTO> myMatches(@PathVariable UUID userId) {
        return service.listForUser(userId);
    }
}
