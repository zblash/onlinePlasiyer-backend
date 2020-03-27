package com.marketing.web;

import com.marketing.web.dtos.user.WritableRegister;
import com.marketing.web.enums.RoleType;
import com.marketing.web.enums.UnitType;
import com.marketing.web.models.*;
import com.marketing.web.repositories.*;
import com.marketing.web.services.product.ProductSpecifyService;
import com.marketing.web.services.storage.StorageService;
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

        List<Product> products = productPopulator(categories, user);

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
        user.setCity(states.get(0).getCity());
        user.setState(states.get(0));
        user.setStatus(true);
        userService.create(user, writableRegister.getRoleType());

        User user1 = UserMapper.writableRegisterToUser(writableRegister1);
        user1.setStatus(true);
        user1.setCity(states.get(0).getCity());
        user1.setState(states.get(0));
        user1.setActiveStates(states);
        User saved = userService.create(user1, writableRegister1.getRoleType());

        User user2 = UserMapper.writableRegisterToUser(writableRegister2);
        user2.setStatus(true);
        user2.setCity(states.get(0).getCity());
        user2.setState(states.get(0));
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
            for (int i = 0; i < 5; i++) {
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
                if (randomForUnitType > 2) {
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

    private List<Product> productPopulator(List<Category> categories, User user) {
        int j = 0;
        List<Product> productList = new ArrayList<>();
        for (Category category : categories) {
            j++;
            for (int i = 0; i < 10; i++) {
                Product product = new Product();
                product.setCategory(category);
                product.setName("Product - " + i + "_" + j);
                product.setStatus(true);
                product.setPhotoUrl(randomPhoto());
                product.setTax(rn(10, 20));
                product.addUser(user);
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
        Category category1 = new Category();
        category1.setName("Soğuk Içecekler");
        category1.setSubCategory(false);
        category1.setPhotoUrl(randomPhoto());
        categories.add(category1);

        Category category2 = new Category();
        category2.setName("Temizlik ve Hijyen Urünleri");
        category2.setSubCategory(false);
        category2.setPhotoUrl(randomPhoto());
        categories.add(category2);

        Category category3 = new Category();
        category3.setName("Sıcak Içecekler");
        category3.setSubCategory(false);
        category3.setPhotoUrl(randomPhoto());
        categories.add(category3);

        Category category4 = new Category();
        category4.setName("Kişisel Bakım ve Kozmetik");
        category4.setSubCategory(false);
        category4.setPhotoUrl(randomPhoto());
        categories.add(category4);

        Category category5 = new Category();
        category5.setName("Kuru Gıda ve Gıda Urünleri");
        category5.setSubCategory(false);
        category5.setPhotoUrl(randomPhoto());
        categories.add(category5);

        Category category6 = new Category();
        category6.setName("Atıştırmalıklar");
        category6.setSubCategory(false);
        category6.setPhotoUrl(randomPhoto());
        categories.add(category6);

        Category category7 = new Category();
        category7.setName("Kahvaltılıklar");
        category7.setSubCategory(false);
        category7.setPhotoUrl(randomPhoto());
        categories.add(category7);

        Category category8 = new Category();
        category8.setName("Süt ve Süt Urünleri");
        category8.setSubCategory(false);
        category8.setPhotoUrl(randomPhoto());
        categories.add(category8);

        Category category9 = new Category();
        category9.setName("Et ve Işlenmiş Et Urünleri");
        category9.setSubCategory(false);
        category9.setPhotoUrl(randomPhoto());
        categories.add(category9);

        Category category10 = new Category();
        category10.setName("Züccaciye Hırdavat");
        category10.setSubCategory(false);
        category10.setPhotoUrl(randomPhoto());
        categories.add(category10);

        Category category11 = new Category();
        category11.setName("Kırtasiye");
        category11.setSubCategory(false);
        category11.setPhotoUrl(randomPhoto());
        categories.add(category11);

        Category category12 = new Category();
        category12.setName("Kağıt, Bez ve Mendil Grubu");
        category12.setSubCategory(false);
        category12.setPhotoUrl(randomPhoto());
        categories.add(category12);

        Category category13 = new Category();
        category13.setName("Elektrik Elektronik");
        category13.setSubCategory(false);
        category13.setPhotoUrl(randomPhoto());
        categories.add(category13);

        Category category14 = new Category();
        category14.setName("Meyve ve sebze");
        category14.setSubCategory(false);
        category14.setPhotoUrl(randomPhoto());
        categories.add(category14);
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
        Category category1i1 = new Category();
        category1i1.setName("Gazlı Içecekler");
        category1i1.setSubCategory(true);
        category1i1.setParent(category1);
        category1i1.setPhotoUrl(randomPhoto());
        subCats.add(category1i1);

        Category category1i2 = new Category();
        category1i2.setName("Gazsız Içecekler");
        category1i2.setSubCategory(true);
        category1i2.setParent(category1);
        category1i2.setPhotoUrl(randomPhoto());
        subCats.add(category1i2);

        Category category1i3 = new Category();
        category1i3.setName("Sular ve Sodalar");
        category1i3.setSubCategory(true);
        category1i3.setParent(category1);
        category1i3.setPhotoUrl(randomPhoto());
        subCats.add(category1i3);

        Category category1i4 = new Category();
        category1i4.setName("Diğer Soğuk Içecekler");
        category1i4.setSubCategory(true);
        category1i4.setParent(category1);
        category1i4.setPhotoUrl(randomPhoto());
        subCats.add(category1i4);

        Category category2i1 = new Category();
        category2i1.setName("Kahve ve Kremalar");
        category2i1.setSubCategory(true);
        category2i1.setParent(category2);
        category2i1.setPhotoUrl(randomPhoto());
        subCats.add(category2i1);

        Category category2i2 = new Category();
        category2i2.setName("Siyah Caylar");
        category2i2.setSubCategory(true);
        category2i2.setParent(category2);
        category2i2.setPhotoUrl(randomPhoto());
        subCats.add(category2i2);

        Category category2i3 = new Category();
        category2i3.setName("Bitki Cayları");
        category2i3.setSubCategory(true);
        category2i3.setParent(category2);
        category2i3.setPhotoUrl(randomPhoto());
        subCats.add(category2i3);

        Category category2i4 = new Category();
        category2i4.setName("Toz Içecekler");
        category2i4.setSubCategory(true);
        category2i4.setParent(category2);
        category2i4.setPhotoUrl(randomPhoto());
        subCats.add(category2i4);

        Category category2i5 = new Category();
        category2i5.setName("Diğer Sıcak Içecekler");
        category2i5.setSubCategory(true);
        category2i5.setParent(category2);
        category2i5.setPhotoUrl(randomPhoto());
        subCats.add(category2i5);

        Category category3i1 = new Category();
        category3i1.setName("Camaşır Temizlik Urünleri El ve Matik");
        category3i1.setSubCategory(true);
        category3i1.setParent(category3);
        category3i1.setPhotoUrl(randomPhoto());
        subCats.add(category3i1);

        Category category3i2 = new Category();
        category3i2.setName("Bulaşık Temizlik Urünleri El ve Matik");
        category3i2.setSubCategory(true);
        category3i2.setParent(category3);
        category3i2.setPhotoUrl(randomPhoto());
        subCats.add(category3i2);

        Category category3i3 = new Category();
        category3i3.setName("Banyo Mutfak ve Yüzey Temizleyiciler");
        category3i3.setSubCategory(true);
        category3i3.setParent(category3);
        category3i3.setPhotoUrl(randomPhoto());
        subCats.add(category3i3);

        Category category3i4 = new Category();
        category3i4.setName("Kalıp ve Sıvı Sabunlar");
        category3i4.setSubCategory(true);
        category3i4.setParent(category3);
        category3i4.setPhotoUrl(randomPhoto());
        subCats.add(category3i4);

        Category category3i5 = new Category();
        category3i5.setName("Diğer Temizlik Urünleri");
        category3i5.setSubCategory(true);
        category3i5.setParent(category3);
        category3i5.setPhotoUrl(randomPhoto());
        subCats.add(category3i5);

        Category category4i1 = new Category();
        category4i1.setName("Saç Bakım Urünleri");
        category4i1.setSubCategory(true);
        category4i1.setParent(category4);
        category4i1.setPhotoUrl(randomPhoto());
        subCats.add(category4i1);

        Category category4i2 = new Category();
        category4i2.setName("Ağız Bakım Urünleri");
        category4i2.setSubCategory(true);
        category4i2.setParent(category4);
        category4i2.setPhotoUrl(randomPhoto());
        subCats.add(category4i2);

        Category category4i3 = new Category();
        category4i3.setName("Vücut Bakım Urünleri");
        category4i3.setSubCategory(true);
        category4i3.setParent(category4);
        category4i3.setPhotoUrl(randomPhoto());
        subCats.add(category4i3);

        Category category4i4 = new Category();
        category4i4.setName("Parfüm ve Deodorantlar");
        category4i4.setSubCategory(true);
        category4i4.setParent(category4);
        category4i4.setPhotoUrl(randomPhoto());
        subCats.add(category4i4);

        Category category4i5 = new Category();
        category4i5.setName("Krem ve Losyonlar");
        category4i5.setSubCategory(true);
        category4i5.setParent(category4);
        category4i5.setPhotoUrl(randomPhoto());
        subCats.add(category4i5);

        Category category4i6 = new Category();
        category4i6.setName("Makyaj ve Güzellik");
        category4i6.setSubCategory(true);
        category4i6.setParent(category4);
        category4i6.setPhotoUrl(randomPhoto());
        subCats.add(category4i6);

        Category category4i7 = new Category();
        category4i7.setName("Güneş Yağlı Koruyucular");
        category4i7.setSubCategory(true);
        category4i7.setParent(category4);
        category4i7.setPhotoUrl(randomPhoto());
        subCats.add(category4i7);

        Category category4i8 = new Category();
        category4i8.setName("Diğer Kişisel Bakım Urünleri");
        category4i8.setSubCategory(true);
        category4i8.setParent(category4);
        category4i8.setPhotoUrl(randomPhoto());
        subCats.add(category4i8);

        Category category5i1 = new Category();
        category5i1.setName("Bakliyat ve Makarna");
        category5i1.setSubCategory(true);
        category5i1.setParent(category5);
        category5i1.setPhotoUrl(randomPhoto());
        subCats.add(category5i1);

        Category category5i2 = new Category();
        category5i2.setName("Tuz Bulyon Corba ve Baharatlar");
        category5i2.setSubCategory(true);
        category5i2.setParent(category5);
        category5i2.setPhotoUrl(randomPhoto());
        subCats.add(category5i2);

        Category category5i3 = new Category();
        category5i3.setName("Paketli ve Toz Sekerler");
        category5i3.setSubCategory(true);
        category5i3.setParent(category5);
        category5i3.setPhotoUrl(randomPhoto());
        subCats.add(category5i3);

        Category category5i4 = new Category();
        category5i4.setName("Diğer Gıda Urünleri");
        category5i4.setSubCategory(true);
        category5i4.setParent(category5);
        category5i4.setPhotoUrl(randomPhoto());
        subCats.add(category5i4);

        Category category5i5 = new Category();
        category5i5.setName("Ketçap Mayonez ve Soslar");
        category5i5.setSubCategory(true);
        category5i5.setParent(category5);
        category5i5.setPhotoUrl(randomPhoto());
        subCats.add(category5i5);

        Category category5i6 = new Category();
        category5i6.setName("Konserveler Salçalar ve Turşular");
        category5i6.setSubCategory(true);
        category5i6.setParent(category5);
        category5i6.setPhotoUrl(randomPhoto());
        subCats.add(category5i6);

        Category category5i7 = new Category();
        category5i7.setName("Un ve Nişastalar");
        category5i7.setSubCategory(true);
        category5i7.setParent(category5);
        category5i7.setPhotoUrl(randomPhoto());
        subCats.add(category5i7);

        Category category5i8 = new Category();
        category5i8.setName("Tatlı ve Pasta Malzemesi");
        category5i8.setSubCategory(true);
        category5i8.setParent(category5);
        category5i8.setPhotoUrl(randomPhoto());
        subCats.add(category5i8);

        Category category6i1 = new Category();
        category6i1.setName("Kek ve Pastalar");
        category6i1.setSubCategory(true);
        category6i1.setParent(category6);
        category6i1.setPhotoUrl(randomPhoto());
        subCats.add(category6i1);

        Category category6i2 = new Category();
        category6i2.setName("Seker ve Sekerlemeler");
        category6i2.setSubCategory(true);
        category6i2.setParent(category6);
        category6i2.setPhotoUrl(randomPhoto());
        subCats.add(category6i2);

        Category category6i3 = new Category();
        category6i3.setName("Bisküvi ve Gofretler");
        category6i3.setSubCategory(true);
        category6i3.setParent(category6);
        category6i3.setPhotoUrl(randomPhoto());
        subCats.add(category6i3);

        Category category6i4 = new Category();
        category6i4.setName("Cikolata ve Cikolatalı Urünler");
        category6i4.setSubCategory(true);
        category6i4.setParent(category6);
        category6i4.setPhotoUrl(randomPhoto());
        subCats.add(category6i4);

        Category category6i5 = new Category();
        category6i5.setName("Cips ve Krakerler");
        category6i5.setSubCategory(true);
        category6i5.setParent(category6);
        category6i5.setPhotoUrl(randomPhoto());
        subCats.add(category6i5);

        Category category6i6 = new Category();
        category6i6.setName("Sakızlar");
        category6i6.setSubCategory(true);
        category6i6.setParent(category6);
        category6i6.setPhotoUrl(randomPhoto());
        subCats.add(category6i6);

        Category category6i7 = new Category();
        category6i7.setName("Unlu Mamüller");
        category6i7.setSubCategory(true);
        category6i7.setParent(category6);
        category6i7.setPhotoUrl(randomPhoto());
        subCats.add(category6i7);

        Category category6i8 = new Category();
        category6i8.setName("Kuruyemiş ve Kuru Meyveler");
        category6i8.setSubCategory(true);
        category6i8.setParent(category6);
        category6i8.setPhotoUrl(randomPhoto());
        subCats.add(category6i8);

        Category category6i9 = new Category();
        category6i9.setName("Diğer Atıştırmalıklar");
        category6i9.setSubCategory(true);
        category6i9.setParent(category6);
        category6i9.setPhotoUrl(randomPhoto());
        subCats.add(category6i9);

        Category category7i1 = new Category();
        category7i1.setName("Fındık Fıstık Kremaları Sürme Cikolatalar");
        category7i1.setSubCategory(true);
        category7i1.setParent(category7);
        category7i1.setPhotoUrl(randomPhoto());
        subCats.add(category7i1);

        Category category7i2 = new Category();
        category7i2.setName("Zeytinler");
        category7i2.setSubCategory(true);
        category7i2.setParent(category7);
        category7i2.setPhotoUrl(randomPhoto());
        subCats.add(category7i2);

        Category category7i3 = new Category();
        category7i3.setName("Tahin Pekmez Helva vb.");
        category7i3.setSubCategory(true);
        category7i3.setParent(category7);
        category7i3.setPhotoUrl(randomPhoto());
        subCats.add(category7i3);

        Category category7i4 = new Category();
        category7i4.setName("Yumurtalar");
        category7i4.setSubCategory(true);
        category7i4.setParent(category7);
        category7i4.setPhotoUrl(randomPhoto());
        subCats.add(category7i4);

        Category category7i5 = new Category();
        category7i5.setName("Reçel Bal vb.");
        category7i5.setSubCategory(true);
        category7i5.setParent(category7);
        category7i5.setPhotoUrl(randomPhoto());
        subCats.add(category7i5);

        Category category7i6 = new Category();
        category7i6.setName("Kahvaltılık Gevrekler");
        category7i6.setSubCategory(true);
        category7i6.setParent(category7);
        category7i6.setPhotoUrl(randomPhoto());
        subCats.add(category7i6);

        Category category7i7 = new Category();
        category7i7.setName("Kahvaltılık Soslar");
        category7i7.setSubCategory(true);
        category7i7.setParent(category7);
        category7i7.setPhotoUrl(randomPhoto());
        subCats.add(category7i7);

        Category category7i8 = new Category();
        category7i8.setName("Diğer Kahvaltılık Urünler");
        category7i8.setSubCategory(true);
        category7i8.setParent(category7);
        category7i8.setPhotoUrl(randomPhoto());
        subCats.add(category7i8);

        Category category8i1 = new Category();
        category8i1.setName("Süt Urünleri Pastörize Günlük Meyveli");
        category8i1.setSubCategory(true);
        category8i1.setParent(category8);
        category8i1.setPhotoUrl(randomPhoto());
        subCats.add(category8i1);

        Category category8i2 = new Category();
        category8i2.setName("Peynir Ceşitleri");
        category8i2.setSubCategory(true);
        category8i2.setParent(category8);
        category8i2.setPhotoUrl(randomPhoto());
        subCats.add(category8i2);

        Category category8i3 = new Category();
        category8i3.setName("Yoğurt ve Ayranlar");
        category8i3.setSubCategory(true);
        category8i3.setParent(category8);
        category8i3.setPhotoUrl(randomPhoto());
        subCats.add(category8i3);

        Category category8i4 = new Category();
        category8i4.setName("Sütlü Tatlılar");
        category8i4.setSubCategory(true);
        category8i4.setParent(category8);
        category8i4.setPhotoUrl(randomPhoto());
        subCats.add(category8i4);

        Category category8i5 = new Category();
        category8i5.setName("Diğer Süt Urünleri");
        category8i5.setSubCategory(true);
        category8i5.setParent(category8);
        category8i5.setPhotoUrl(randomPhoto());
        subCats.add(category8i5);

        Category category8i6 = new Category();
        category8i6.setName("Toz Sütlü Içecekler");
        category8i6.setSubCategory(true);
        category8i6.setParent(category8);
        category8i6.setPhotoUrl(randomPhoto());
        subCats.add(category8i6);

        Category category8i7 = new Category();
        category8i7.setName("Toz Sütlü Tatlılar");
        category8i7.setSubCategory(true);
        category8i7.setParent(category8);
        category8i7.setPhotoUrl(randomPhoto());
        subCats.add(category8i7);

        Category category9i1 = new Category();
        category9i1.setName("Balık ve Deniz Urünleri");
        category9i1.setSubCategory(true);
        category9i1.setParent(category9);
        category9i1.setPhotoUrl(randomPhoto());
        subCats.add(category9i1);

        Category category9i2 = new Category();
        category9i2.setName("Kırmızı Et Urünleri");
        category9i2.setSubCategory(true);
        category9i2.setParent(category9);
        category9i2.setPhotoUrl(randomPhoto());
        subCats.add(category9i2);

        Category category9i3 = new Category();
        category9i3.setName("Piliç ve Kanatlar");
        category9i3.setSubCategory(true);
        category9i3.setParent(category9);
        category9i3.setPhotoUrl(randomPhoto());
        subCats.add(category9i3);

        Category category9i4 = new Category();
        category9i4.setName("Işlenmiş Et Urünleri");
        category9i4.setSubCategory(true);
        category9i4.setParent(category9);
        category9i4.setPhotoUrl(randomPhoto());
        subCats.add(category9i4);

        Category category9i5 = new Category();
        category9i5.setName("Donuk Urünler");
        category9i5.setSubCategory(true);
        category9i5.setParent(category9);
        category9i5.setPhotoUrl(randomPhoto());
        subCats.add(category9i5);

        Category category9i6 = new Category();
        category9i6.setName("Diğer Et Urünleri");
        category9i6.setSubCategory(true);
        category9i6.setParent(category9);
        category9i6.setPhotoUrl(randomPhoto());
        subCats.add(category9i6);

        Category category10i1 = new Category();
        category10i1.setName("Plastik Züccaciye Urünleri");
        category10i1.setSubCategory(true);
        category10i1.setParent(category10);
        category10i1.setPhotoUrl(randomPhoto());
        subCats.add(category10i1);

        Category category10i2 = new Category();
        category10i2.setName("Çelik Alüminyum Granit Urünler");
        category10i2.setSubCategory(true);
        category10i2.setParent(category10);
        category10i2.setPhotoUrl(randomPhoto());
        subCats.add(category10i2);

        Category category10i3 = new Category();
        category10i3.setName("Porselen Mutfak Urünleri");
        category10i3.setSubCategory(true);
        category10i3.setParent(category10);
        category10i3.setPhotoUrl(randomPhoto());
        subCats.add(category10i3);

        Category category10i4 = new Category();
        category10i4.setName("Cam Mutfak Urünleri");
        category10i4.setSubCategory(true);
        category10i4.setParent(category10);
        category10i4.setPhotoUrl(randomPhoto());
        subCats.add(category10i4);

        Category category10i5 = new Category();
        category10i5.setName("Catal Bıçak ve Benzeri Mutfak Gereçleri");
        category10i5.setSubCategory(true);
        category10i5.setParent(category10);
        category10i5.setPhotoUrl(randomPhoto());
        subCats.add(category10i5);

        Category category10i6 = new Category();
        category10i6.setName("İnşaat Bahçe ve Nalbur Malzemeleri");
        category10i6.setSubCategory(true);
        category10i6.setParent(category10);
        category10i6.setPhotoUrl(randomPhoto());
        subCats.add(category10i6);

        Category category10i7 = new Category();
        category10i7.setName("Mutfak ve Banyo Gereçleri");
        category10i7.setSubCategory(true);
        category10i7.setParent(category10);
        category10i7.setPhotoUrl(randomPhoto());
        subCats.add(category10i7);

        Category category10i8 = new Category();
        category10i8.setName("Giysi ve Ayakkabı Gereçleri");
        category10i8.setSubCategory(true);
        category10i8.setParent(category10);
        category10i8.setPhotoUrl(randomPhoto());
        subCats.add(category10i8);

        Category category10i9 = new Category();
        category10i9.setName("Diğer Hırdavat Ceşitleri");
        category10i9.setSubCategory(true);
        category10i9.setParent(category10);
        category10i9.setPhotoUrl(randomPhoto());
        subCats.add(category10i9);

        Category category10i10 = new Category();
        category10i10.setName("Diğer Züccaciye Ceşitleri");
        category10i10.setSubCategory(true);
        category10i10.setParent(category10);
        category10i10.setPhotoUrl(randomPhoto());
        subCats.add(category10i10);

        Category category11i1 = new Category();
        category11i1.setName("Defter ve Kağıt Ceşitleri");
        category11i1.setSubCategory(true);
        category11i1.setParent(category11);
        category11i1.setPhotoUrl(randomPhoto());
        subCats.add(category11i1);

        Category category11i2 = new Category();
        category11i2.setName("Kalem ve Kalem Boyama Ceşitleri");
        category11i2.setSubCategory(true);
        category11i2.setParent(category11);
        category11i2.setPhotoUrl(randomPhoto());
        subCats.add(category11i2);

        Category category11i3 = new Category();
        category11i3.setName("Silgi Açacak vb. Yazı Malzemeleri");
        category11i3.setSubCategory(true);
        category11i3.setParent(category11);
        category11i3.setPhotoUrl(randomPhoto());
        subCats.add(category11i3);

        Category category11i4 = new Category();
        category11i4.setName("Canta ve Okul Aksesuarları");
        category11i4.setSubCategory(true);
        category11i4.setParent(category11);
        category11i4.setPhotoUrl(randomPhoto());
        subCats.add(category11i4);

        Category category12i1 = new Category();
        category12i1.setName("Cocuk Bezleri");
        category12i1.setSubCategory(true);
        category12i1.setParent(category12);
        category12i1.setPhotoUrl(randomPhoto());
        subCats.add(category12i1);

        Category category12i2 = new Category();
        category12i2.setName("Hijyenik Kadın Pedleri");
        category12i2.setSubCategory(true);
        category12i2.setParent(category12);
        category12i2.setPhotoUrl(randomPhoto());
        subCats.add(category12i2);

        Category category12i3 = new Category();
        category12i3.setName("Hasta Bezleri");
        category12i3.setSubCategory(true);
        category12i3.setParent(category12);
        category12i3.setPhotoUrl(randomPhoto());
        subCats.add(category12i3);

        Category category12i4 = new Category();
        category12i4.setName("Islak Mendiller Bebek ve Cocuk");
        category12i4.setSubCategory(true);
        category12i4.setParent(category12);
        category12i4.setPhotoUrl(randomPhoto());
        subCats.add(category12i4);

        Category category12i5 = new Category();
        category12i5.setName("Tuvalet Kağıdı ve Havlu Peçeteler");
        category12i5.setSubCategory(true);
        category12i5.setParent(category12);
        category12i5.setPhotoUrl(randomPhoto());
        subCats.add(category12i5);

        Category category12i6 = new Category();
        category12i6.setName("Kare ve Yemek Peçeteleri");
        category12i6.setSubCategory(true);
        category12i6.setParent(category12);
        category12i6.setPhotoUrl(randomPhoto());
        subCats.add(category12i6);

        Category category12i7 = new Category();
        category12i7.setName("Diğer Kağıt Urünleri");
        category12i7.setSubCategory(true);
        category12i7.setParent(category12);
        category12i7.setPhotoUrl(randomPhoto());
        subCats.add(category12i7);

        Category category13i1 = new Category();
        category13i1.setName("Elektrikli Ev Aletleri");
        category13i1.setSubCategory(true);
        category13i1.setParent(category13);
        category13i1.setPhotoUrl(randomPhoto());
        subCats.add(category13i1);

        Category category13i2 = new Category();
        category13i2.setName("Elektrikli Isıtıcılar");
        category13i2.setSubCategory(true);
        category13i2.setParent(category13);
        category13i2.setPhotoUrl(randomPhoto());
        subCats.add(category13i2);

        Category category13i3 = new Category();
        category13i3.setName("Aydınlatma Urünleri");
        category13i3.setSubCategory(true);
        category13i3.setParent(category13);
        category13i3.setPhotoUrl(randomPhoto());
        subCats.add(category13i3);

        Category category13i4 = new Category();
        category13i4.setName("Elektrik Araç Gereçleri");
        category13i4.setSubCategory(true);
        category13i4.setParent(category13);
        category13i4.setPhotoUrl(randomPhoto());
        subCats.add(category13i4);

        Category category13i5 = new Category();
        category13i5.setName("Telefon Aksesuarları");
        category13i5.setSubCategory(true);
        category13i5.setParent(category13);
        category13i5.setPhotoUrl(randomPhoto());
        subCats.add(category13i5);

        Category category13i6 = new Category();
        category13i6.setName("Müzik ve Ses Cihazları");
        category13i6.setSubCategory(true);
        category13i6.setParent(category13);
        category13i6.setPhotoUrl(randomPhoto());
        subCats.add(category13i6);

        Category category13i7 = new Category();
        category13i7.setName("Diger Elektrik Aletler");
        category13i7.setSubCategory(true);
        category13i7.setParent(category13);
        category13i7.setPhotoUrl(randomPhoto());
        subCats.add(category13i7);

        Category category13i8 = new Category();
        category13i8.setName("Diğer Elektronik Cihazlar");
        category13i8.setSubCategory(true);
        category13i8.setParent(category13);
        category13i8.setPhotoUrl(randomPhoto());
        subCats.add(category13i8);

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
