package com.amazon.backend;

import com.amazon.backend.model.Product;
import com.amazon.backend.repository.ProductRepository;
import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DataSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;

    public DataSeeder(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        if (productRepository.count() > 0) return; // skip if already seeded

        Faker faker = new Faker(new Locale("en-US"));
        List<Product> products = new ArrayList<>();

        // Category configs: name, brands, image keywords
        List<Object[]> categories = List.of(
            new Object[]{"Electronics", List.of("Samsung", "Sony", "LG", "OnePlus", "Apple", "Xiaomi"), "electronics,gadget,tech"},
            new Object[]{"Mobiles", List.of("Samsung", "Apple", "Xiaomi", "Realme", "OnePlus", "Vivo"), "smartphone,mobile,phone"},
            new Object[]{"Laptops", List.of("Dell", "HP", "Lenovo", "Asus", "Acer", "Apple"), "laptop,computer,notebook"},
            new Object[]{"Clothing", List.of("Nike", "Adidas", "Puma", "H&M", "Zara", "Levi's"), "fashion,clothing,apparel"},
            new Object[]{"Home & Kitchen", List.of("Prestige", "Bajaj", "Philips", "Bosch", "IFB"), "kitchen,home,appliance"},
            new Object[]{"Books", List.of("Penguin", "Harper Collins", "Scholastic", "Wiley", "Oxford"), "book,library,reading"},
            new Object[]{"Sports", List.of("Nike", "Adidas", "Decathlon", "Yonex", "Cosco"), "sports,fitness,gym"},
            new Object[]{"Beauty", List.of("L'Oreal", "Nivea", "Lakme", "Maybelline", "Dove"), "beauty,cosmetics,skincare"},
            new Object[]{"Furniture", List.of("IKEA", "Pepperfry", "Urban Ladder", "Nilkamal"), "furniture,interior,home"},
            new Object[]{"Toys", List.of("Lego", "Hasbro", "Mattel", "Fisher-Price", "Hot Wheels"), "toy,kids,play"}
        );

        Random rnd = new Random();
        int idCounter = 1;

        for (Object[] cat : categories) {
            String catName = (String) cat[0];
            @SuppressWarnings("unchecked")
            List<String> brands = (List<String>) cat[1];
            String imgKeyword = ((String) cat[2]).split(",")[rnd.nextInt(((String) cat[2]).split(",").length)];

            for (int i = 0; i < 10; i++) {
                String brand = brands.get(rnd.nextInt(brands.size()));
                String adjective = faker.commerce().productName().split(" ")[0];
                String productName = brand + " " + adjective + " " + catName.split(" ")[0];

                double price = Math.round((rnd.nextDouble() * 49000 + 999) * 100.0) / 100.0;
                int stock = rnd.nextInt(200) + 5;
                double rating = Math.round((3.0 + rnd.nextDouble() * 2.0) * 10.0) / 10.0;
                int numReviews = rnd.nextInt(5000) + 50;

                String imageUrl = "https://source.unsplash.com/400x400/?" + imgKeyword + "&sig=" + idCounter;

                Product p = Product.builder()
                        .name(productName)
                        .description(faker.lorem().paragraph(2))
                        .price(price)
                        .stock(stock)
                        .category(catName)
                        .brand(brand)
                        .imageUrl(imageUrl)
                        .rating(rating)
                        .numReviews(numReviews)
                        .build();

                products.add(p);
                idCounter++;
            }
        }

        productRepository.saveAll(products);
        System.out.println("✅ Seeded " + products.size() + " fake products.");
    }
}
