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

    @Transactional
    public OrderDTO createOrder(CreateOrderDTO dto) {
        Person buyer = personService.getProfile();

        if (dto.getQuantity() == null || dto.getQuantity() <= 0) {
            throw new RuntimeException("Quantity must be greater than 0");
        }

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.getAvailable()) {
            throw new RuntimeException("Product is not available");
        }

        if (product.getStock() < dto.getQuantity()) {
            throw new RuntimeException("Insufficient stock. Available: " + product.getStock());
        }

        if (product.getSeller().getId().equals(buyer.getId())) {
            throw new RuntimeException("You cannot buy your own product");
        }

        double totalPrice = product.getPrice() * dto.getQuantity();
        System.out.println(totalPrice);
        System.out.println(dto.getQuantity());
        if (buyer.getBalance() < totalPrice) {
            throw new RuntimeException("Insufficient balance. Required: " + totalPrice + " PLN, Available: " + buyer.getBalance() + " PLN");
        }

        buyer.setBalance((float) (buyer.getBalance() - totalPrice));
        personRepository.save(buyer);

        Person seller = product.getSeller();
        seller.setBalance((float) (seller.getBalance() + totalPrice));
        personRepository.save(seller);

        product.setStock(product.getStock() - dto.getQuantity());

        if (product.getStock() == 0) {
            product.setAvailable(false);
        }

        productRepository.save(product);

        Order order = Order.builder()
                .buyer(buyer)
                .seller(seller)
                .product(product)
                .quantity(dto.getQuantity())
                .pricePerUnit(product.getPrice())
                .totalPrice(totalPrice)
                .status(OrderStatus.COMPLETED)
                .deliveryAddress(dto.getDeliveryAddress())
                .buyerNotes(dto.getBuyerNotes())
                .build();

        order = orderRepository.save(order);

        return mapToDTO(order);
    }


    public List<OrderDTO> getMyPurchases() {
        Person buyer = personService.getProfile();
        List<Order> orders = orderRepository.findRecentOrdersByBuyer(buyer);
        return orders.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    public List<OrderDTO> getMySales() {
        Person seller = personService.getProfile();
        List<Order> orders = orderRepository.findRecentOrdersBySeller(seller);
        return orders.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    public OrderDTO getOrderById(Long id) {
        Person currentUser = personService.getProfile();

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        boolean isBuyer = order.getBuyer().getId().equals(currentUser.getId());
        boolean isSeller = order.getSeller().getId().equals(currentUser.getId());

        if (!isBuyer && !isSeller) {
            throw new RuntimeException("You don't have permission to view this order");
        }

        return mapToDTO(order);
    }


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
    

    private OrderDTO mapToDTO(Order order) {
        return OrderDTO.builder()
                .id(order.getId())
                .buyerId(order.getBuyer().getId())
                .buyerFirstname(order.getBuyer().getFirstname())
                .buyerLastname(order.getBuyer().getLastname())
                .buyerEmail(order.getBuyer().getEmail())
                .sellerId(order.getSeller().getId())
                .sellerFirstname(order.getSeller().getFirstname())
                .sellerLastname(order.getSeller().getLastname())
                .sellerEmail(order.getSeller().getEmail())
                .productId(order.getProduct().getId())
                .productName(order.getProduct().getName())
                .productCategory(order.getProduct().getCategory().toString())
                .productImage(order.getProduct().getImageBase64())
                .quantity(order.getQuantity())
                .pricePerUnit(order.getPricePerUnit())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus())
                .orderedAt(order.getOrderedAt())
                .deliveredAt(order.getDeliveredAt())
                .deliveryAddress(order.getDeliveryAddress())
                .buyerNotes(order.getBuyerNotes())
                .build();
    }
}