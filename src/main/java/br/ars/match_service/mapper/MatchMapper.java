package br.ars.match_service.mapper;

import br.ars.match_service.domain.Match;
import br.ars.match_service.dto.MatchDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface MatchMapper {
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "userA", source = "userA")
    @Mapping(target = "userB", source = "userB")
    @Mapping(target = "conviteMutuo", source = "conviteMutuo")
    @Mapping(target = "createdAt", source = "createdAt")
    MatchDTO toDTO(Match entity);
}
