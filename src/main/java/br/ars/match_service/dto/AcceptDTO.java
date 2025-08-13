package br.ars.match_service.dto;

import lombok.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AcceptDTO {
    private UUID id;
    private UUID inviteId;          // mapeado de entity.invite.id
    private String inviterName;
    private String inviterPhone;
    private String inviterAvatar;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private Long version;
}
