package com.beconnect.beeconnect_backend.Service;

import com.beconnect.beeconnect_backend.DTO.UpdateProfileDTO;
import com.beconnect.beeconnect_backend.Model.Person;
import com.beconnect.beeconnect_backend.Repository.PersonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PersonService {

    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Transactional
    public Person updateProfile(String currentEmail, UpdateProfileDTO dto) {
        System.out.println("=== SERVICE: Rozpoczynam aktualizację profilu ===");
        System.out.println("Email użytkownika: " + currentEmail);

        Person person = personRepository.findByEmail(currentEmail)
                .orElseThrow(() -> {
                    System.out.println("❌ Użytkownik nie znaleziony!");
                    return new RuntimeException("User not found");
                });

        System.out.println("✅ Znaleziono użytkownika: " + person.getFirstname() + " " + person.getLastname());

        if (dto.getPhone() != null && !dto.getPhone().trim().isEmpty()) {
            System.out.println("Aktualizacja telefonu...");
            String phone = dto.getPhone().trim();

            if (phone.length() < 9 || phone.length() > 15) {
                System.out.println("❌ Nieprawidłowa długość numeru telefonu");
                throw new IllegalArgumentException("Numer telefonu musi mieć 9-15 znaków");
            }

            if (!phone.matches("^\\+?[0-9\\s-]+$")) {
                System.out.println("❌ Telefon zawiera niedozwolone znaki");
                throw new IllegalArgumentException("Numer telefonu może zawierać tylko cyfry, spacje, + i -");
            }

            String oldPhone = person.getPhone();
            person.setPhone(phone);
            System.out.println("✅ Telefon zaktualizowany: " + oldPhone + " → " + phone);
        }

        if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty()) {
            System.out.println("Aktualizacja emaila...");
            String newEmail = dto.getEmail().trim().toLowerCase();

            if (!newEmail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                System.out.println("❌ Nieprawidłowy format emaila");
                throw new IllegalArgumentException("Nieprawidłowy format adresu email");
            }

            if (!newEmail.equals(currentEmail) && personRepository.existsByEmail(newEmail)) {
                System.out.println("❌ Email już zajęty");
                throw new IllegalArgumentException("Ten adres email jest już zajęty");
            }

            String oldEmail = person.getEmail();
            person.setEmail(newEmail);
            person.setLogin(newEmail);
            System.out.println("✅ Email zaktualizowany: " + oldEmail + " → " + newEmail);
        }

        System.out.println("Zapisuję zmiany w bazie danych...");
        Person updatedPerson = personRepository.save(person);

        System.out.println("✅ Profil zaktualizowany pomyślnie!");
        System.out.println("=== SERVICE: Koniec ===");

        return updatedPerson;
    }

    public Person getProfile(String email) {
        System.out.println("=== SERVICE: Pobieram profil dla: " + email + " ===");

        return personRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}