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
@Table(name = "match_accepts",
       indexes = @Index(name="idx_match_accepts_invite", columnList = "invite_id"))
public class MatchAccept {

    @Id
    @GeneratedValue @UuidGenerator
    @Column(name = "id", columnDefinition="uuid", nullable=false, updatable=false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="invite_id",
        nullable=false,
        foreignKey=@ForeignKey(name="fk_accept_invite"))
    private MatchInvite invite;

    @Column(name = "inviter_name")
    private String inviterName;

    @Column(name = "inviter_phone")
    private String inviterPhone;

    @Column(name = "inviter_avatar")
    private String inviterAvatar;

    @CreationTimestamp
    @Column(name = "created_at", nullable=false, updatable=false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable=false)
    private OffsetDateTime updatedAt;

    @Version
    @Column(name = "version", nullable=false)
    private Long version;
}
