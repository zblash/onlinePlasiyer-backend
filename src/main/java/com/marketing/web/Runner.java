package com.marketing.web;

import com.marketing.web.dtos.user.WritableRegister;
import com.marketing.web.enums.RoleType;
import com.marketing.web.enums.UnitType;
import com.marketing.web.models.*;
import com.marketing.web.repositories.*;
import com.marketing.web.services.product.ProductSpecifyService;
import com.marketing.web.services.storage.StorageService;
import com.marketing.web.services.user.AddressServiceImpl;
import com.marketing.web.services.user.UserServiceImpl;
import com.marketing.web.utils.mappers.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Component
public class Runner implements CommandLineRunner {
    private Random r = new Random();

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
    BarcodeRepository barcodeRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserServiceImpl userService;

    @Autowired
    AddressServiceImpl addressService;

    @Autowired
    ProductSpecifyService productSpecifyService;

    @Override
    public void run(String... args) throws Exception {
        populator();
    }

    public void dropTables() {
        categoryRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();
        stateRepository.deleteAll();
        cityRepository.deleteAll();
    }

    public void populator() throws URISyntaxException {

        List<State> states = statePopulator();

        User user = userPopulator(states);

        List<Category> categories = categoryPopulator();

        List<Product> products = productPopulator(categories);

        barcodePopulator(products);

        productSpecifyPopulator(products, user);
    }


    public User userPopulator(List<State> states) throws URISyntaxException {

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
        userService.create(user, writableRegister.getRoleType());

        User user1 = UserMapper.writableRegisterToUser(writableRegister1);
        user1.setStatus(true);
        user1.setActiveStates(states);
        User saved = userService.create(user1, writableRegister1.getRoleType());

        User user2 = UserMapper.writableRegisterToUser(writableRegister2);
        user2.setStatus(true);
        userService.create(user2, writableRegister2.getRoleType());
        return saved;
    }

    private List<Barcode> barcodePopulator(List<Product> products) {
        List<Barcode> barcodes = new ArrayList<>();

        for (Product product : products) {
            for (int i = 0; i < 4; i++) {
                Barcode barcode = new Barcode();
                barcode.setProduct(product);
                barcode.setBarcodeNo(generateBarcode());
                barcodeRepository.save(barcode);
                barcodes.add(barcode);
            }
        }
        return barcodes;
    }

    private List<ProductSpecify> productSpecifyPopulator(List<Product> products, User user) {
        List<ProductSpecify> productSpecifies = new ArrayList<>();

        for (Product product : products) {
            for (int i = 0; i < rn(15, 35); i++) {
                List<State> states = new ArrayList<>(user.getActiveStates());
                ProductSpecify productSpecify = new ProductSpecify();
                productSpecify.setProduct(product);
                productSpecify.setRecommendedRetailPrice(rn(15, 999));
                int randomForUnitType = rn(1, 3);
                if (randomForUnitType == 1) {
                    productSpecify.setUnitType(UnitType.KG);
                }
                if (randomForUnitType == 2) {
                    productSpecify.setUnitType(UnitType.KL);
                }
                if (randomForUnitType == 2) {
                    productSpecify.setUnitType(UnitType.AD);
                }
                productSpecify.setContents(rn(15, 1500));
                productSpecify.setQuantity(rn(99, 1500));
                productSpecify.setUnitPrice(rn(2, 1500));
                productSpecify.setTotalPrice(rn(5, 1500));
                productSpecify.setStates(states);
                productSpecify.setUser(user);
                productSpecifies.add(productSpecify);
                productSpecifyService.create(productSpecify);

            }

        }

        return productSpecifies;
    }

    private List<Product> productPopulator(List<Category> categories) {
        int j = 0;
        List<Product> productList = new ArrayList<>();
        for (Category category : categories) {
            j++;
            for (int i = 0; i < rn(15, 35); i++) {
                Product product = new Product();
                product.setCategory(category);
                product.setName("Product - " + i + "_" + j);
                product.setStatus(true);
                product.setPhotoUrl(randomPhoto());
                product.setTax(rn(10, 20));
                productRepository.save(product);
                productList.add(product);
            }
        }
        return productList;
    }

    private List<State> statePopulator() {
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
        return stateRepository.saveAll(states);

    }

    private List<Category> categoryPopulator() {
        List<Category> categories = new ArrayList<>();
        for (int i = 0; i < +9; i++) {
            Category category = new Category();
            category.setName("Category - " + i);
            category.setPhotoUrl(randomPhoto());
            category.setSubCategory(false);
            categories.add(category);
        }
        List<Category> savedCategories = categoryRepository.saveAll(categories);
        List<Category> subCats = new ArrayList<>();
        for (Category baseCategory : savedCategories) {
            for (int i = 0; i < rn(15, 35); i++) {
                Category subCat = new Category();
                subCat.setName("Sub Category - " + i);
                subCat.setSubCategory(true);
                subCat.setParent(baseCategory);
                subCat.setPhotoUrl(randomPhoto());
                subCats.add(subCat);
            }
        }
        savedCategories.addAll(categoryRepository.saveAll(subCats));
        return savedCategories;
    }


    private String randomPhoto() {
        return "https://picsum.photos/" + rn(500, 1000) + "/" + rn(500, 1000);
    }

    private String generateBarcode() {
        int length = 13;
        char[] digits = new char[length];
        digits[0] = (char) (r.nextInt(9) + '1');
        for (int i = 1; i < length; i++) {
            digits[i] = (char) (r.nextInt(10) + '0');
        }
        return new String(digits);
    }

    private int rn(int min, int max) {
        return r.nextInt((max - min) + 1) + min;
    }
}
