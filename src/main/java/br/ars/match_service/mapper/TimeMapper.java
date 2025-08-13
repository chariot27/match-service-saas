package br.ars.match_service.mapper;

import org.mapstruct.Mapper;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface TimeMapper {

    default OffsetDateTime toOffsetDateTime(Instant value) {
        return value == null ? null : value.atOffset(ZoneOffset.UTC);
    }

    default Instant toInstant(OffsetDateTime value) {
        return value == null ? null : value.toInstant();
    }
}
