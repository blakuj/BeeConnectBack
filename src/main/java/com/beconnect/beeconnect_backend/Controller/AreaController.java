package com.beconnect.beeconnect_backend.Controller;

import com.beconnect.beeconnect_backend.DTO.AreaDTO;
import com.beconnect.beeconnect_backend.DTO.EditAreaDTO;
import com.beconnect.beeconnect_backend.Service.AreaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AreaController {
    @Autowired
    private AreaService areaService;

    @PostMapping("/addArea")
    public ResponseEntity<?> addArea(@RequestBody AreaDTO areaDto) {
        try {
            areaService.addArea(areaDto);
            return ResponseEntity.ok("Area added successfully");
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @GetMapping("/ownedAreas")
    public ResponseEntity<List<AreaDTO>> getMyAreas(){
        List<AreaDTO> areas = areaService.getOwnedAreas();
        return ResponseEntity.ok(areas);
    }

    @GetMapping("/rentedAreas")
    public ResponseEntity<List<AreaDTO>> getMyRentedAreas(){
        List<AreaDTO> areas = areaService.getRentedAreas();
        return ResponseEntity.ok(areas);
    }


    @GetMapping("/areas")
    public ResponseEntity<List<AreaDTO>> getAllAreas() {
        List<AreaDTO> areas = areaService.getAllAreas();
        return ResponseEntity.ok(areas);
    }



    @PostMapping("/deleteArea/{id}")
    public ResponseEntity<?> deleteArea(@PathVariable Long id) {
        areaService.deleteArea(id);
        return ResponseEntity.ok("Area deleted successfully");
    }


}

