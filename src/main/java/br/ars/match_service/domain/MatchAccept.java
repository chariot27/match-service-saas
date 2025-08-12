package br.ars.match_service.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("match_accepts")
public class MatchAccept {
    @Id
    private UUID id;

    @Column("invite_id")
    private UUID inviteId;

    @Column("inviter_name")
    private String inviterName;

    @Column("inviter_phone")
    private String inviterPhone;

    @Column("inviter_avatar")
    private String inviterAvatar;

    @Column("created_at")
    private OffsetDateTime createdAt;
}
