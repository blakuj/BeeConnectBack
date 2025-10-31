package com.beconnect.beeconnect_backend.Service;

import com.beconnect.beeconnect_backend.DTO.ChangePasswordDTO;
import com.beconnect.beeconnect_backend.DTO.PersonDTO;
import com.beconnect.beeconnect_backend.DTO.UpdateProfileDTO;
import com.beconnect.beeconnect_backend.Model.Person;
import com.beconnect.beeconnect_backend.Repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PersonRepository personRepository;

    @Transactional
    public void updateProfile(UpdateProfileDTO dto) {
        Person person = getProfile();

        if (dto.getPhone().length() > 11 || dto.getEmail().length() < 9) {
            throw new IllegalArgumentException("Phone length must be between 9 and 11 characters");
        }

        if (!dto.getEmail().equals(person.getEmail())) {
            if (personRepository.existsByEmail(dto.getEmail())) {
                throw new IllegalArgumentException("Email already exists");
            }
        }

        person.setPhone(dto.getPhone());
        person.setEmail(dto.getEmail());
        personRepository.save(person);

    }

    public Person getProfile() {
        String idStr = SecurityContextHolder.getContext().getAuthentication().getName();
        Long id = Long.parseLong(idStr);
        return personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public void changePassword(ChangePasswordDTO changePasswordDTO) {
        Person person = getProfile();

        if (!passwordEncoder.matches(changePasswordDTO.getOldPassword(), person.getPassword())) {
            throw new IllegalArgumentException("Old password is wrong");
        }

        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmPassword())){
            throw new IllegalArgumentException("Passwords do not match");
        }


        person.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        personRepository.save(person);

    }

    public void addFunds(Long amount){
        Person person = getProfile();
        person.setBalance(person.getBalance() + amount);
        personRepository.save(person);
    }

    public List<PersonDTO> getAllUsers() {
        return personRepository.findAll()
                .stream()
                .map(p -> new PersonDTO(
                        p.getId(),
                        p.getFirstname(),
                        p.getLastname(),
                        p.getEmail(),
                        p.getPhone(),
                        p.getRole(),
                        p.getBalance()
                ))
                .collect(Collectors.toList());
    }

}