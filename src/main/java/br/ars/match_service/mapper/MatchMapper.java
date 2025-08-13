package br.ars.match_service.mapper;

import br.ars.match_service.domain.Match;
import br.ars.match_service.dto.MatchDTO;
import org.mapstruct.*;

@Mapper(config = MapStructConfig.class)
public interface MatchMapper {

    @Mappings({
        @Mapping(target = "fromInviteId", source = "fromInvite.id")
    })
    MatchDTO toDTO(Match entity);
}
