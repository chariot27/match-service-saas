package br.ars.match_service.mapper;

import br.ars.match_service.domain.MatchInvite;
import br.ars.match_service.dto.InviteDTO;
import br.ars.match_service.dto.InviteRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(config = MapStructConfig.class, uses = { TimeMapper.class })
public interface MatchInviteMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "status", ignore = true),   // default = PENDING na entidade
        @Mapping(target = "createdAt", ignore = true),
        @Mapping(target = "updatedAt", ignore = true),
        @Mapping(target = "version",   ignore = true)
    })
    MatchInvite toEntity(InviteRequest in);

    // O MapStruct agora encontra os métodos do TimeMapper para:
    // Instant -> OffsetDateTime (createdAt, updatedAt)
    InviteDTO toDTO(MatchInvite entity);
}
