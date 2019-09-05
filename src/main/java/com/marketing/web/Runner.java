package com.marketing.web;

import com.marketing.web.models.Category;
import com.marketing.web.models.City;
import com.marketing.web.models.State;
import com.marketing.web.repositories.CityRepository;
import com.marketing.web.repositories.StateRepository;
import com.marketing.web.services.category.CategoryService;
import com.marketing.web.services.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import java.util.ArrayList;
import java.util.List;

public class Runner implements CommandLineRunner {

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    StorageService storageService;

    @Autowired
    CategoryService categoryService;

    @Override
    public void run(String... args) throws Exception {
        storageService.init();

        if (cityRepository.findAll().isEmpty()) {
            List<City> cities = new ArrayList<>();
            City city = new City();
            city.setCode(6);
            city.setTitle("Ankara");
            cities.add(city);
            City city1 = new City();
            city1.setCode(7);
            city1.setTitle("Antalya");
            cities.add(city1);
            City city2 = new City();
            city2.setCode(34);
            city2.setTitle("İstanbul");
            cities.add(city2);
            cityRepository.saveAll(cities);

            List<State> states = new ArrayList<>();
            State state = new State();
            state.setCity(cityRepository.findByTitle("Antalya").orElse(null));
            state.setCode(700);
            state.setTitle("Manavgat");
            states.add(state);
            State state1 = new State();
            state1.setCity(cityRepository.findByTitle("Antalya").orElse(null));
            state1.setCode(750);
            state1.setTitle("Alanya");
            states.add(state1);
            State state2 = new State();
            state2.setCity(cityRepository.findByTitle("Ankara").orElse(null));
            state2.setCode(600);
            state2.setTitle("Mamak");
            states.add(state2);
            State state3 = new State();
            state3.setCity(cityRepository.findByTitle("ANKARA").orElse(null));
            state3.setCode(600);
            state3.setTitle("Çankaya");
            states.add(state3);
            State state4 = new State();
            state4.setCity(cityRepository.findByTitle("İstanbul").orElse(null));
            state4.setCode(340);
            state4.setTitle("Beykoz");
            states.add(state4);
            stateRepository.saveAll(states);
        }

        Category category = new Category();
        category.setName("Kagit Urunleri");
    }
}
