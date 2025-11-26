package com.beconnect.beeconnect_backend.Service;

import com.beconnect.beeconnect_backend.DTO.CreateProductDTO;
import com.beconnect.beeconnect_backend.DTO.ProductDTO;
import com.beconnect.beeconnect_backend.DTO.UpdateProductDTO;
import com.beconnect.beeconnect_backend.Enum.ProductCategory;
import com.beconnect.beeconnect_backend.Model.Person;
import com.beconnect.beeconnect_backend.Model.Product;
import com.beconnect.beeconnect_backend.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PersonService personService;

    @Autowired
    private BadgeService badgeService;

    /**
     * Pobierz wszystkie dostępne produkty
     */
    public List<ProductDTO> getAllProducts() {
        List<Product> products = productRepository.findByAvailableTrue();
        return products.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Pobierz produkt według ID
     */
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return mapToDTO(product);
    }

    /**
     * Pobierz produkty według kategorii
     */
    public List<ProductDTO> getProductsByCategory(ProductCategory category) {
        List<Product> products = productRepository.findByCategoryAndAvailableTrue(category);
        return products.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Wyszukaj produkty po nazwie
     */
    public List<ProductDTO> searchProducts(String searchTerm) {
        List<Product> products = productRepository.searchByName(searchTerm);
        return products.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Pobierz produkty w przedziale cenowym
     */
    public List<ProductDTO> getProductsByPriceRange(Double minPrice, Double maxPrice) {
        List<Product> products = productRepository.findByPriceRange(minPrice, maxPrice);
        return products.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Pobierz produkty zalogowanego użytkownika (sprzedawcy)
     */
    public List<ProductDTO> getMyProducts() {
        Person seller = personService.getProfile();
        List<Product> products = productRepository.findBySeller(seller);
        return products.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Dodaj nowy produkt
     */
    @Transactional
    public ProductDTO addProduct(CreateProductDTO dto) {
        Person seller = personService.getProfile();

        // Walidacja
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new RuntimeException("Product name is required");
        }
        if (dto.getPrice() == null || dto.getPrice() <= 0) {
            throw new RuntimeException("Price must be greater than 0");
        }
        if (dto.getStock() == null || dto.getStock() < 0) {
            throw new RuntimeException("Stock cannot be negative");
        }

        Product product = Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .category(dto.getCategory())
                .imageBase64(dto.getImageBase64())
                .stock(dto.getStock())
                .available(true)
                .rating(0.0)
                .reviewCount(0)
                .seller(seller)
                .location(dto.getLocation())
                .weight(dto.getWeight())
                .weightUnit(dto.getWeightUnit())
                .build();

        product = productRepository.save(product);
        try {
            badgeService.checkAndAwardBadges(personService.getProfile().getId());
        } catch (Exception e) {
            // Loguj błąd, ale nie przerywaj operacji dodawania produktu
            System.err.println("Error checking badges: " + e.getMessage());
        }

        return mapToDTO(product);
    }

    /**
     * Aktualizuj produkt
     */
    @Transactional
    public ProductDTO updateProduct(UpdateProductDTO dto) {
        Person currentUser = personService.getProfile();

        Product product = productRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Sprawdź, czy użytkownik jest właścicielem produktu
        if (!product.getSeller().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You don't have permission to edit this product");
        }

        // Aktualizuj pola
        if (dto.getName() != null) product.setName(dto.getName());
        if (dto.getDescription() != null) product.setDescription(dto.getDescription());
        if (dto.getPrice() != null) product.setPrice(dto.getPrice());
        if (dto.getCategory() != null) product.setCategory(dto.getCategory());
        if (dto.getImageBase64() != null) product.setImageBase64(dto.getImageBase64());
        if (dto.getStock() != null) product.setStock(dto.getStock());
        if (dto.getAvailable() != null) product.setAvailable(dto.getAvailable());
        if (dto.getLocation() != null) product.setLocation(dto.getLocation());
        if (dto.getWeight() != null) product.setWeight(dto.getWeight());
        if (dto.getWeightUnit() != null) product.setWeightUnit(dto.getWeightUnit());

        product = productRepository.save(product);
        return mapToDTO(product);
    }

    /**
     * Usuń produkt
     */
    @Transactional
    public void deleteProduct(Long id) {
        Person currentUser = personService.getProfile();

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Sprawdź, czy użytkownik jest właścicielem produktu
        if (!product.getSeller().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You don't have permission to delete this product");
        }

        productRepository.delete(product);
    }

    /**
     * Zmień dostępność produktu
     */
    @Transactional
    public ProductDTO toggleAvailability(Long id) {
        Person currentUser = personService.getProfile();

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Sprawdź, czy użytkownik jest właścicielem produktu
        if (!product.getSeller().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You don't have permission to modify this product");
        }

        product.setAvailable(!product.getAvailable());
        product = productRepository.save(product);
        return mapToDTO(product);
    }

    /**
     * Pobierz najnowsze produkty
     */
    public List<ProductDTO> getRecentProducts() {
        List<Product> products = productRepository.findRecentProducts();
        return products.stream()
                .limit(20)
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Pobierz najpopularniejsze produkty
     */
    public List<ProductDTO> getPopularProducts() {
        List<Product> products = productRepository.findPopularProducts();
        return products.stream()
                .limit(20)
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Mapowanie Product → ProductDTO
     */
    private ProductDTO mapToDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .category(product.getCategory())
                .imageBase64(product.getImageBase64())
                .stock(product.getStock())
                .available(product.getAvailable())
                .rating(product.getRating())
                .reviewCount(product.getReviewCount())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .sellerId(product.getSeller().getId())
                .sellerFirstname(product.getSeller().getFirstname())
                .sellerLastname(product.getSeller().getLastname())
                .sellerEmail(product.getSeller().getEmail())
                .location(product.getLocation())
                .weight(product.getWeight())
                .weightUnit(product.getWeightUnit())
                .build();
    }
}