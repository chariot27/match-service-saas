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
@Table(
    name = "matches",
    uniqueConstraints = @UniqueConstraint(name = "uq_pair", columnNames = {"pair_low", "pair_high"}),
    indexes = {
        @Index(name = "idx_matches_user_a",    columnList = "user_a"),
        @Index(name = "idx_matches_user_b",    columnList = "user_b"),
        @Index(name = "idx_matches_pair_low",  columnList = "pair_low"),
        @Index(name = "idx_matches_pair_high", columnList = "pair_high")
    }
)
/**
 * Mantém coerência: user_a = pair_low, user_b = pair_high e low < high.
 * (Garante compatibilidade com a constraint de banco 'chk_pair_order')
 */
@Check(constraints = "user_a = pair_low AND user_b = pair_high AND pair_low < pair_high")
public class Match {

    @Id
    @GeneratedValue @UuidGenerator
    @Column(name = "id", columnDefinition = "uuid", nullable = false, updatable = false)
    private UUID id;

    /** SEMPRE o menor UUID do par */
    @Column(name = "user_a", nullable = false, columnDefinition = "uuid")
    private UUID userA;

    /** SEMPRE o maior UUID do par */
    @Column(name = "user_b", nullable = false, columnDefinition = "uuid")
    private UUID userB;

    /** SEMPRE igual a userA */
    @Column(name = "pair_low", nullable = false, columnDefinition = "uuid")
    private UUID pairLow;

    /** SEMPRE igual a userB */
    @Column(name = "pair_high", nullable = false, columnDefinition = "uuid")
    private UUID pairHigh;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "from_invite_id",
        foreignKey = @ForeignKey(name = "fk_match_from_invite")
    )
    private MatchInvite fromInvite;

    @Column(name = "convite_mutuo", nullable = false)
    private boolean conviteMutuo = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @PrePersist
    @PreUpdate
    private void ensureOrderedPair() {
        // Fonte primária: se pair_* vierem nulos, usamos userA/userB; caso contrário, partimos dos users.
        UUID a = this.userA != null ? this.userA : this.pairLow;
        UUID b = this.userB != null ? this.userB : this.pairHigh;

        if (a == null || b == null) {
            throw new IllegalStateException("userA/userB (ou pairLow/pairHigh) obrigatórios");
        }
        if (a.equals(b)) {
            throw new IllegalStateException("userA e userB não podem ser iguais");
        }

        boolean aMenor = a.compareTo(b) <= 0;
        UUID low  = aMenor ? a : b;
        UUID high = aMenor ? b : a;

        // Alinha tudo: users e pairs
        this.userA   = low;
        this.userB   = high;
        this.pairLow = low;
        this.pairHigh= high;
    }
}
