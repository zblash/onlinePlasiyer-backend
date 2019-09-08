package com.marketing.web.repositories;

import com.marketing.web.models.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CityRepository extends JpaRepository<City,Long> {

    Optional<City> findByTitle(String title);

    Optional<City> findByUuid(UUID uuid);
}
