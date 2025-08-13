package br.ars.match_service.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.DynamicInsert;

import java.time.Instant;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@DynamicInsert // se algum campo vier null, o Hibernate omite a coluna e deixa o default do DB agir
@Entity
@Table(name = "match_invites",
       indexes = {
         @Index(name="idx_match_invites_inviter_target", columnList = "inviter_id,target_id", unique = true),
         @Index(name="idx_match_invites_target_status_created", columnList = "target_id,status,created_at"),
         @Index(name="idx_match_invites_inviter_status_created", columnList = "inviter_id,status,created_at")
       })
public class MatchInvite {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
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
    @Column(name = "status", nullable = false, length = 16)
    @Builder.Default
    private InviteStatus status = InviteStatus.PENDING; // ✅ default no Java

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Version
    @Column(name = "version")
    private long version;
}
