package com.beconnect.beeconnect_backend.Controller;

import com.beconnect.beeconnect_backend.DTO.CreateOrderDTO;
import com.beconnect.beeconnect_backend.DTO.OrderDTO;
import com.beconnect.beeconnect_backend.Service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * POST /api/orders
     * Utwórz zamówienie (kup produkt)
     */
    @PostMapping
    public ResponseEntity<?> createOrder(@Valid @RequestBody CreateOrderDTO dto) {
        try {
            OrderDTO order = orderService.createOrder(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(order);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/orders/my-purchases
     * Pobierz historię zakupów zalogowanego użytkownika
     */
    @GetMapping("/my-purchases")
    public ResponseEntity<List<OrderDTO>> getMyPurchases() {
        try {
            List<OrderDTO> orders = orderService.getMyPurchases();
            return ResponseEntity.ok(orders);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    /**
     * GET /api/orders/my-sales
     * Pobierz historię sprzedaży zalogowanego użytkownika
     */
    @GetMapping("/my-sales")
    public ResponseEntity<List<OrderDTO>> getMySales() {
        try {
            List<OrderDTO> orders = orderService.getMySales();
            return ResponseEntity.ok(orders);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    /**
     * GET /api/orders/my
     * Pobierz wszystkie zamówienia użytkownika (kupione + sprzedane)
     */
    @GetMapping("/my")
    public ResponseEntity<List<OrderDTO>> getAllMyOrders() {
        try {
            List<OrderDTO> orders = orderService.getAllMyOrders();
            return ResponseEntity.ok(orders);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    /**
     * GET /api/orders/{id}
     * Pobierz szczegóły zamówienia
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        try {
            OrderDTO order = orderService.getOrderById(id);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}