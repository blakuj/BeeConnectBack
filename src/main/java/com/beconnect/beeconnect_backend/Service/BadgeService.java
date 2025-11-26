package com.beconnect.beeconnect_backend.Service;

import com.beconnect.beeconnect_backend.DTO.BadgeDTO;
import com.beconnect.beeconnect_backend.Model.*;
import com.beconnect.beeconnect_backend.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BadgeService {

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private ProductReviewRepository productReviewRepository;

    @Autowired
    private AreaReviewRepository areaReviewRepository;

    /**
     * Pobierz wszystkie odznaki użytkownika
     */
    public List<BadgeDTO> getUserBadges(Long personId) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new RuntimeException("Person not found"));

        return person.getBadges().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Sprawdź i przyznaj wszystkie możliwe odznaki użytkownikowi
     */
    @Transactional
    public void checkAndAwardBadges(Long personId) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new RuntimeException("Person not found"));

        // Sprawdź każdy typ odznaki
        checkFrequentSeller(person);
        checkTrustedSeller(person);
        checkNewStar(person);
        checkActiveHost(person);
        checkPerfectService(person);
        checkVeteran(person);
        checkTopRated(person);

        personRepository.save(person);
    }

    /**
     * Częste nowości - dodanie 5+ produktów w ciągu 30 dni
     */
    private void checkFrequentSeller(Person person) {
        String badgeCode = "FREQUENT_SELLER";
        if (person.getBadges().stream().anyMatch(b -> b.getCode().equals(badgeCode))) {
            return; // Już ma
        }

        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        long recentProducts = person.getSellingProducts().stream()
                .filter(p -> p.getCreatedAt() != null && p.getCreatedAt().isAfter(thirtyDaysAgo))
                .count();

        long recentAreas = person.getOwnedAreas().stream()
                .filter(a -> a.getAvailableFrom() != null &&
                        a.getAvailableFrom().atStartOfDay().isAfter(thirtyDaysAgo))
                .count();

        if (recentProducts + recentAreas >= 5) {
            awardBadge(person, badgeCode);
        }
    }

    /**
     * Zaufany sprzedawca - średnia ocen produktów powyżej 4.5
     */
    private void checkTrustedSeller(Person person) {
        String badgeCode = "TRUSTED_SELLER";
        if (person.getBadges().stream().anyMatch(b -> b.getCode().equals(badgeCode))) {
            return;
        }

        List<Product> products = person.getSellingProducts();
        if (products.isEmpty()) return;

        double avgRating = products.stream()
                .filter(p -> p.getReviewCount() > 0)
                .mapToDouble(Product::getRating)
                .average()
                .orElse(0.0);

        long totalReviews = products.stream()
                .mapToLong(Product::getReviewCount)
                .sum();

        if (avgRating >= 4.5 && totalReviews >= 5) {
            awardBadge(person, badgeCode);
        }
    }

    /**
     * Nowa gwiazda - pierwsze 3 produkty/obszary dodane
     */
    private void checkNewStar(Person person) {
        String badgeCode = "NEW_STAR";
        if (person.getBadges().stream().anyMatch(b -> b.getCode().equals(badgeCode))) {
            return;
        }

        long totalItems = person.getSellingProducts().size() + person.getOwnedAreas().size();
        if (totalItems >= 3) {
            awardBadge(person, badgeCode);
        }
    }

    /**
     * Aktywny wynajmujący - 5+ obszarów dodanych
     */
    private void checkActiveHost(Person person) {
        String badgeCode = "ACTIVE_HOST";
        if (person.getBadges().stream().anyMatch(b -> b.getCode().equals(badgeCode))) {
            return;
        }

        if (person.getOwnedAreas().size() >= 5) {
            awardBadge(person, badgeCode);
        }
    }

    /**
     * Doskonała obsługa - 10+ opinii z oceną 5 gwiazdek
     */
    private void checkPerfectService(Person person) {
        String badgeCode = "PERFECT_SERVICE";
        if (person.getBadges().stream().anyMatch(b -> b.getCode().equals(badgeCode))) {
            return;
        }

        long perfectProductReviews = person.getSellingProducts().stream()
                .flatMap(p -> productReviewRepository.findByProductOrderByCreatedAtDesc(p).stream())
                .filter(r -> r.getRating() == 5)
                .count();

        long perfectAreaReviews = person.getOwnedAreas().stream()
                .flatMap(a -> areaReviewRepository.findByAreaOrderByCreatedAtDesc(a).stream())
                .filter(r -> r.getRating() == 5)
                .count();

        if (perfectProductReviews + perfectAreaReviews >= 10) {
            awardBadge(person, badgeCode);
        }
    }

    /**
     * Weteran - konto starsze niż 6 miesięcy
     */
    private void checkVeteran(Person person) {
        String badgeCode = "VETERAN";
        if (person.getBadges().stream().anyMatch(b -> b.getCode().equals(badgeCode))) {
            return;
        }

        if (person.getCreatedAt() != null) {
            LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
            if (person.getCreatedAt().isBefore(sixMonthsAgo)) {
                awardBadge(person, badgeCode);
            }
        }
    }

    /**
     * Najwyżej oceniany - średnia ocen obszarów powyżej 4.8
     */
    private void checkTopRated(Person person) {
        String badgeCode = "TOP_RATED";
        if (person.getBadges().stream().anyMatch(b -> b.getCode().equals(badgeCode))) {
            return;
        }

        List<Area> areas = person.getOwnedAreas();
        if (areas.isEmpty()) return;

        double avgRating = areas.stream()
                .filter(a -> a.getReviewCount() > 0)
                .mapToDouble(Area::getAverageRating)
                .average()
                .orElse(0.0);

        long totalReviews = areas.stream()
                .mapToLong(Area::getReviewCount)
                .sum();

        if (avgRating >= 4.8 && totalReviews >= 5) {
            awardBadge(person, badgeCode);
        }
    }

    /**
     * Przyznaj odznakę użytkownikowi
     */
    private void awardBadge(Person person, String badgeCode) {
        Badge badge = badgeRepository.findByCode(badgeCode)
                .orElse(null);

        if (badge != null) {
            person.getBadges().add(badge);
        }
    }

    /**
     * Mapowanie Badge → BadgeDTO
     */
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

    /**
     * Inicjalizacja domyślnych odznak w bazie (wywołać raz przy starcie)
     */
    @Transactional
    public void initializeDefaultBadges() {
        createBadgeIfNotExists("FREQUENT_SELLER", "Częste nowości",
                "Dodano 5+ produktów/obszarów w ciągu 30 dni",
                "fas fa-fire", "#FF6B6B");

        createBadgeIfNotExists("TRUSTED_SELLER", "Zaufany sprzedawca",
                "Średnia ocen produktów powyżej 4.5 (min. 5 opinii)",
                "fas fa-shield-alt", "#51CF66");

        createBadgeIfNotExists("NEW_STAR", "Nowa gwiazda",
                "Dodano pierwsze 3 produkty/obszary",
                "fas fa-star", "#FFD43B");

        createBadgeIfNotExists("ACTIVE_HOST", "Aktywny wynajmujący",
                "Dodano 5+ obszarów do wynajęcia",
                "fas fa-home", "#339AF0");

        createBadgeIfNotExists("PERFECT_SERVICE", "Doskonała obsługa",
                "Otrzymano 10+ opinii z oceną 5 gwiazdek",
                "fas fa-trophy", "#FFD700");

        createBadgeIfNotExists("VETERAN", "Weteran",
                "Konto aktywne od ponad 6 miesięcy",
                "fas fa-crown", "#9775FA");

        createBadgeIfNotExists("TOP_RATED", "Najwyżej oceniany",
                "Średnia ocen obszarów powyżej 4.8 (min. 5 opinii)",
                "fas fa-gem", "#20C997");
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
