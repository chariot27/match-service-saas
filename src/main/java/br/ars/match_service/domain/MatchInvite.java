package br.ars.match_service.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "match_invites",
       uniqueConstraints = @UniqueConstraint(name = "uq_inviter_target",
                       columnNames = {"inviter_id","target_id"}),
       indexes = {
           @Index(name="idx_match_invites_inviter", columnList = "inviter_id"),
           @Index(name="idx_match_invites_target",  columnList = "target_id")
       })
public class MatchInvite {

    @Id
    @GeneratedValue @UuidGenerator
    @Column(name = "id", columnDefinition = "uuid", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "inviter_id", nullable = false, columnDefinition = "uuid")
    private UUID inviterId;

    @Column(name = "target_id", nullable = false, columnDefinition = "uuid")
    private UUID targetId;

    @Column(name = "inviter_name")
    private String inviterName;

    @Column(name = "inviter_phone")
    private String inviterPhone;

    @Column(name = "inviter_avatar")
    private String inviterAvatar;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InviteStatus status = InviteStatus.PENDING;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;
}
