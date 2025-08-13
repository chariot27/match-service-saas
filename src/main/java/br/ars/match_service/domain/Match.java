package br.ars.match_service.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "matches",
       uniqueConstraints = @UniqueConstraint(name="uq_pair",
                        columnNames={"pair_low","pair_high"}),
       indexes = {
           @Index(name="idx_matches_user_a",    columnList="user_a"),
           @Index(name="idx_matches_user_b",    columnList="user_b"),
           @Index(name="idx_matches_pair_low",  columnList="pair_low"),
           @Index(name="idx_matches_pair_high", columnList="pair_high")
       })
@Check(constraints = "pair_low < pair_high")
public class Match {

    @Id
    @GeneratedValue @UuidGenerator
    @Column(name = "id", columnDefinition="uuid", nullable=false, updatable=false)
    private UUID id;

    @Column(name = "user_a", nullable=false, columnDefinition="uuid")
    private UUID userA;

    @Column(name = "user_b", nullable=false, columnDefinition="uuid")
    private UUID userB;

    @Column(name = "pair_low",  nullable=false, columnDefinition="uuid")
    private UUID pairLow;

    @Column(name = "pair_high", nullable=false, columnDefinition="uuid")
    private UUID pairHigh;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="from_invite_id",
        foreignKey=@ForeignKey(name="fk_match_from_invite"))
    private MatchInvite fromInvite;

    @Column(name = "convite_mutuo", nullable=false)
    private boolean conviteMutuo = true;

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
