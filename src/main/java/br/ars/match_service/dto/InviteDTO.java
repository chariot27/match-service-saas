package br.ars.match_service.dto;

import br.ars.match_service.domain.InviteStatus;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class InviteDTO {
    private UUID id;
    private UUID inviterId;
    private UUID targetId;
    private String inviterName;
    private String inviterPhone;
    private String inviterAvatar;
    private InviteStatus status;
    private OffsetDateTime createdAt;
}
