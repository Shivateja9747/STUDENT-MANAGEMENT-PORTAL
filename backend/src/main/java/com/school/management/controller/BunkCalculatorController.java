package com.school.management.controller;

import com.school.management.dto.BunkCalculatorDto;
import com.school.management.service.BunkCalculatorService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student/bunk-calculator")
@PreAuthorize("hasRole('STUDENT')")
public class BunkCalculatorController {

    private final BunkCalculatorService bunkCalculatorService;

    public BunkCalculatorController(BunkCalculatorService bunkCalculatorService) {
        this.bunkCalculatorService = bunkCalculatorService;
    }

    @GetMapping
    public ResponseEntity<List<BunkCalculatorDto>> calculateBunkMargin(
            @RequestParam(value = "target", required = false, defaultValue = "75.0") Double target,
            Authentication auth) {
        return ResponseEntity.ok(bunkCalculatorService.calculateBunkMarginForStudent(auth.getName(), target));
    }
}
