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
@Table("matches")
public class Match {
    @Id
    private UUID id;

    @Column("user_a") private UUID userA;
    @Column("user_b") private UUID userB;

    @Column("pair_low")  private UUID pairLow;
    @Column("pair_high") private UUID pairHigh;

    @Column("from_invite_id") private UUID fromInviteId;

    @Column("convite_mutuo") private boolean conviteMutuo;

    @Column("created_at") private OffsetDateTime createdAt;
    @Column("updated_at") private OffsetDateTime updatedAt;

    @Version private Long version;
}
