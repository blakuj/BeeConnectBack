package com.beconnect.beeconnect_backend.Service;

import com.beconnect.beeconnect_backend.DTO.CreateProductDTO;
import com.beconnect.beeconnect_backend.DTO.ProductDTO;
import com.beconnect.beeconnect_backend.DTO.UpdateProductDTO;
import com.beconnect.beeconnect_backend.Enum.ProductCategory;
import com.beconnect.beeconnect_backend.Model.Image;
import com.beconnect.beeconnect_backend.Model.Person;
import com.beconnect.beeconnect_backend.Model.Product;
import com.beconnect.beeconnect_backend.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

        // Obsługa zdjęć - konwersja List<String> na List<Image>
        List<Image> images = new ArrayList<>();
        if (dto.getImages() != null) {
            images = dto.getImages().stream()
                    .map(base64 -> Image.builder().fileContent(base64).build())
                    .collect(Collectors.toList());
        }

        Product product = Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .category(dto.getCategory())
                .images(images) // Przypisanie listy zdjęć
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

        // Sprawdzenie odznak po dodaniu produktu
        try {
            badgeService.checkAndAwardBadges(personService.getProfile().getId());
        } catch (Exception e) {
            System.err.println("Error checking badges: " + e.getMessage());
        }

        return mapToDTO(product);
    }

    @Transactional
    public ProductDTO updateProduct(UpdateProductDTO dto) {
        Person currentUser = personService.getProfile();

        Product product = productRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Sprawdzenie uprawnień
        if (!product.getSeller().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You don't have permission to edit this product");
        }

        // Aktualizacja pól
        if (dto.getName() != null) product.setName(dto.getName());
        if (dto.getDescription() != null) product.setDescription(dto.getDescription());
        if (dto.getPrice() != null) product.setPrice(dto.getPrice());
        if (dto.getCategory() != null) product.setCategory(dto.getCategory());

        // Aktualizacja zdjęć
        if (dto.getImages() != null) {
            product.getImages().clear();
            List<Image> newImages = dto.getImages().stream()
                    .map(base64 -> Image.builder().fileContent(base64).build())
                    .collect(Collectors.toList());
            product.getImages().addAll(newImages);
        }

        if (dto.getStock() != null) product.setStock(dto.getStock());
        if (dto.getAvailable() != null) product.setAvailable(dto.getAvailable());
        if (dto.getLocation() != null) product.setLocation(dto.getLocation());
        if (dto.getWeight() != null) product.setWeight(dto.getWeight());
        if (dto.getWeightUnit() != null) product.setWeightUnit(dto.getWeightUnit());

        product = productRepository.save(product);
        return mapToDTO(product);
    }

    public List<ProductDTO> getAllProducts() {
        List<Product> products = productRepository.findByAvailableTrue();
        return products.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return mapToDTO(product);
    }

    public List<ProductDTO> getProductsByCategory(ProductCategory category) {
        List<Product> products = productRepository.findByCategoryAndAvailableTrue(category);
        return products.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<ProductDTO> searchProducts(String searchTerm) {
        List<Product> products = productRepository.searchByName(searchTerm);
        return products.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<ProductDTO> getProductsByPriceRange(Double min, Double max) {
        List<Product> products = productRepository.findByPriceRange(min, max);
        return products.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<ProductDTO> getMyProducts() {
        Person seller = personService.getProfile();
        List<Product> products = productRepository.findBySeller(seller);
        return products.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<ProductDTO> getRecentProducts() {
        List<Product> products = productRepository.findRecentProducts();
        return products.stream().limit(20).map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<ProductDTO> getPopularProducts() {
        List<Product> products = productRepository.findPopularProducts();
        return products.stream().limit(20).map(this::mapToDTO).collect(Collectors.toList());
    }

    @Transactional
    public void deleteProduct(Long id) {
        Person currentUser = personService.getProfile();
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.getSeller().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You don't have permission to delete this product");
        }

        productRepository.delete(product);
    }

    @Transactional
    public ProductDTO toggleAvailability(Long id) {
        Person currentUser = personService.getProfile();
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.getSeller().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You don't have permission to modify this product");
        }

        product.setAvailable(!product.getAvailable());
        product = productRepository.save(product);
        return mapToDTO(product);
    }

    private ProductDTO mapToDTO(Product product) {
        // Mapowanie zdjęć
        List<String> images = product.getImages().stream()
                .map(Image::getFileContent)
                .collect(Collectors.toList());

        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .category(product.getCategory())
                .images(images) // Lista zdjęć
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