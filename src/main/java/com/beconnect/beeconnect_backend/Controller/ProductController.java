package com.beconnect.beeconnect_backend.Controller;

import com.beconnect.beeconnect_backend.DTO.CreateProductDTO;
import com.beconnect.beeconnect_backend.DTO.ProductDTO;
import com.beconnect.beeconnect_backend.DTO.UpdateProductDTO;
import com.beconnect.beeconnect_backend.Enum.ProductCategory;
import com.beconnect.beeconnect_backend.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * GET /api/products
     * Pobierz wszystkie dostępne produkty
     */
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        try {
            List<ProductDTO> products = productService.getAllProducts();
            return ResponseEntity.ok(products);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * GET /api/products/{id}
     * Pobierz produkt według ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        try {
            ProductDTO product = productService.getProductById(id);
            return ResponseEntity.ok(product);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/products/category/{category}
     * Pobierz produkty według kategorii
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<?> getProductsByCategory(@PathVariable String category) {
        try {
            ProductCategory categoryEnum = ProductCategory.valueOf(category.toUpperCase());
            List<ProductDTO> products = productService.getProductsByCategory(categoryEnum);
            return ResponseEntity.ok(products);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid category"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * GET /api/products/search?q={searchTerm}
     * Wyszukaj produkty po nazwie
     */
    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> searchProducts(@RequestParam String q) {
        try {
            List<ProductDTO> products = productService.searchProducts(q);
            return ResponseEntity.ok(products);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * GET /api/products/price-range?min={minPrice}&max={maxPrice}
     * Pobierz produkty w przedziale cenowym
     */
    @GetMapping("/price-range")
    public ResponseEntity<List<ProductDTO>> getProductsByPriceRange(
            @RequestParam Double min,
            @RequestParam Double max) {
        try {
            List<ProductDTO> products = productService.getProductsByPriceRange(min, max);
            return ResponseEntity.ok(products);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * GET /api/products/my
     * Pobierz produkty zalogowanego użytkownika
     */
    @GetMapping("/my")
    public ResponseEntity<List<ProductDTO>> getMyProducts() {
        try {
            List<ProductDTO> products = productService.getMyProducts();
            return ResponseEntity.ok(products);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    /**
     * GET /api/products/recent
     * Pobierz najnowsze produkty
     */
    @GetMapping("/recent")
    public ResponseEntity<List<ProductDTO>> getRecentProducts() {
        try {
            List<ProductDTO> products = productService.getRecentProducts();
            return ResponseEntity.ok(products);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * GET /api/products/popular
     * Pobierz najpopularniejsze produkty
     */
    @GetMapping("/popular")
    public ResponseEntity<List<ProductDTO>> getPopularProducts() {
        try {
            List<ProductDTO> products = productService.getPopularProducts();
            return ResponseEntity.ok(products);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * POST /api/products
     * Dodaj nowy produkt
     */
    @PostMapping
    public ResponseEntity<?> addProduct(@RequestBody CreateProductDTO dto) {
        try {
            ProductDTO product = productService.addProduct(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(product);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PUT /api/products
     * Aktualizuj produkt
     */
    @PutMapping
    public ResponseEntity<?> updateProduct(@RequestBody UpdateProductDTO dto) {
        try {
            ProductDTO product = productService.updateProduct(dto);
            return ResponseEntity.ok(product);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DELETE /api/products/{id}
     * Usuń produkt
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok(Map.of("message", "Product deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PUT /api/products/{id}/toggle-availability
     * Zmień dostępność produktu
     */
    @PutMapping("/{id}/toggle-availability")
    public ResponseEntity<?> toggleAvailability(@PathVariable Long id) {
        try {
            ProductDTO product = productService.toggleAvailability(id);
            return ResponseEntity.ok(product);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}