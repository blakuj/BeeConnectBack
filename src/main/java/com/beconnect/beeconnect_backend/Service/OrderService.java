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
}