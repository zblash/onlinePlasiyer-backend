package com.marketing.web;

import com.marketing.web.models.Category;
import com.marketing.web.models.City;
import com.marketing.web.models.Product;
import com.marketing.web.models.State;
import com.marketing.web.repositories.CategoryRepository;
import com.marketing.web.repositories.CityRepository;
import com.marketing.web.repositories.ProductRepository;
import com.marketing.web.repositories.StateRepository;
import com.marketing.web.services.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class Runner implements CommandLineRunner {

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    StorageService storageService;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ProductRepository productRepository;

    @Override
    public void run(String... args) throws Exception {
        storageService.init();

        statePopulator();

        List<Category> categories = categoryPopulator();

        productPopulator(categories);

    }

    private void productPopulator(List<Category> categories) {
        int i = 0;
        for (Category category : categories){
            i++;
            Product product = new Product();
            product.setBarcode(generateBarcode());
            product.setCategory(category);
            product.setName("Example-Product"+i);
            product.setStatus(true);
            product.setPhotoUrl(randomPhoto());
            product.setTax(18);
            productRepository.save(product);
        }
    }

    private void statePopulator(){
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

    private List<Category> categoryPopulator(){
        List<Category> categories = new ArrayList<>();
        Category category = new Category();
        category.setName("Kagit Urunleri");
        category.setPhotoUrl("https://picsum.photos/200");
        category.setSubCategory(false);
        categories.add(category);
        Category category1 = new Category();
        category1.setName("Temizlik Urunleri");
        category1.setSubCategory(false);
        category1.setPhotoUrl("https://picsum.photos/200");
        categories.add(category1);
        Category category2 = new Category();
        category2.setName("Kisisel Bakim Urunleri");
        category2.setSubCategory(false);
        category2.setPhotoUrl("https://picsum.photos/200");
        categories.add(category2);
        Category category3 = new Category();
        category3.setName("Icecek Urunleri");
        category3.setSubCategory(false);
        category3.setPhotoUrl("https://picsum.photos/200");
        categories.add(category3);
        Category category4 = new Category();
        category4.setName("Atistirmalik Urunler");
        category4.setSubCategory(false);
        category4.setPhotoUrl("https://picsum.photos/200");
        categories.add(category4);
        Category category5 = new Category();
        category5.setName("Kuru Gida Urunleri");
        category5.setSubCategory(false);
        category5.setPhotoUrl("https://picsum.photos/200");
        categories.add(category5);
        Category category6 = new Category();
        category6.setName("Sut Urunleri");
        category6.setSubCategory(false);
        category6.setPhotoUrl("https://picsum.photos/200");
        categories.add(category6);
        Category category7 = new Category();
        category7.setName("Sarkuteri Urunleri");
        category7.setSubCategory(false);
        category7.setPhotoUrl("https://picsum.photos/200");
        categories.add(category7);
        Category category8 = new Category();
        category8.setName("Tutun Urunleri");
        category8.setSubCategory(false);
        category8.setPhotoUrl("https://picsum.photos/200");
        categories.add(category8);

        List<Category> savedCategories = categoryRepository.saveAll(categories);
        List<Category> subCats = new ArrayList<>();
        int i = 0;
        for (Category baseCategory : savedCategories){
            i++;
            Category subCat = new Category();
            subCat.setName("Example Sub Category"+i);
            subCat.setSubCategory(true);
            subCat.setParent(baseCategory);
            subCat.setPhotoUrl("https://picsum.photos/200");
            subCats.add(subCat);
        }
        savedCategories.addAll(categoryRepository.saveAll(subCats));
        return savedCategories;
    }


    private String randomPhoto(){
        Random rand = new Random();
        return "https://picsum.photos/id/" + rand.nextInt(800) + "/200/200";
    }

    private String generateBarcode() {
        int length = 13;
        Random random = new Random();
        char[] digits = new char[length];
        digits[0] = (char) (random.nextInt(9) + '1');
        for (int i = 1; i < length; i++) {
            digits[i] = (char) (random.nextInt(10) + '0');
        }
        return new String(digits);
    }
}
