package com.beconnect.beeconnect_backend.Service;

import com.beconnect.beeconnect_backend.DTO.BadgeDTO;
import com.beconnect.beeconnect_backend.Model.*;
import com.beconnect.beeconnect_backend.Repository.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BadgeService {

    @Autowired
    private PersonRepository personRepository;

    @Transactional(readOnly = true)
    public List<BadgeDTO> getUserBadges(Long personId) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new RuntimeException("Person not found"));

        return person.getBadges().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private BadgeDTO mapToDTO(Badge badge) {
        return BadgeDTO.builder()
                .id(badge.getId())
                .code(badge.getCode())
                .name(badge.getName())
                .description(badge.getDescription())
                .icon(badge.getIcon())
                .color(badge.getColor())
                .build();
    }
}