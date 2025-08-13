package br.ars.match_service.dto;

import lombok.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class MatchDTO {
    private UUID id;
    private UUID userA;
    private UUID userB;
    private UUID pairLow;
    private UUID pairHigh;

    private UUID fromInviteId;      // <-- precisa existir p/ casar com o mapper
    private boolean conviteMutuo;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private Long version;
}
