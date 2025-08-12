package br.ars.match_service.domain;

import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("match_invites")
public class MatchInvite {
    @Id
    private UUID id;

    @Column("inviter_id") private UUID inviterId;
    @Column("target_id")  private UUID targetId;

    @Column("inviter_name")   private String inviterName;
    @Column("inviter_phone")  private String inviterPhone;
    @Column("inviter_avatar") private String inviterAvatar;

    private InviteStatus status;

    @Column("created_at") private OffsetDateTime createdAt;
    @Column("updated_at") private OffsetDateTime updatedAt;

    @Version private Long version;
}
