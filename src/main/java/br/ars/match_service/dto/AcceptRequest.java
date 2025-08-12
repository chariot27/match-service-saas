package br.ars.match_service.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class AcceptRequest {
    private UUID inviteId;  // opcional
    private UUID inviterId; // usado se inviteId ausente
    private UUID targetId;  // usado se inviteId ausente
}
