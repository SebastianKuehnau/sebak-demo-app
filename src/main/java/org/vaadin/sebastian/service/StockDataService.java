package org.vaadin.sebastian.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class StockDataService {

    private final AtomicLong elementId = new AtomicLong(0);

    private final Set<StockData> elements = Stream.of("Hydrogen",
            "Helium", "Lithium", "Beryllium", "Boron", "Carbon", "Nitrogen", "Oxygen", "Fluorine",
            "Neon", "Sodium", "Magnesium", "Aluminum", "Silicon", "Phosphorus", "Sulfur", "Chlorine",
            "Argon", "Potassium", "Calcium", "Scandium", "Titanium", "Vanadium", "Chromium", "Manganese",
            "Iron", "Cobalt", "Nickel", "Copper", "Zinc", "Gallium", "Germanium", "Arsenic",
            "Selenium", "Bromine", "Krypton", "Rubidium", "Strontium", "Yttrium", "Zirconium", "Niobium",
            "Molybdenum", "Technetium", "Ruthenium", "Rhodium", "Palladium", "Silver", "Cadmium", "Indium",
            "Tin", "Antimony", "Tellurium", "Iodine", "Xenon", "Cesium", "Barium", "Lanthanum",
            "Cerium", "Praseodymium", "Neodymium", "Promethium", "Samarium", "Europium", "Gadolinium", "Terbium",
            "Dysprosium", "Holmium", "Erbium", "Thulium", "Ytterbium", "Lutetium", "Hafnium", "Tantalum",
            "Tungsten", "Rhenium", "Osmium", "Iridium", "Platinum", "Gold", "Mercury", "Thallium",
            "Lead", "Bismuth", "Polonium", "Astatine", "Radon", "Francium", "Radium", "Actinium",
            "Thorium", "Protactinium", "Uranium", "Neptunium", "Plutonium", "Americium", "Curium", "Berkelium",
            "Californium", "Einsteinium", "Fermium", "Mendelevium", "Nobelium", "Lawrencium", "Rutherfordium", "Dubnium",
            "Seaborgium", "Bohrium", "Hassium", "Meitnerium", "Darmstadtium", "Roentgenium", "Copernicium", "Nihonium",
            "Flerovium", "Moscovium", "Livermorium", "Tennessine", "Oganesson")
            .map(this::createStockDataInstance)
            .collect(Collectors.toSet());

    public Set<StockData> getStockData() {
        return elements;
    }

    private StockData createStockDataInstance(String s) {
        return createRandomPrice(new StockData(elementId.incrementAndGet(), s));
    }

    private StockData createRandomPrice(StockData stockData) {
        stockData.setPrice(BigDecimal.valueOf(ThreadLocalRandom.current().nextInt(10000), 2));
        return stockData;
    }

    public Set<StockData> updateStockPrice() {
        return elements.stream()
                .unordered()
                .filter(stockData -> ThreadLocalRandom.current().nextInt(4) % 4 == 0)
                .map(this::createRandomPrice)
                .collect(Collectors.toSet());
    }

    public static class StockData {
        private Long id ;
        private String name ;
        private BigDecimal price = BigDecimal.ZERO;

        public StockData(Long id, String name, BigDecimal price) {
            this.id = id ;
            this.name = name;
            this.price = price;
        }

        public StockData(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }
    }
}
