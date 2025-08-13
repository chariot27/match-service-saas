package br.ars.match_service.mapper;

import br.ars.match_service.domain.MatchInvite;
import br.ars.match_service.dto.InviteDTO;
import br.ars.match_service.dto.InviteRequest;
import org.mapstruct.*;

@Mapper(config = MapStructConfig.class)
public interface MatchInviteMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "status", ignore = true),        // default PENDING na entidade
        @Mapping(target = "createdAt", ignore = true),
        @Mapping(target = "updatedAt", ignore = true),
        @Mapping(target = "version", ignore = true)
    })
    MatchInvite toEntity(InviteRequest in);

    InviteDTO toDTO(MatchInvite entity);
}
