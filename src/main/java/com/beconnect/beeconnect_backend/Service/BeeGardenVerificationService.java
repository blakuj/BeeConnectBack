package com.beconnect.beeconnect_backend.Service;

import com.beconnect.beeconnect_backend.DTO.VerificationDTO;
import com.beconnect.beeconnect_backend.DTO.VerificationResponseDTO;
import com.beconnect.beeconnect_backend.Enum.Role;
import com.beconnect.beeconnect_backend.Enum.Status;
import com.beconnect.beeconnect_backend.Model.BeeGarden;
import com.beconnect.beeconnect_backend.Model.BeeGardenVerification;
import com.beconnect.beeconnect_backend.Model.Document;
import com.beconnect.beeconnect_backend.Model.Person;
import com.beconnect.beeconnect_backend.Repository.BeeGardenRepository;
import com.beconnect.beeconnect_backend.Repository.BeeGardenVerificationRepository;
import com.beconnect.beeconnect_backend.Repository.DocumentRepository;
import com.beconnect.beeconnect_backend.Repository.PersonRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BeeGardenVerificationService {

    @Autowired
    private PersonService personService;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private BeeGardenVerificationRepository verificationRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private BeeGardenRepository beeGardenRepository;

    @Value("${upload.directory}")
    private String uploadDirectory;

    @Transactional
    public void submitAnApplication(MultipartFile file, VerificationDTO verificationDTO) throws IOException {
        Person person = personService.getProfile();

        if (verificationDTO == null || verificationDTO.getBeeGardenName() == null || verificationDTO.getDocType() == null) {
            throw new IllegalArgumentException("Nieprawidłowe dane weryfikacyjne");
        }

        // Sprawdzenie czy użytkownik nie ma już wniosku
        if (person.getVerification() != null) {
            throw new IllegalArgumentException("Masz już złożony wniosek o weryfikację");
        }

        BeeGarden beeGarden = BeeGarden.builder()
                .person(person)
                .name(verificationDTO.getBeeGardenName())
                .adress(verificationDTO.getAdress())
                .hiveCount(verificationDTO.getCountHives())
                .honeyType(verificationDTO.getHoneyType())
                .build();

        beeGardenRepository.save(beeGarden);

        BeeGardenVerification verification = BeeGardenVerification.builder()
                .person(person)
                .status(Status.PENDING)
                .creationDate(LocalDateTime.now())
                .build();

        verificationRepository.save(verification);

        // Zapisz plik
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDirectory, fileName);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, file.getBytes());

        Document document = Document.builder()
                .type(verificationDTO.getDocType())
                .filePath(filePath.toString())
                .verification(verification)
                .build();

        documentRepository.save(document);

        person.setVerification(verification);
        personRepository.save(person);
    }

    // METODY DLA ADMINA

    public List<VerificationResponseDTO> getAllVerifications(String statusFilter) {
        List<BeeGardenVerification> verifications;

        if (statusFilter != null && !statusFilter.isEmpty()) {
            Status status = Status.valueOf(statusFilter.toUpperCase());
            verifications = verificationRepository.findByStatus(status);
        } else {
            verifications = verificationRepository.findAll();
        }

        return verifications.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public VerificationResponseDTO getVerificationById(Long id) {
        BeeGardenVerification verification = verificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono wniosku o ID: " + id));
        return mapToDTO(verification);
    }

    @Transactional
    public void approveVerification(Long id, String comment) {
        BeeGardenVerification verification = verificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono wniosku o ID: " + id));

        if (verification.getStatus() != Status.PENDING) {
            throw new RuntimeException("Tylko wnioski oczekujące mogą być zatwierdzone");
        }

        verification.setStatus(Status.APPROVED);
        verification.setComment(comment);
        verificationRepository.save(verification);

        // Zmień rolę użytkownika na BEEKEEPER
        Person person = verification.getPerson();
        person.setRole(Role.BEEKEEPER);
        personRepository.save(person);
    }

    @Transactional
    public void rejectVerification(Long id, String reason) {
        BeeGardenVerification verification = verificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono wniosku o ID: " + id));

        if (verification.getStatus() != Status.PENDING) {
            throw new RuntimeException("Tylko wnioski oczekujące mogą być odrzucone");
        }

        verification.setStatus(Status.REJECTED);
        verification.setComment(reason);
        verificationRepository.save(verification);
    }

    private VerificationResponseDTO mapToDTO(BeeGardenVerification verification) {
        Person person = verification.getPerson();
        BeeGarden beeGarden = beeGardenRepository.findByPerson(person).orElse(null);

        List<VerificationResponseDTO.DocumentDTO> documentDTOs = verification.getDocuments().stream()
                .map(doc -> VerificationResponseDTO.DocumentDTO.builder()
                        .id(doc.getId())
                        .type(doc.getType())
                        .fileName(Paths.get(doc.getFilePath()).getFileName().toString())
                        .filePath(doc.getFilePath())
                        .build())
                .collect(Collectors.toList());

        return VerificationResponseDTO.builder()
                .id(verification.getId())
                .userName(person.getFirstname() + " " + person.getLastname())
                .userEmail(person.getEmail())
                .userPhone(person.getPhone())
                .beeGardenName(beeGarden != null ? beeGarden.getName() : null)
                .address(beeGarden != null ? beeGarden.getAdress() : null)
                .hiveCount(beeGarden != null ? beeGarden.getHiveCount() : null)
                .honeyType(beeGarden != null ? beeGarden.getHoneyType() : null)
                .status(verification.getStatus())
                .creationDate(verification.getCreationDate())
                .comment(verification.getComment())
                .documents(documentDTOs)
                .build();
    }
}