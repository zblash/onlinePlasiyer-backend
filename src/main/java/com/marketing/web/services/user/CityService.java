package com.marketing.web.services.user;

import com.marketing.web.models.City;

import java.util.List;

public interface CityService {

    List<City> findAll();

    City findById(String id);

    City findByTitle(String title);

    City create(City city);

    City update(String id, City updatedCity);

    void delete(City city);
}
