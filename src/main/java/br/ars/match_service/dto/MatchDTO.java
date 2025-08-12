package br.ars.match_service.dto;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class MatchDTO {
    private UUID id;
    private UUID userA;
    private UUID userB;
    private boolean conviteMutuo;
    private OffsetDateTime createdAt;
}
