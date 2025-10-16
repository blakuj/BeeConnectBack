package com.beconnect.beeconnect_backend.Config;

import com.beconnect.beeconnect_backend.Enum.Role;
import com.beconnect.beeconnect_backend.Model.Person;
import com.beconnect.beeconnect_backend.Repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        createDefaultAdmin();
    }

    private void createDefaultAdmin() {
        // Sprawdź czy admin już istnieje
        String adminEmail = "admin@beeconnect.pl";
        String adminLogin = "admin";

        if (personRepository.findByEmail(adminEmail).isEmpty() &&
                personRepository.findByLogin(adminLogin).isEmpty()) {

            Person admin = Person.builder()
                    .firstname("Admin")
                    .lastname("System")
                    .email(adminEmail)
                    .login(adminLogin)
                    .password(passwordEncoder.encode("Admin123!"))  // Domyślne hasło
                    .phone("000000000")
                    .role(Role.ADMIN)
                    .build();

            personRepository.save(admin);

            System.out.println("================================================");
            System.out.println("   UTWORZONO KONTO ADMINISTRATORA");
            System.out.println("   Login: admin");
            System.out.println("   Hasło: Admin123!");
            System.out.println("   Email: admin@beeconnect.pl");
            System.out.println("   ZMIEŃ HASŁO PO PIERWSZYM LOGOWANIU!");
            System.out.println("================================================");
        } else {
            System.out.println("Konto administratora już istnieje.");
        }
    }
}