package com.beconnect.beeconnect_backend.Controller;

import com.beconnect.beeconnect_backend.DTO.VerificationDTO;
import com.beconnect.beeconnect_backend.Service.BeeGardenVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;



@RestController
@RequestMapping("/api/verification")
public class BeeGardenVerificationController {
    @Autowired
    BeeGardenVerificationService beeGardenVerificationService;

    @PostMapping("/submit")
    public ResponseEntity<?> submit(@RequestPart("file") MultipartFile file,
                                   @RequestPart("verificationDTO") VerificationDTO verificationDTO){
        try {
            beeGardenVerificationService.submitAnApplication(file, verificationDTO);
            return ResponseEntity.ok().build();
        }catch (Exception e) {
           return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

