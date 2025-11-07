package com.beconnect.beeconnect_backend.Controller;

import com.beconnect.beeconnect_backend.DTO.CreateOrderDTO;
import com.beconnect.beeconnect_backend.DTO.OrderDTO;
import com.beconnect.beeconnect_backend.Service.OrderService;
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

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody CreateOrderDTO dto) {
        try {
            OrderDTO order = orderService.createOrder(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(order);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }


    @GetMapping("/my-purchases")
    public ResponseEntity<List<OrderDTO>> getMyPurchases() {
        try {
            List<OrderDTO> orders = orderService.getMyPurchases();
            return ResponseEntity.ok(orders);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @GetMapping("/my-sales")
    public ResponseEntity<List<OrderDTO>> getMySales() {
        try {
            List<OrderDTO> orders = orderService.getMySales();
            return ResponseEntity.ok(orders);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @GetMapping("/my")
    public ResponseEntity<List<OrderDTO>> getAllMyOrders() {
        try {
            List<OrderDTO> orders = orderService.getAllMyOrders();
            return ResponseEntity.ok(orders);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

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