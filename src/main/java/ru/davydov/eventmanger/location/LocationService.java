package ru.davydov.eventmanger.location;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository repository;
    private final LocationEntityMapper entityMapper;

    public List<Location> getAllLocations() {
        return repository.findAll()
                .stream()
                .map(entityMapper::toDomain)
                .toList();
    }

    public Location createLocation(Location location) {
        if (location.id() != null) {
            throw new IllegalArgumentException("Could not create location with provided id. Id must be null");
        }
        LocationEntity createdLocation = repository.save(entityMapper.toEntity(location));
        return entityMapper.toDomain(createdLocation);
    }

    public Location deleteLocation(Long locationId) {
        LocationEntity entityToDelete = repository.findById(locationId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Location entity with id=%s wasn't found".formatted(locationId)));
        repository.deleteById(locationId);
        return entityMapper.toDomain(entityToDelete);
    }

    public Location updateLocation(Long locationId, Location locationToUpdate) {
        if (locationToUpdate.id() != null) {
            throw new IllegalArgumentException("Could not create location with provided id. Id must be null");
        }
        LocationEntity entityToUpdate = repository.findById(locationId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Location entity with id=%s wasn't found".formatted(locationId)));

        entityToUpdate.setName(locationToUpdate.name());
        entityToUpdate.setAddress(locationToUpdate.address());
        entityToUpdate.setCapacity(locationToUpdate.capacity());
        entityToUpdate.setDescription(locationToUpdate.description());

        LocationEntity updatedEntity = repository.save(entityToUpdate);
        return entityMapper.toDomain(updatedEntity);
    }

    public Location getLocationById(Long locationId) {
        LocationEntity foundEntity = repository.findById(locationId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Location entity with id=%s wasn't found".formatted(locationId)));
        return entityMapper.toDomain(foundEntity);
    }
}
