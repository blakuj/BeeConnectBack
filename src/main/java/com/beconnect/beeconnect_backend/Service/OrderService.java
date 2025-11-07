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