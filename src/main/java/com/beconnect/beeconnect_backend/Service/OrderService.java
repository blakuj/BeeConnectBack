package com.beconnect.beeconnect_backend.Service;

import com.beconnect.beeconnect_backend.DTO.CreateOrderDTO;
import com.beconnect.beeconnect_backend.DTO.OrderDTO;
import com.beconnect.beeconnect_backend.Enum.OrderStatus;
import com.beconnect.beeconnect_backend.Model.Order;
import com.beconnect.beeconnect_backend.Model.Person;
import com.beconnect.beeconnect_backend.Model.Product;
import com.beconnect.beeconnect_backend.Repository.OrderRepository;
import com.beconnect.beeconnect_backend.Repository.PersonRepository;
import com.beconnect.beeconnect_backend.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PersonService personService;

    @Autowired
    private NotificationService notificationService;

    /**
     * Utwórz zamówienie (kup produkt)
     */
    @Transactional
    public OrderDTO createOrder(CreateOrderDTO dto) {
        Person buyer = personService.getProfile();

        // Walidacja ilości
        if (dto.getQuantity() == null || dto.getQuantity() <= 0) {
            throw new RuntimeException("Quantity must be greater than 0");
        }

        // Pobierz produkt
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Sprawdź dostępność
        if (!product.getAvailable()) {
            throw new RuntimeException("Product is not available");
        }

        // Sprawdź stock
        if (product.getStock() < dto.getQuantity()) {
            throw new RuntimeException("Insufficient stock. Available: " + product.getStock());
        }

        // Sprawdź czy użytkownik nie kupuje własnego produktu
        if (product.getSeller().getId().equals(buyer.getId())) {
            throw new RuntimeException("You cannot buy your own product");
        }

        // Obliczenia finansowe na BigDecimal
        BigDecimal quantity = BigDecimal.valueOf(dto.getQuantity());
        BigDecimal pricePerUnit = product.getPrice();
        BigDecimal totalPrice = pricePerUnit.multiply(quantity);

        // Sprawdź saldo kupującego
        if (buyer.getBalance().compareTo(totalPrice) < 0) {
            throw new RuntimeException("Insufficient balance. Required: " + totalPrice + " PLN, Available: " + buyer.getBalance() + " PLN");
        }

        // POBIERZ ŚRODKI OD KUPUJĄCEGO
        buyer.setBalance(buyer.getBalance().subtract(totalPrice));
        personRepository.save(buyer);

        // DODAJ ŚRODKI SPRZEDAWCY
        Person seller = product.getSeller();
        seller.setBalance(seller.getBalance().add(totalPrice));
        personRepository.save(seller);

        // Aktualizacja stanu magazynowego
        product.setStock(product.getStock() - dto.getQuantity());

        if (product.getStock() == 0) {
            product.setAvailable(false);
        }

        productRepository.save(product);

        // Zapisz zamówienie
        Order order = Order.builder()
                .buyer(buyer)
                .product(product)
                .quantity(dto.getQuantity())
                .pricePerUnit(pricePerUnit)
                .totalPrice(totalPrice)
                .status(OrderStatus.COMPLETED)
                .deliveryAddress(dto.getDeliveryAddress())
                .buyerNotes(dto.getBuyerNotes())
                .build();

        order = orderRepository.save(order);

        // Wyślij powiadomienie
        Person currentUser = personService.getProfile();
        notificationService.notifyNewOrder(
                product.getSeller().getId(),
                product.getName(),
                currentUser.getFirstname() + " " + currentUser.getLastname(),
                order.getId()
        );

        return mapToDTO(order);
    }

    /**
     * Pobierz historię zakupów zalogowanego użytkownika
     */
    public List<OrderDTO> getMyPurchases() {
        Person buyer = personService.getProfile();
        List<Order> orders = orderRepository.findRecentOrdersByBuyer(buyer);
        return orders.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Pobierz historię sprzedaży zalogowanego użytkownika
     */
    public List<OrderDTO> getMySales() {
        Person seller = personService.getProfile();

        List<Order> orders = orderRepository.findRecentOrdersBySeller(seller);
        return orders.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Pobierz szczegóły zamówienia
     */
    public OrderDTO getOrderById(Long id) {
        Person currentUser = personService.getProfile();

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        boolean isBuyer = order.getBuyer().getId().equals(currentUser.getId());
        boolean isSeller = order.getProduct().getSeller().getId().equals(currentUser.getId());

        if (!isBuyer && !isSeller) {
            throw new RuntimeException("You don't have permission to view this order");
        }

        return mapToDTO(order);
    }

    /**
     * Pobierz wszystkie zamówienia użytkownika (kupione + sprzedane)
     */
    public List<OrderDTO> getAllMyOrders() {
        Person user = personService.getProfile();

        List<Order> purchases = orderRepository.findByBuyer(user);
        List<Order> sales = orderRepository.findBySeller(user);

        List<Order> allOrders = new java.util.ArrayList<>(purchases);
        allOrders.addAll(sales);

        allOrders.sort((o1, o2) -> o2.getOrderedAt().compareTo(o1.getOrderedAt()));

        return allOrders.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Mapowanie Order → OrderDTO
     */
    private OrderDTO mapToDTO(Order order) {
        String productImage = null;
        if (order.getProduct().getImages() != null && !order.getProduct().getImages().isEmpty()) {
            productImage = order.getProduct().getImages().get(0).getFileContent();
        }

        Person seller = order.getProduct().getSeller();

        return OrderDTO.builder()
                .id(order.getId())
                .buyerId(order.getBuyer().getId())
                .buyerFirstname(order.getBuyer().getFirstname())
                .buyerLastname(order.getBuyer().getLastname())
                .buyerEmail(order.getBuyer().getEmail())
                .sellerId(seller.getId())
                .sellerFirstname(seller.getFirstname())
                .sellerLastname(seller.getLastname())
                .sellerEmail(seller.getEmail())
                .productId(order.getProduct().getId())
                .productName(order.getProduct().getName())
                .productCategory(order.getProduct().getCategory().toString())
                .productImage(productImage)
                .quantity(order.getQuantity())
                .pricePerUnit(order.getPricePerUnit().doubleValue())
                .totalPrice(order.getTotalPrice().doubleValue())
                .status(order.getStatus())
                .orderedAt(order.getOrderedAt())
                .deliveredAt(order.getDeliveredAt())
                .deliveryAddress(order.getDeliveryAddress())
                .buyerNotes(order.getBuyerNotes())
                .build();
    }
}