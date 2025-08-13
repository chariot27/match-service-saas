package br.ars.match_service.mapper;

import br.ars.match_service.domain.MatchAccept;
import br.ars.match_service.dto.AcceptDTO;
import org.mapstruct.*;

@Mapper(config = MapStructConfig.class)
public interface MatchAcceptMapper {

    @Mappings({
        @Mapping(target = "inviteId", source = "invite.id")
    })
    AcceptDTO toDTO(MatchAccept entity);
}
