package com.amazon.backend;

import com.amazon.backend.model.Product;
import com.amazon.backend.model.Role;
import com.amazon.backend.model.User;
import com.amazon.backend.repository.ProductRepository;
import com.amazon.backend.repository.UserRepository;
import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DataSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(ProductRepository productRepository,
                      UserRepository userRepository,
                      PasswordEncoder passwordEncoder) {
        this.productRepository = productRepository;
        this.userRepository    = userRepository;
        this.passwordEncoder   = passwordEncoder;
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
        // ── Seed default admin user ────────────────────────────────────────────
        if (!userRepository.existsByEmail("admin@primedeals.com")) {
            User admin = User.builder()
                    .name("Admin")
                    .email("admin@primedeals.com")
                    .password(passwordEncoder.encode("Admin@123"))
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);
            System.out.println("✅ Admin user created → admin@primedeals.com / Admin@123");
        }

        // ── Seed 50 premium Mobiles + 50 premium Laptops ──────────────────────
        seedPremiumProducts();

        if (productRepository.count() >= 2100) return;

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

    // ── 50 curated Mobiles + 50 curated Laptops ────────────────────────────────
    private void seedPremiumProducts() {
        Random rnd = new Random(42);
        int imgSeed = 5001;

        String[] mDesc = {
            "Featuring a stunning display and pro-grade camera system, this flagship smartphone delivers exceptional performance for work and play with lightning-fast 5G connectivity.",
            "The latest innovation in mobile technology with advanced AI features, ultra-fast charging, and an immersive AMOLED display for an unmatched daily experience.",
            "Capture life in stunning detail with the advanced triple-camera setup. Powered by the latest chipset for blazing-fast, lag-free performance all day long.",
            "Slim design meets powerhouse performance. Enjoy all-day battery life with rapid charging and seamless 5G connectivity for the modern lifestyle.",
            "Vibrant high-refresh-rate display and flagship-grade internals in a premium build. This smartphone redefines what is possible at its price point."
        };

        String[] lDesc = {
            "Ultra-thin and lightweight powerhouse with the latest processor, stunning display, and all-day battery life — engineered for professionals who demand the best on the go.",
            "Built for creators and gamers with a high-refresh-rate display, dedicated GPU, advanced thermal management, and a robust premium build that lasts.",
            "Business-grade laptop with enterprise security features, MIL-SPEC durability rating, all-day battery life, and an ergonomic keyboard built for long sessions.",
            "Immersive OLED display with factory-calibrated colours, ideal for content creators, photographers, and media professionals who need perfect accuracy.",
            "High-performance gaming laptop featuring the latest RTX GPU, ultra-fast 165Hz display, and advanced cooling technology for competitive and creative workloads."
        };

        Object[][] mobiles = {
            {"Apple iPhone 15 Pro Max 256GB",        "Apple",    134999.0},
            {"Apple iPhone 15 Pro 128GB",            "Apple",    119900.0},
            {"Apple iPhone 15 256GB",                "Apple",     79900.0},
            {"Apple iPhone 14 128GB",                "Apple",     69900.0},
            {"Apple iPhone 13 128GB",                "Apple",     59900.0},
            {"Samsung Galaxy S24 Ultra 256GB",       "Samsung",  129999.0},
            {"Samsung Galaxy S24+ 256GB",            "Samsung",   99999.0},
            {"Samsung Galaxy S24 128GB",             "Samsung",   74999.0},
            {"Samsung Galaxy Z Fold 6 512GB",        "Samsung",  164999.0},
            {"Samsung Galaxy Z Flip 6 256GB",        "Samsung",  109999.0},
            {"Samsung Galaxy A55 5G 128GB",          "Samsung",   39999.0},
            {"Samsung Galaxy A35 5G 128GB",          "Samsung",   29999.0},
            {"Samsung Galaxy M55 5G 128GB",          "Samsung",   27999.0},
            {"Samsung Galaxy F55 5G 256GB",          "Samsung",   26999.0},
            {"Samsung Galaxy A15 5G 128GB",          "Samsung",   16999.0},
            {"OnePlus 12 5G 256GB",                  "OnePlus",   64999.0},
            {"OnePlus 12R 5G 256GB",                 "OnePlus",   39999.0},
            {"OnePlus Nord CE4 5G 128GB",            "OnePlus",   24999.0},
            {"OnePlus Nord 4 5G 256GB",              "OnePlus",   29999.0},
            {"OnePlus Nord CE3 Lite 5G 128GB",       "OnePlus",   17999.0},
            {"Xiaomi 14 5G 512GB",                   "Xiaomi",    69999.0},
            {"Xiaomi 13T Pro 256GB",                 "Xiaomi",    49999.0},
            {"Xiaomi Redmi Note 13 Pro+ 5G 256GB",   "Xiaomi",    31999.0},
            {"Xiaomi Redmi Note 13 Pro 5G 256GB",    "Xiaomi",    25999.0},
            {"Xiaomi Poco X6 Pro 5G 256GB",          "Xiaomi",    26999.0},
            {"Xiaomi Poco M6 Pro 5G 128GB",          "Xiaomi",    15999.0},
            {"Xiaomi Redmi Note 13 5G 128GB",        "Xiaomi",    14999.0},
            {"Xiaomi Redmi 13C 5G 128GB",            "Xiaomi",    10999.0},
            {"Realme GT 6T 5G 256GB",                "Realme",    35999.0},
            {"Realme Narzo 70 Pro 5G 256GB",         "Realme",    19999.0},
            {"Realme 13 Pro+ 5G 256GB",              "Realme",    32999.0},
            {"Realme C67 5G 128GB",                  "Realme",    11999.0},
            {"Vivo X100 Pro 5G 256GB",               "Vivo",      89999.0},
            {"Vivo V30 Pro 5G 256GB",                "Vivo",      49999.0},
            {"Vivo V30e 5G 128GB",                   "Vivo",      29999.0},
            {"Oppo Find X7 Ultra 5G 512GB",          "Oppo",      99999.0},
            {"Oppo Reno 12 Pro 5G 256GB",            "Oppo",      39999.0},
            {"Oppo A3 Pro 5G 128GB",                 "Oppo",      22999.0},
            {"iQOO 12 5G 256GB",                     "iQOO",      52999.0},
            {"iQOO Neo 9 Pro 5G 256GB",              "iQOO",      37999.0},
            {"iQOO Z9 Pro 5G 256GB",                 "iQOO",      27999.0},
            {"Nothing Phone 2a 5G 256GB",            "Nothing",   25999.0},
            {"Nothing Phone 2 5G 256GB",             "Nothing",   44999.0},
            {"Motorola Edge 50 Ultra 5G 512GB",      "Motorola",  59999.0},
            {"Motorola Edge 50 Pro 5G 256GB",        "Motorola",  31999.0},
            {"Motorola Moto G85 5G 256GB",           "Motorola",  19999.0},
            {"Motorola G54 5G 256GB",                "Motorola",  12999.0},
            {"Google Pixel 8 Pro 256GB",             "Google",    89999.0},
            {"Google Pixel 8a 128GB",                "Google",    52999.0},
            {"Samsung Galaxy A25 5G 128GB",          "Samsung",   19999.0},
        };

        Object[][] laptops = {
            {"Apple MacBook Pro 16-inch M3 Max 36GB",              "Apple",       349900.0},
            {"Apple MacBook Pro 14-inch M3 Pro 18GB",              "Apple",       209900.0},
            {"Apple MacBook Air 15-inch M3 16GB",                  "Apple",       134900.0},
            {"Apple MacBook Air 13-inch M3 8GB",                   "Apple",       114900.0},
            {"Apple MacBook Pro 13-inch M2 8GB",                   "Apple",       129900.0},
            {"Dell XPS 15 9530 RTX 4060",                          "Dell",        184990.0},
            {"Dell XPS 13 9340 Intel Core Ultra 7",                "Dell",        124990.0},
            {"Dell Alienware m18 RTX 4090",                        "Dell",        394990.0},
            {"Dell Inspiron 15 Intel Core i5 16GB",                "Dell",         62990.0},
            {"Dell G15 Gaming RTX 4060",                           "Dell",         96990.0},
            {"HP Spectre x360 14 OLED Intel Core Ultra 7",         "HP",          174999.0},
            {"HP Envy x360 15 OLED AMD Ryzen 7",                   "HP",          109999.0},
            {"HP Pavilion 15 Intel Core i5 16GB",                  "HP",           56999.0},
            {"HP OMEN 16 RTX 4070 Gaming",                         "HP",          149999.0},
            {"HP Victus 16 RTX 4060 Intel Core i7",                "HP",           89999.0},
            {"Lenovo ThinkPad X1 Carbon Gen 12",                   "Lenovo",      169990.0},
            {"Lenovo ThinkPad E16 AMD Ryzen 7",                    "Lenovo",       79990.0},
            {"Lenovo IdeaPad Slim 5 AMD Ryzen 7",                  "Lenovo",       69990.0},
            {"Lenovo Legion 5 Pro RTX 4070 16GB",                  "Lenovo",      139990.0},
            {"Lenovo Legion 7i Gen 9 RTX 4080",                    "Lenovo",      249990.0},
            {"Lenovo Yoga Pro 9i Intel Core Ultra 9",              "Lenovo",      189990.0},
            {"Asus ROG Zephyrus G16 RTX 4090",                     "Asus",        349990.0},
            {"Asus ROG Strix G18 RTX 4080",                        "Asus",        249990.0},
            {"Asus TUF Gaming F17 RTX 4060",                       "Asus",         89990.0},
            {"Asus Vivobook S 15 OLED AMD Ryzen 9",                "Asus",         89990.0},
            {"Asus Zenbook 14 OLED Intel Core Ultra 9",            "Asus",        124990.0},
            {"Asus ProArt Studiobook 16 RTX 4070",                 "Asus",        174990.0},
            {"Asus ROG Flow X16 RTX 4090 2-in-1",                  "Asus",        299990.0},
            {"Acer Predator Helios Neo 16 RTX 4070",               "Acer",        129990.0},
            {"Acer Swift X 14 AMD Ryzen 7 OLED",                   "Acer",         89990.0},
            {"Acer Aspire 5 Intel Core i7 16GB",                   "Acer",         59990.0},
            {"Acer Nitro 17 RTX 4070 Intel Core i7",               "Acer",        114990.0},
            {"MSI Titan GT77 RTX 4090",                            "MSI",         399990.0},
            {"MSI Raider GE68HX RTX 4080",                         "MSI",         299990.0},
            {"MSI Stealth 15M RTX 4060",                           "MSI",         124990.0},
            {"Razer Blade 16 RTX 4090 240Hz",                      "Razer",       374990.0},
            {"Razer Blade 14 RTX 4070 144Hz",                      "Razer",       249990.0},
            {"Microsoft Surface Laptop 5 Intel Core i7",           "Microsoft",   119990.0},
            {"Microsoft Surface Pro 10 Intel Core Ultra 7",        "Microsoft",   139990.0},
            {"Gigabyte AORUS Master 16 RTX 4090",                  "Gigabyte",    329990.0},
            {"Samsung Galaxy Book4 Pro 360 Intel Core Ultra 7",    "Samsung",     149990.0},
            {"Samsung Galaxy Book4 Ultra RTX 4070",                "Samsung",     249990.0},
            {"LG Gram 17 Intel Core Ultra 7 Lightweight",          "LG",          149990.0},
            {"LG Gram 16 2-in-1 Intel Core Ultra 7",               "LG",          134990.0},
            {"HP EliteBook 840 G11 Intel Core Ultra 7",            "HP",          149990.0},
            {"Dell Latitude 5550 Intel Core Ultra 7",              "Dell",        129990.0},
            {"Lenovo IdeaPad Gaming 3 RTX 3050",                   "Lenovo",       65990.0},
            {"HP Dragonfly G4 Intel Core i7 Business",             "HP",          169990.0},
            {"Acer Chromebook 516 GE Gaming",                      "Acer",         54990.0},
            {"Razer Book 13 Intel Core i7 2K Touch",               "Razer",       149990.0},
        };

        if (!productRepository.existsByName((String) mobiles[0][0])) {
            List<Product> batch = new ArrayList<>(mobiles.length);
            for (int i = 0; i < mobiles.length; i++) {
                String name   = (String) mobiles[i][0];
                String brand  = (String) mobiles[i][1];
                double price  = ((Number) mobiles[i][2]).doubleValue();
                double rating = Math.round((3.9 + rnd.nextDouble() * 1.0) * 10.0) / 10.0;
                int reviews   = rnd.nextInt(25000) + 1000;
                int stock     = rnd.nextInt(100) + 10;
                String img    = "https://picsum.photos/seed/" + (imgSeed++) + "/400/400";
                batch.add(Product.builder()
                        .name(name).description(mDesc[i % mDesc.length]).price(price).stock(stock)
                        .category("Mobiles").brand(brand).imageUrl(img).rating(rating).numReviews(reviews)
                        .build());
            }
            productRepository.saveAll(batch);
            System.out.println("✅ Seeded 50 premium Mobile products");
        } else {
            imgSeed += mobiles.length;
        }

        if (!productRepository.existsByName((String) laptops[0][0])) {
            List<Product> batch = new ArrayList<>(laptops.length);
            for (int i = 0; i < laptops.length; i++) {
                String name   = (String) laptops[i][0];
                String brand  = (String) laptops[i][1];
                double price  = ((Number) laptops[i][2]).doubleValue();
                double rating = Math.round((3.9 + rnd.nextDouble() * 1.0) * 10.0) / 10.0;
                int reviews   = rnd.nextInt(20000) + 500;
                int stock     = rnd.nextInt(80) + 5;
                String img    = "https://picsum.photos/seed/" + (imgSeed++) + "/400/400";
                batch.add(Product.builder()
                        .name(name).description(lDesc[i % lDesc.length]).price(price).stock(stock)
                        .category("Laptops").brand(brand).imageUrl(img).rating(rating).numReviews(reviews)
                        .build());
            }
            productRepository.saveAll(batch);
            System.out.println("✅ Seeded 50 premium Laptop products");
        }
    }
}
