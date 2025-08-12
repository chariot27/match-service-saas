package br.ars.match_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class InviteRequest {
    @NotNull private UUID inviterId;
    @NotNull private UUID targetId;

    private String inviterName;
    private String inviterPhone;
    private String inviterAvatar;
}
