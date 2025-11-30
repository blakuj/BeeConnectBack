package com.beconnect.beeconnect_backend.Service;

import com.beconnect.beeconnect_backend.Model.Flower;
import com.beconnect.beeconnect_backend.Repository.FlowerRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
public class FlowerService {

    @Autowired
    private FlowerRepository flowerRepository;

//    /**
//     * Metoda uruchamiana automatycznie przy starcie aplikacji.
//     * Inicjalizuje bazę danych predefiniowanymi kwiatami.
//     */
//    @PostConstruct
//    @Transactional
//    public void initFlowers() {
//        // Lista zgodna z tym, co masz na frontendzie
//        List<FlowerData> defaults = Arrays.asList(
//                new FlowerData("Lipa", "#FF0000"),
//                new FlowerData("Wielokwiat", "#FFA500"),
//                new FlowerData("Gryka", "#FFFF00"),
//                new FlowerData("Rzepak", "#FFFF99"),
//                new FlowerData("Spadź", "#800000"),
//                new FlowerData("Akacja", "#7878FF"),
//                new FlowerData("Wrzos", "#800080"),
//                new FlowerData("Malina", "#FF69B4")
//        );
//
//        for (FlowerData data : defaults) {
//            createFlowerIfNotExists(data.name, data.color);
//        }
//    }

    private void createFlowerIfNotExists(String name, String color) {
        if (flowerRepository.findByName(name).isEmpty()) {
            Flower flower = Flower.builder()
                    .name(name)
                    .color(color)
                    .build();
            flowerRepository.save(flower);
            System.out.println("Zainicjalizowano kwiat: " + name);
        }
    }

    private static class FlowerData {
        String name;
        String color;

        public FlowerData(String name, String color) {
            this.name = name;
            this.color = color;
        }
    }
}