package ru.davydov.eventmanger.location.mappers;

import org.springframework.stereotype.Component;
import ru.davydov.eventmanger.location.Location;
import ru.davydov.eventmanger.location.LocationEntity;

@Component
public class LocationEntityMapper {

    public Location toDomain(LocationEntity locationEntity) {
        return new Location(
                locationEntity.getId(),
                locationEntity.getName(),
                locationEntity.getAddress(),
                locationEntity.getCapacity(),
                locationEntity.getDescription()
        );
    }

    public LocationEntity toEntity(Location location) {
        return new LocationEntity(
                location.id(),
                location.name(),
                location.address(),
                location.capacity(),
                location.description()
        );
    }
}
