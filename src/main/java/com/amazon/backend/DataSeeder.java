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

    // ── category config ────────────────────────────────────────────────────────
    record CatConfig(String name, List<String> brands, List<String> types,
                     List<String> adjectives, double minPrice, double maxPrice,
                     String imgKeyword) {}

    private static final List<CatConfig> CATS = List.of(
        new CatConfig("Mobiles",
            List.of("Samsung","Apple","OnePlus","Xiaomi","Realme","Vivo","Oppo","iQOO","Nothing","Motorola"),
            List.of("Smartphone","Phone","5G Mobile","Pro Max","Ultra","Lite","Edge","S Series","Note","Fold"),
            List.of("Flagship","Budget","Premium","Compact","Gaming","Slim","Fast-Charging","Dual-SIM","Rugged","AI"),
            6999, 149999, "smartphone"),

        new CatConfig("Laptops",
            List.of("Dell","HP","Lenovo","Asus","Acer","Apple","MSI","Razer","Microsoft","Gigabyte"),
            List.of("Laptop","Notebook","Gaming Laptop","Ultrabook","Chromebook","2-in-1","Workstation","MacBook","ProBook","IdeaPad"),
            List.of("Thin & Light","Gaming","Business","Student","Creator","AI-Powered","OLED","4K","RTX","High-Performance"),
            25999, 299999, "laptop"),

        new CatConfig("Electronics",
            List.of("Sony","Samsung","LG","Philips","Panasonic","Boat","JBL","Bose","Sennheiser","Anker"),
            List.of("Smart TV","Headphones","Earbuds","Speaker","Soundbar","Tablet","Monitor","Camera","Projector","Router"),
            List.of("Wireless","Noise-Cancelling","4K","Smart","Portable","Hi-Fi","Bluetooth","Dolby Atmos","AMOLED","Voice-Assisted"),
            999, 249999, "electronics"),

        new CatConfig("Clothing",
            List.of("Nike","Adidas","Puma","Levi's","H&M","Zara","US Polo","Arrow","Allen Solly","Peter England"),
            List.of("T-Shirt","Jeans","Shirt","Jacket","Hoodie","Kurta","Blazer","Trackpants","Shorts","Sweater"),
            List.of("Slim Fit","Regular","Oversized","Casual","Formal","Trendy","Striped","Solid","Printed","Athletic"),
            299, 9999, "fashion,clothing"),

        new CatConfig("Home & Kitchen",
            List.of("Prestige","Bajaj","Philips","Bosch","IFB","Morphy Richards","Havells","Pigeon","TTK","Butterfly"),
            List.of("Mixer Grinder","Microwave","Air Fryer","Induction Cooktop","Pressure Cooker","Blender","Toaster","Coffee Maker","Water Purifier","Juicer"),
            List.of("Smart","Stainless Steel","Non-Stick","Energy Saving","Multi-Function","Digital","Auto-Clean","Compact","Heavy Duty","Eco"),
            499, 49999, "kitchen,appliance"),

        new CatConfig("Sports & Fitness",
            List.of("Nike","Adidas","Decathlon","Yonex","Cosco","Vector X","Nivia","Reebok","Under Armour","Columbia"),
            List.of("Cricket Bat","Football","Badminton Racket","Yoga Mat","Dumbbell","Treadmill","Cycle","Gym Gloves","Skipping Rope","Knee Support"),
            List.of("Professional","Training","Lightweight","Durable","Beginner","Heavy Duty","Ergonomic","Competition","Indoor","Outdoor"),
            199, 79999, "sports,fitness"),

        new CatConfig("Beauty & Personal Care",
            List.of("L'Oreal","Nivea","Lakme","Maybelline","Dove","Biotique","Mamaearth","WOW","Plum","Himalaya"),
            List.of("Face Wash","Moisturizer","Serum","Sunscreen","Lipstick","Foundation","Shampoo","Conditioner","Body Lotion","Perfume"),
            List.of("Organic","Herbal","Anti-Aging","Brightening","Hydrating","Vitamin C","SPF 50","Sulphate-Free","Natural","Dermatologist-Tested"),
            99, 4999, "beauty,skincare,cosmetics"),

        new CatConfig("Books",
            List.of("Penguin","Harper Collins","Scholastic","Wiley","Oxford","Pearson","Bloomsbury","Hachette","Simon & Schuster","Random House"),
            List.of("Novel","Self-Help Book","Thriller","Biography","Science Fiction","Cook Book","Business Book","Children's Book","Comic","Textbook"),
            List.of("Bestselling","Award-Winning","International","National","Classic","Illustrated","Revised Edition","Limited","Collector's","Pocket"),
            99, 1999, "book,library"),

        new CatConfig("Furniture",
            List.of("IKEA","Pepperfry","Urban Ladder","Nilkamal","Godrej","Wakefit","Durian","Centuary","Sleep Well","HomeTown"),
            List.of("Sofa","Bed","Wardrobe","Dining Table","Study Chair","Bookshelf","Coffee Table","TV Unit","Office Desk","Recliner"),
            List.of("Solid Wood","Modern","Minimalist","Ergonomic","Foldable","Convertible","Space-Saving","Luxury","Industrial","Scandinavian"),
            1999, 149999, "furniture,interior"),

        new CatConfig("Toys & Games",
            List.of("Lego","Hasbro","Mattel","Fisher-Price","Hot Wheels","Nerf","Barbie","Funskool","Milton","Skillmatics"),
            List.of("Building Blocks","Action Figure","Board Game","Remote Car","Doll","Puzzle","Science Kit","Art Set","Card Game","Educational Toy"),
            List.of("Creative","STEM","Educational","Interactive","Battery-Operated","3D","Magnetic","Glow-in-Dark","Giant","Mini"),
            199, 9999, "toys,kids")
    );

    @Override
    public void run(String... args) {
        if (productRepository.count() >= 2000) return;

        Faker faker = new Faker(new Locale("en-IN"));
        Random rnd = new Random();
        List<Product> batch = new ArrayList<>(100);
        int total = 0;
        int seed = 1;

        for (CatConfig cat : CATS) {
            for (int i = 0; i < 200; i++) {
                String brand     = cat.brands().get(rnd.nextInt(cat.brands().size()));
                String type      = cat.types().get(rnd.nextInt(cat.types().size()));
                String adjective = cat.adjectives().get(rnd.nextInt(cat.adjectives().size()));
                String name      = brand + " " + adjective + " " + type;

                double rawPrice  = cat.minPrice() + rnd.nextDouble() * (cat.maxPrice() - cat.minPrice());
                // make prices end in 99 or 999 like real e-commerce
                double price     = Math.floor(rawPrice / 100) * 100 - 1;

                double rating    = Math.round((3.0 + rnd.nextDouble() * 2.0) * 10.0) / 10.0;
                int numReviews   = rnd.nextInt(9500) + 50;
                int stock        = rnd.nextInt(500) + 1;

                // picsum gives fast, stable images by seed — no API key needed
                String imageUrl  = "https://picsum.photos/seed/" + seed + "/400/400";

                batch.add(Product.builder()
                        .name(name)
                        .description(faker.lorem().paragraph(3))
                        .price(price)
                        .stock(stock)
                        .category(cat.name())
                        .brand(brand)
                        .imageUrl(imageUrl)
                        .rating(rating)
                        .numReviews(numReviews)
                        .build());

                seed++;
                total++;

                // flush every 500 rows for performance
                if (batch.size() == 100) {
                    productRepository.saveAll(batch);
                    batch.clear();
                }
            }
        }

        if (!batch.isEmpty()) productRepository.saveAll(batch);

        System.out.println("✅ Seeded " + total + " products across " + CATS.size() + " categories.");
    }
}
