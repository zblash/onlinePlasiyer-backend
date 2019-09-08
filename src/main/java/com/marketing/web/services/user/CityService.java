package com.marketing.web.services.user;

import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.City;
import com.marketing.web.repositories.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CityService implements ICityService {

    @Autowired
    private CityRepository cityRepository;

    @Override
    public List<City> findAll() {
        return cityRepository.findAll();
    }

    @Override
    public City findById(Long id) {
        return cityRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("City not found with id: "+id));
    }

    @Override
    public City findByUuid(String uuid) {
        return cityRepository.findByUuid(UUID.fromString(uuid)).orElseThrow(() -> new ResourceNotFoundException("City not found with id: "+uuid));
    }

    @Override
    public City findByTitle(String title) {
        return cityRepository.findByTitle(title).orElseThrow(() -> new ResourceNotFoundException("City not found with title: "+title));
    }

    @Override
    public City create(City city) {
        return cityRepository.save(city);
    }

    @Override
    public City update(String uuid, City updatedCity) {
        City city = findByUuid(uuid);
        city.setCode(updatedCity.getCode());
        city.setTitle(updatedCity.getTitle());
        return cityRepository.save(city);
    }

    @Override
    public void delete(City city) {
        cityRepository.delete(city);
    }
}
