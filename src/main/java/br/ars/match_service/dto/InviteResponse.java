package br.ars.match_service.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class InviteResponse {
    private boolean matched;
    private UUID matchId;     // se matched=true
    private InviteDTO invite; // eco do convite quando ainda não formou match
}
