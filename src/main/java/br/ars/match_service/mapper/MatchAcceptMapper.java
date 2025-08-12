package br.ars.match_service.mapper;

import br.ars.match_service.domain.MatchAccept;
import br.ars.match_service.dto.AcceptDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface MatchAcceptMapper {
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "inviteId", source = "inviteId")
    @Mapping(target = "inviterName", source = "inviterName")
    @Mapping(target = "inviterPhone", source = "inviterPhone")
    @Mapping(target = "inviterAvatar", source = "inviterAvatar")
    @Mapping(target = "createdAt", source = "createdAt")
    AcceptDTO toDTO(MatchAccept entity);
}
