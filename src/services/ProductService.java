package services;
import enums.Category;

import java.util.Arrays;
import java.util.List;

import models.*;
public class ProductService {
    private final List<Product> products;

    public ProductService() {
        this.products = Arrays.asList(
                // ELECTRONICS

                new Product(307L, "LAMZIEN LZ3", 100.0, Category.ELECTRONICS),
                new Product(318L, "Artel Smart TV A55LU", 3500.0, Category.ELECTRONICS),
                new Product(319L, "Shivaki Konditsioner 12HF", 2800.0, Category.ELECTRONICS),
                new Product(320L, "Xiaomi Redmi Note 13", 2500.0, Category.ELECTRONICS),
                new Product(321L, "Samsung Galaxy A54", 3300.0, Category.ELECTRONICS),
                new Product(322L, "Anker QuardBase Powerbank", 450.0, Category.ELECTRONICS),
                new Product(323L, "Realme Buds Wireless 2 Neo", 290.0, Category.ELECTRONICS),
                new Product(324L, "HP Victus 15 Gaming Laptop", 8900.0, Category.ELECTRONICS),

                // BOOKS
                new Product(308L, "Oʻtkan kunlar", 15.0, Category.BOOKS),
                new Product(309L, "Yangi avlod", 20.0, Category.BOOKS),
                new Product(310L, "Kitoblar olami", 25.0, Category.BOOKS),
                new Product(311L, "Kitoblar dunyosi", 30.0, Category.BOOKS),
                new Product(312L, "Temur tuzuklari", 35.0, Category.BOOKS),
                new Product(313L, "Alpomish", 36.0, Category.BOOKS),
                new Product(314L, "Qutadgʻu bilig", 37.0, Category.BOOKS),
                new Product(315L, "Yulduzli tunlar", 38.0, Category.BOOKS),
                new Product(316L, "Boburnoma", 39.0, Category.BOOKS),
                new Product(317L, "Hamsa", 40.0, Category.BOOKS),
                new Product(325L, "Ulugʻbek Xazinasi", 22.0, Category.BOOKS),
                new Product(326L, "Mustaqillik Davri Adabiyoti", 28.0, Category.BOOKS),

                // CLOTHING
                new Product(327L, "Atlas Koʻylak (ayollar uchun)", 120.0, Category.CLOTHING),
                new Product(328L, "Doʻppi (erkaklar uchun)", 35.0, Category.CLOTHING),
                new Product(329L, "Chust Poyabzali", 140.0, Category.CLOTHING),
                new Product(330L, "Yozgi milliy erkaklar kostyumi", 180.0, Category.CLOTHING),
                new Product(331L, "Qoʻl bilan tikilgan duppi", 55.0, Category.CLOTHING),
                new Product(332L, "Paxta toʻqilgan erkaklar futbolkasi", 90.0, Category.CLOTHING),
                new Product(333L, "Ayollar milliy libosi – Qalqon", 170.0, Category.CLOTHING),
                new Product(334L, "Bolalar uchun milliy kiyim toʻplami", 130.0, Category.CLOTHING)

        );
    }

    public List<Product> getAllProducts() {
        return products;
    }

    public List<Product> getProductsByCategory(Category category) {
        return products.stream()
                .filter(p -> p.getCategory() == category)
                .toList();
    }

    public Product getProductById(Long id) {
        return products.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Product> searchProducts(String query) {
        return products.stream()
                .filter(p -> p.getName().toLowerCase().contains(query.toLowerCase()))
                .toList();
    }
}
