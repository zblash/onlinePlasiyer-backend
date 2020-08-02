package com.marketing.web.services.user;

import com.marketing.web.configs.constants.MessagesConstants;
import com.marketing.web.errors.ResourceNotFoundException;
import com.marketing.web.models.City;
import com.marketing.web.repositories.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CityServiceImpl implements CityService {

    private final CityRepository cityRepository;

    public CityServiceImpl(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @Override
    public List<City> findAll() {
        return cityRepository.findAll();
    }

    @Override
    public City findById(String id) {
        return cityRepository.findById(UUID.fromString(id)).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"city",id.toString()));
    }

    @Override
    public City findByTitle(String title) {
        return cityRepository.findByTitle(title).orElseThrow(() -> new ResourceNotFoundException(MessagesConstants.RESOURCES_NOT_FOUND+"city",title));
    }

    @Override
    public City create(City city) {
        return cityRepository.save(city);
    }

    @Override
    public City update(String id, City updatedCity) {
        City city = findById(id);
        city.setCode(updatedCity.getCode());
        city.setTitle(updatedCity.getTitle());
        return cityRepository.save(city);
    }

    @Override
    public void delete(City city) {
        cityRepository.delete(city);
    }
}
