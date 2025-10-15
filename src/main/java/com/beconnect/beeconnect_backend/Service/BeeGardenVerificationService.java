package com.beconnect.beeconnect_backend.Service;

import com.beconnect.beeconnect_backend.DTO.VerificationDTO;
import com.beconnect.beeconnect_backend.Enum.Status;
import com.beconnect.beeconnect_backend.Model.BeeGarden;
import com.beconnect.beeconnect_backend.Model.BeeGardenVerification;
import com.beconnect.beeconnect_backend.Model.Document;
import com.beconnect.beeconnect_backend.Model.Person;
import com.beconnect.beeconnect_backend.Repository.BeeGardenRepository;
import com.beconnect.beeconnect_backend.Repository.BeeGardenVerificationRepository;
import com.beconnect.beeconnect_backend.Repository.DocumentRepository;
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
import java.util.UUID;

@Service
public class BeeGardenVerificationService {

    @Autowired
    private PersonService personService;

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

        BeeGarden beeGarden = BeeGarden.builder()
                .person(person)
                .name(verificationDTO.getBeeGardenName())
                .adress(verificationDTO.getAdress())
                .hiveCount(verificationDTO.getCountHives())
                .honeyType(verificationDTO.getHoneyType())
                .build();

        beeGardenRepository.save(beeGarden);

        BeeGardenVerification beeGardenVerification = BeeGardenVerification.builder()
                .status(Status.PENDING)
                .person(person)
                .creationDate(LocalDateTime.now())
                .build();

        verificationRepository.save(beeGardenVerification);

        if (file != null && !file.isEmpty()) {
            String path = saveFile(file);

            Document document = Document.builder()
                    .type(verificationDTO.getDocType())
                    .filePath(path)
                    .verification(beeGardenVerification)
                    .build();

            documentRepository.save(document);
        } else {
            throw new IllegalArgumentException("Plik jest wymagany");
        }
    }

    public String saveFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Plik nie może być pusty");
        }

        String[] allowedExtensions = {".pdf", ".jpg", ".jpeg", ".png"};
        String fileName = file.getOriginalFilename();
        String fileExtension = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
        boolean isValidExtension = false;
        for (String ext : allowedExtensions) {
            if (ext.equals(fileExtension)) {
                isValidExtension = true;
                break;
            }
        }
        if (!isValidExtension) {
            throw new IllegalArgumentException("Nieobsługiwany format pliku. Dozwolone: PDF, JPG, JPEG, PNG");
        }

        String uniqueFileName = UUID.randomUUID() + fileExtension;
        Path uploadPath = Paths.get(uploadDirectory);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(uniqueFileName);
        Files.write(filePath, file.getBytes());

        return filePath.toString();
    }
}