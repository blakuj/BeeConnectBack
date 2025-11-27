package com.beconnect.beeconnect_backend.Service;

import com.beconnect.beeconnect_backend.DTO.BadgeDTO;
import com.beconnect.beeconnect_backend.Model.*;
import com.beconnect.beeconnect_backend.Repository.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BadgeService {

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private ProductReviewRepository productReviewRepository;

    /**
     * Automatyczna inicjalizacja typów odznak przy starcie aplikacji
     */
    @PostConstruct
    public void init() {
        initializeDefaultBadges();
    }

    /**
     * Pobierz wszystkie odznaki użytkownika
     */
    @Transactional(readOnly = true)
    public List<BadgeDTO> getUserBadges(Long personId) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new RuntimeException("Person not found"));

        return person.getBadges().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * GŁÓWNA METODA: Sprawdź i przyznaj odznaki użytkownikowi
     */
    @Transactional
    public void checkAndAwardBadges(Long personId) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new RuntimeException("Person not found"));

        // 1. Sprawdź badge "Nowicjusz"
        checkNewbie(person);

        // 2. Sprawdź badge "Potentat"
        checkTycoon(person);

        // 3. Sprawdź badge "Zaufany Sprzedawca"
        checkTrustedSeller(person);

        personRepository.save(person);
    }

    // --- LOGIKA BIZNESOWA ODZNAK ---

    /**
     * 1. NEWBIE (Nowicjusz)
     * Warunek: Posiadaj dodany przynajmniej 1 obszar ALBO 1 produkt.
     */
    private void checkNewbie(Person person) {
        String badgeCode = "NEWBIE";
        if (hasBadge(person, badgeCode)) return;

        boolean hasAreas = !person.getOwnedAreas().isEmpty();
        boolean hasProducts = !person.getSellingProducts().isEmpty();

        if (hasAreas || hasProducts) {
            awardBadge(person, badgeCode);
        }
    }

    /**
     * 2. TYCOON (Potentat)
     * Warunek: Posiadaj przynajmniej 5 obszarów (ownedAreas).
     */
    private void checkTycoon(Person person) {
        String badgeCode = "TYCOON";
        if (hasBadge(person, badgeCode)) return;

        if (person.getOwnedAreas().size() >= 5) {
            awardBadge(person, badgeCode);
        }
    }

    /**
     * 3. TRUSTED_SELLER (Zaufany Sprzedawca)
     * Warunek: Średnia ocen powyżej 4.5 przy minimum 5 ocenach wszystkich produktów łącznie.
     */
    private void checkTrustedSeller(Person person) {
        String badgeCode = "TRUSTED_SELLER";
        if (hasBadge(person, badgeCode)) return;

        List<Product> products = person.getSellingProducts();
        if (products.isEmpty()) return;

        // Pobieramy wszystkie opinie dla wszystkich produktów tego użytkownika
        long totalReviewsCount = 0;
        double totalRatingSum = 0.0;

        for (Product product : products) {
            List<ProductReview> reviews = productReviewRepository.findByProductOrderByCreatedAtDesc(product);
            totalReviewsCount += reviews.size();
            totalRatingSum += reviews.stream().mapToInt(ProductReview::getRating).sum();
        }

        if (totalReviewsCount >= 5) {
            double globalAverage = totalRatingSum / totalReviewsCount;
            if (globalAverage > 4.5) {
                awardBadge(person, badgeCode);
            }
        }
    }

    // --- METODY POMOCNICZE ---

    private boolean hasBadge(Person person, String badgeCode) {
        return person.getBadges().stream().anyMatch(b -> b.getCode().equals(badgeCode));
    }

    private void awardBadge(Person person, String badgeCode) {
        badgeRepository.findByCode(badgeCode).ifPresent(badge -> {
            person.getBadges().add(badge);
        });
    }

    private BadgeDTO mapToDTO(Badge badge) {
        return BadgeDTO.builder()
                .id(badge.getId())
                .code(badge.getCode())
                .name(badge.getName())
                .description(badge.getDescription())
                .icon(badge.getIcon())
                .color(badge.getColor())
                .build();
    }

    @Transactional
    public void initializeDefaultBadges() {
        createBadgeIfNotExists("NEWBIE", "Nowicjusz",
                "Posiada przynajmniej jeden obszar lub produkt.",
                "fas fa-seedling", "#51CF66"); // Zielony listek

        createBadgeIfNotExists("TYCOON", "Potentat",
                "Posiada 5 lub więcej obszarów na własność.",
                "fas fa-crown", "#FFD700"); // Złota korona

        createBadgeIfNotExists("TRUSTED_SELLER", "Zaufany Sprzedawca",
                "Średnia ocen produktów > 4.5 (przy min. 5 ocenach).",
                "fas fa-check-circle", "#339AF0"); // Niebieski znaczek
    }

    private void createBadgeIfNotExists(String code, String name, String description,
                                        String icon, String color) {
        if (!badgeRepository.existsByCode(code)) {
            Badge badge = Badge.builder()
                    .code(code)
                    .name(name)
                    .description(description)
                    .icon(icon)
                    .color(color)
                    .build();
            badgeRepository.save(badge);
        }
    }
}