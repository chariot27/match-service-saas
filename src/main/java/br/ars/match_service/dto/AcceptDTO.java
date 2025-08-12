package br.ars.match_service.dto;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class AcceptDTO {
    private UUID id;
    private UUID inviteId;
    private String inviterName;
    private String inviterPhone;
    private String inviterAvatar;
    private OffsetDateTime createdAt;
}
