package com.marketing.web;

import com.marketing.web.dtos.user.WritableRegister;
import com.marketing.web.enums.RoleType;
import com.marketing.web.models.*;
import com.marketing.web.repositories.CategoryRepository;
import com.marketing.web.repositories.CityRepository;
import com.marketing.web.repositories.ProductRepository;
import com.marketing.web.repositories.StateRepository;
import com.marketing.web.services.storage.StorageService;
import com.marketing.web.services.user.AddressServiceImpl;
import com.marketing.web.services.user.UserServiceImpl;
import com.marketing.web.utils.mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;
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

    @Autowired
    UserServiceImpl userService;

    @Autowired
    AddressServiceImpl addressService;

    @Override
    public void run(String... args) throws Exception {
//        storageService.init();
//
//        statePopulator();
//
//        List<Category> categories = categoryPopulator();
//
//        productPopulator(categories);
//
//        userPopulator();

    }

    public void userPopulator() throws URISyntaxException {

        WritableRegister writableRegister = new WritableRegister();
        writableRegister.setUsername("admin");
        writableRegister.setName("Admin Account");
        writableRegister.setPassword("12345");
        writableRegister.setTaxNumber("TR23123123");
        writableRegister.setEmail("admin@admin.com");
        writableRegister.setDetails("falan");
        writableRegister.setRoleType(RoleType.ADMIN);


        WritableRegister writableRegister1 = new WritableRegister();
        writableRegister1.setUsername("merchant");
        writableRegister1.setName("Merchant Account");
        writableRegister1.setPassword("12345");
        writableRegister1.setTaxNumber("TR31313131");
        writableRegister1.setEmail("merchant@merchant.com");
        writableRegister1.setDetails("falan");
        writableRegister1.setRoleType(RoleType.MERCHANT);

        WritableRegister writableRegister2 = new WritableRegister();
        writableRegister2.setUsername("customer");
        writableRegister2.setName("Customer Account");
        writableRegister2.setPassword("12345");
        writableRegister2.setTaxNumber("TR4234234234");
        writableRegister2.setEmail("customer@customer.com");
        writableRegister2.setDetails("falan");
        writableRegister2.setRoleType(RoleType.CUSTOMER);

        User user = UserMapper.writableRegisterToUser(writableRegister);
        user.setStatus(true);
        userService.create(user,writableRegister.getRoleType());

        User user1 = UserMapper.writableRegisterToUser(writableRegister1);
        user1.setStatus(true);
        userService.create(user1,writableRegister1.getRoleType());

        User user2 = UserMapper.writableRegisterToUser(writableRegister2);
        user2.setStatus(true);
        userService.create(user2,writableRegister2.getRoleType());
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
            city.setTitle("ANKARA");
            cities.add(city);
            City city1 = new City();
            city1.setCode(7);
            city1.setTitle("ANTALYA");
            cities.add(city1);
            City city2 = new City();
            city2.setCode(34);
            city2.setTitle("ISTANBUL");
            cities.add(city2);
            cityRepository.saveAll(cities);

            List<State> states = new ArrayList<>();
            State state = new State();
            state.setCity(cityRepository.findByTitle("ANTALYA").orElse(null));
            state.setCode(700);
            state.setTitle("Manavgat");
            states.add(state);
            State state1 = new State();
            state1.setCity(cityRepository.findByTitle("ANTALYA").orElse(null));
            state1.setCode(750);
            state1.setTitle("Alanya");
            states.add(state1);
            State state2 = new State();
            state2.setCity(cityRepository.findByTitle("ANKARA").orElse(null));
            state2.setCode(600);
            state2.setTitle("Mamak");
            states.add(state2);
            State state3 = new State();
            state3.setCity(cityRepository.findByTitle("ANKARA").orElse(null));
            state3.setCode(600);
            state3.setTitle("Çankaya");
            states.add(state3);
            State state4 = new State();
            state4.setCity(cityRepository.findByTitle("ISTANBUL").orElse(null));
            state4.setCode(340);
            state4.setTitle("Beykoz");
            states.add(state4);
            stateRepository.saveAll(states);

    }

    private List<Category> categoryPopulator(){
        List<Category> categories = new ArrayList<>();
        Category category = new Category();
        category.setName("Kagit Urunleri");
        category.setPhotoUrl(randomPhoto());
        category.setSubCategory(false);
        categories.add(category);
        Category category1 = new Category();
        category1.setName("Temizlik Urunleri");
        category1.setSubCategory(false);
        category1.setPhotoUrl(randomPhoto());
        categories.add(category1);
        Category category2 = new Category();
        category2.setName("Kisisel Bakim Urunleri");
        category2.setSubCategory(false);
        category2.setPhotoUrl(randomPhoto());
        categories.add(category2);
        Category category3 = new Category();
        category3.setName("Icecek Urunleri");
        category3.setSubCategory(false);
        category3.setPhotoUrl(randomPhoto());
        categories.add(category3);
        Category category4 = new Category();
        category4.setName("Atistirmalik Urunler");
        category4.setSubCategory(false);
        category4.setPhotoUrl(randomPhoto());
        categories.add(category4);
        Category category5 = new Category();
        category5.setName("Kuru Gida Urunleri");
        category5.setSubCategory(false);
        category5.setPhotoUrl(randomPhoto());
        categories.add(category5);
        Category category6 = new Category();
        category6.setName("Sut Urunleri");
        category6.setSubCategory(false);
        category6.setPhotoUrl(randomPhoto());
        categories.add(category6);
        Category category7 = new Category();
        category7.setName("Sarkuteri Urunleri");
        category7.setSubCategory(false);
        category7.setPhotoUrl(randomPhoto());
        categories.add(category7);
        Category category8 = new Category();
        category8.setName("Tutun Urunleri");
        category8.setSubCategory(false);
        category8.setPhotoUrl(randomPhoto());
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
            subCat.setPhotoUrl(randomPhoto());
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
