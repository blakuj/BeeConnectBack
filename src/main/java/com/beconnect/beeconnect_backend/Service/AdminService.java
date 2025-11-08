package com.beconnect.beeconnect_backend.Service;

import com.beconnect.beeconnect_backend.DTO.DashboardStatsDTO;
import com.beconnect.beeconnect_backend.DTO.DocumentDTO;
import com.beconnect.beeconnect_backend.DTO.VerificationDecisionDTO;
import com.beconnect.beeconnect_backend.DTO.VerificationResponseDTO;
import com.beconnect.beeconnect_backend.Enum.AvailabilityStatus;
import com.beconnect.beeconnect_backend.Enum.Role;
import com.beconnect.beeconnect_backend.Enum.Status;
import com.beconnect.beeconnect_backend.Model.BeeGarden;
import com.beconnect.beeconnect_backend.Model.BeeGardenVerification;
import com.beconnect.beeconnect_backend.Model.Person;
import com.beconnect.beeconnect_backend.Repository.AreaRepository;
import com.beconnect.beeconnect_backend.Repository.BeeGardenRepository;
import com.beconnect.beeconnect_backend.Repository.BeeGardenVerificationRepository;
import com.beconnect.beeconnect_backend.Repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private BeeGardenVerificationRepository verificationRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private BeeGardenRepository beeGardenRepository;

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private PersonService personService;

    @Autowired
    private NotificationService notificationService;

    /**
     * Pobierz statystyki dla dashboardu
     */
    public DashboardStatsDTO getDashboardStats() {
        return DashboardStatsDTO.builder()
                .totalUsers(personRepository.count())
                .verifiedBeekeepers(personRepository.countByRole(Role.BEEKEEPER))
                .pendingVerifications(verificationRepository.countByStatus(Status.PENDING))
                .rejectedVerifications(verificationRepository.countByStatus(Status.REJECTED))
                .totalAreas(areaRepository.count())
                .availableAreas(areaRepository.countByAvailabilityStatus(AvailabilityStatus.AVAILABLE))
                .reservedAreas(areaRepository.countByAvailabilityStatus(AvailabilityStatus.UNAVAILABLE))
                .totalProducts(0L) // TODO: gdy dodamy produkty
                .adminsCount(personRepository.countByRole(Role.ADMIN))
                .build();
    }

    /**
     * Pobierz wszystkie wnioski weryfikacyjne
     */
    public List<VerificationResponseDTO> getAllVerifications() {
        List<BeeGardenVerification> verifications = verificationRepository.findAll();
        return verifications.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Pobierz wnioski według statusu
     */
    public List<VerificationResponseDTO> getVerificationsByStatus(Status status) {
        List<BeeGardenVerification> verifications = verificationRepository.findByStatus(status);
        return verifications.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Pobierz szczegóły konkretnego wniosku
     */
    public VerificationResponseDTO getVerificationById(Long id) {
        BeeGardenVerification verification = verificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Verification not found"));
        return mapToDTO(verification);
    }

    /**
     * Rozpatrz wniosek (zatwierdź lub odrzuć)
     */
    @Transactional
    public void processVerification(VerificationDecisionDTO decision) {
        // Pobierz obecnego admina
        Person admin = personService.getProfile();
        if (admin.getRole() != Role.ADMIN) {
            throw new RuntimeException("Only admins can process verifications");
        }

        // Pobierz wniosek
        BeeGardenVerification verification = verificationRepository.findById(decision.getVerificationId())
                .orElseThrow(() -> new RuntimeException("Verification not found"));

        // Sprawdź czy wniosek nie jest już rozpatrzony
        if (verification.getStatus() != Status.PENDING) {
            throw new RuntimeException("This verification has already been processed");
        }

        // Ustaw status
        verification.setStatus(decision.getApproved() ? Status.APPROVED : Status.REJECTED);
        verification.setComment(decision.getComment());
        verification.setReviewedDate(LocalDateTime.now());
        verification.setReviewedBy(admin.getEmail());

        Person person = verification.getPerson();
        if (decision.getApproved()) {

            person.setRole(Role.BEEKEEPER);
            personRepository.save(person);

            notificationService.notifyBeekeeperVerified(person.getId());
        }else
            notificationService.notifyBeekeeperRejected(person.getId(), decision.getComment());

        verificationRepository.save(verification);
    }

    /**
     * Mapowanie BeeGardenVerification → VerificationResponseDTO
     */
    private VerificationResponseDTO mapToDTO(BeeGardenVerification verification) {
        Person person = verification.getPerson();

        // Znajdź BeeGarden powiązany z tym użytkownikiem
        BeeGarden beeGarden = beeGardenRepository.findByPerson(person).stream()
                .findFirst()
                .orElse(null);

        List<DocumentDTO> documentDTOs = verification.getDocuments().stream()
                .map(doc -> DocumentDTO.builder()
                        .id(doc.getId())
                        .type(doc.getType())
                        .filePath(doc.getFilePath())
                        .fileName(Paths.get(doc.getFilePath()).getFileName().toString())
                        .build())
                .collect(Collectors.toList());

        return VerificationResponseDTO.builder()
                .id(verification.getId())
                .status(verification.getStatus())
                .comment(verification.getComment())
                .creationDate(verification.getCreationDate())
                .reviewedDate(verification.getReviewedDate())
                .reviewedBy(verification.getReviewedBy())
                .personId(person.getId())
                .firstname(person.getFirstname())
                .lastname(person.getLastname())
                .email(person.getEmail())
                .phone(person.getPhone())
                .beeGardenName(beeGarden != null ? beeGarden.getName() : null)
                .beeGardenAddress(beeGarden != null ? beeGarden.getAdress() : null)
                .hiveCount(beeGarden != null ? beeGarden.getHiveCount() : null)
                .honeyType(beeGarden != null ? beeGarden.getHoneyType() : null)
                .documents(documentDTOs)
                .build();
    }
}