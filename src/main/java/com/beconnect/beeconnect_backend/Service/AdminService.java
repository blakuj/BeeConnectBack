// Rozszerzony AdminService.java

package com.beconnect.beeconnect_backend.Service;

import com.beconnect.beeconnect_backend.DTO.*;
import com.beconnect.beeconnect_backend.Enum.Role;
import com.beconnect.beeconnect_backend.Enum.Status;
import com.beconnect.beeconnect_backend.Model.Area;
import com.beconnect.beeconnect_backend.Model.Person;
import com.beconnect.beeconnect_backend.Repository.AreaRepository;
import com.beconnect.beeconnect_backend.Repository.BeeGardenVerificationRepository;
import com.beconnect.beeconnect_backend.Repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private BeeGardenVerificationRepository verificationRepository;

    @Autowired
    private AreaRepository areaRepository;



    // ============ STATYSTYKI ============
    public AdminStatsDTO getAdminStatistics() {
        long totalUsers = personRepository.count();
        long verifiedBeekeepers = personRepository.countByRole(Role.BEEKEEPER);
        long pendingVerifications = verificationRepository.countByStatus(Status.PENDING);
        long rejectedVerifications = verificationRepository.countByStatus(Status.REJECTED);
        long totalVerifications = verificationRepository.count();
        long totalAreas = areaRepository.count();
        long activeAreas = areaRepository.countByStatus("AVAILABLE");

        return AdminStatsDTO.builder()
                .totalUsers(totalUsers)
                .verifiedBeekeepers(verifiedBeekeepers)
                .pendingVerifications(pendingVerifications)
                .rejectedVerifications(rejectedVerifications)
                .totalVerifications(totalVerifications)
                .totalAreas(totalAreas)
                .activeAreas(activeAreas)
                .build();
    }

    // ============ ZARZĄDZANIE UŻYTKOWNIKAMI ============

    public List<UserDTO> getAllUsers(String search, String role) {
        List<Person> users;

        if (role != null && !role.isEmpty()) {
            Role roleEnum = Role.valueOf(role.toUpperCase());
            users = personRepository.findByRole(roleEnum);
        } else {
            users = personRepository.findAll();
        }

        // Filtrowanie po wyszukiwaniu
        if (search != null && !search.isEmpty()) {
            String searchLower = search.toLowerCase();
            users = users.stream()
                    .filter(u ->
                            u.getFirstname().toLowerCase().contains(searchLower) ||
                                    u.getLastname().toLowerCase().contains(searchLower) ||
                                    u.getEmail().toLowerCase().contains(searchLower)
                    )
                    .collect(Collectors.toList());
        }

        return users.stream()
                .map(this::mapPersonToUserDTO)
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id) {
        Person user = personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));
        return mapPersonToUserDTO(user);
    }

    @Transactional
    public void blockUser(Long id, String reason) {
        Person user = personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

        if (user.getRole() == Role.ADMIN) {
            throw new RuntimeException("Nie można zablokować administratora");
        }

        user.setActive(false);
        user.setBlockReason(reason);
        personRepository.save(user);

        // Opcjonalnie: wyślij email do użytkownika
        // emailService.sendBlockNotification(user, reason);
    }

    @Transactional
    public void unblockUser(Long id) {
        Person user = personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

        user.setActive(true);
        user.setBlockReason(null);
        personRepository.save(user);

        // Opcjonalnie: wyślij email do użytkownika
        // emailService.sendUnblockNotification(user);
    }

    @Transactional
    public void changeUserRole(Long id, String newRole) {
        Person user = personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

        Role role = Role.valueOf(newRole.toUpperCase());

        // Zabezpieczenie przed usunięciem ostatniego admina
        if (user.getRole() == Role.ADMIN && role != Role.ADMIN) {
            long adminCount = personRepository.countByRole(Role.ADMIN);
            if (adminCount <= 1) {
                throw new RuntimeException("Nie można zmienić roli ostatniego administratora");
            }
        }

        user.setRole(role);
        personRepository.save(user);
    }

    // ============ ZARZĄDZANIE OBSZARAMI ============

    public List<AreaDTO> getAllAreas(String status) {
        List<Area> areas;

        if (status != null && !status.isEmpty()) {
            areas = areaRepository.findByStatus(status);
        } else {
            areas = areaRepository.findAll();
        }

        return areas.stream()
                .map(this::mapAreaToDTO)
                .collect(Collectors.toList());
    }

    public AreaDTO getAreaById(Long id) {
        Area area = areaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Obszar nie znaleziony"));
        return mapAreaToDTO(area);
    }

    @Transactional
    public void deleteArea(Long id, String reason, boolean notifyOwner) {
        Area area = areaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Obszar nie znaleziony"));

        Person owner = area.getOwner();

        // Zapisz informację o usunięciu przed faktycznym usunięciem
        if (notifyOwner && owner != null) {
            // Opcjonalnie: wyślij powiadomienie do właściciela
            // emailService.sendAreaDeletionNotification(owner, area, reason);
        }

        areaRepository.delete(area);
    }

    @Transactional
    public void changeAreaStatus(Long id, String newStatus) {
        Area area = areaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Obszar nie znaleziony"));

        area.setStatus(newStatus);
        areaRepository.save(area);
    }

    // ============ METODY POMOCNICZE ============

    private UserDTO mapPersonToUserDTO(Person person) {
        return UserDTO.builder()
                .id(person.getId())
                .firstname(person.getFirstname())
                .lastname(person.getLastname())
                .email(person.getEmail())
                .phone(person.getPhone())
                .role(person.getRole())
                .active(person.isActive())
                .registrationDate(person.getRegistrationDate())
                .lastLoginDate(person.getLastLoginDate())
                .hasVerification(person.getVerification() != null)
                .verificationStatus(person.getVerification() != null ?
                        person.getVerification().getStatus() : null)
                .blockReason(person.getBlockReason())
                .build();
    }

    private AreaDTO mapAreaToDTO(Area area) {
        List<List<Double>> coords = area.getCoordinates().stream()
                .map(s -> {
                    String[] parts = s.split(",");
                    return List.of(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
                })
                .collect(Collectors.toList());

        return AreaDTO.builder()
                .id(area.getId())
                .type(area.getType())
                .coordinates(coords)
                .area(area.getArea())
                .description(area.getDescription())
                .maxHives(area.getMaxHives())
                .pricePerDay(area.getPricePerDay())
                .status(area.getStatus())
                .ownerName(area.getOwner() != null ?
                        area.getOwner().getFirstname() + " " + area.getOwner().getLastname() : null)
                .ownerId(area.getOwner() != null ? area.getOwner().getId() : null)
                .dateAdded(area.getDateAdded())
                .build();
    }
}