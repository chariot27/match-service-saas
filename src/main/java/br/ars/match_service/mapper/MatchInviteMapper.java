package br.ars.match_service.mapper;

import br.ars.match_service.domain.MatchInvite;
import br.ars.match_service.dto.InviteDTO;
import br.ars.match_service.dto.InviteRequest;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface MatchInviteMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "inviterId", source = "inviterId")
    @Mapping(target = "targetId", source = "targetId")
    @Mapping(target = "inviterName", source = "inviterName")
    @Mapping(target = "inviterPhone", source = "inviterPhone")
    @Mapping(target = "inviterAvatar", source = "inviterAvatar")
    MatchInvite toEntity(InviteRequest request);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "inviterId", source = "inviterId")
    @Mapping(target = "targetId", source = "targetId")
    @Mapping(target = "inviterName", source = "inviterName")
    @Mapping(target = "inviterPhone", source = "inviterPhone")
    @Mapping(target = "inviterAvatar", source = "inviterAvatar")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "createdAt", source = "createdAt")
    InviteDTO toDTO(MatchInvite entity);
}
