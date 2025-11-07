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


    public List<ProductDTO> getAllProducts() {
        List<Product> products = productRepository.findByAvailableTrue();
        return products.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return mapToDTO(product);
    }

    public List<ProductDTO> getProductsByCategory(ProductCategory category) {
        List<Product> products = productRepository.findByCategoryAndAvailableTrue(category);
        return products.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    public List<ProductDTO> searchProducts(String searchTerm) {
        List<Product> products = productRepository.searchByName(searchTerm);
        return products.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


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