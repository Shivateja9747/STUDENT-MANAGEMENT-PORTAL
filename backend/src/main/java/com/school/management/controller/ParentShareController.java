package com.school.management.controller;

import com.school.management.entity.ParentShareLink;
import com.school.management.service.ParentShareService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ParentShareController {

    private final ParentShareService parentShareService;

    public ParentShareController(ParentShareService parentShareService) {
        this.parentShareService = parentShareService;
    }

    @PostMapping("/student/share-link")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ParentShareLink> createShareLink(
            Authentication auth,
            @RequestBody(required = false) Map<String, Integer> body) {
        Integer days = (body != null && body.containsKey("durationDays")) ? body.get("durationDays") : 30;
        return ResponseEntity.ok(parentShareService.createShareLink(auth.getName(), days));
    }

    @GetMapping("/student/share-link")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<ParentShareLink>> getMyShareLinks(Authentication auth) {
        return ResponseEntity.ok(parentShareService.getStudentShareLinks(auth.getName()));
    }

    @DeleteMapping("/student/share-link/{id}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Map<String, String>> revokeShareLink(@PathVariable Long id, Authentication auth) {
        parentShareService.revokeShareLink(auth.getName(), id);
        return ResponseEntity.ok(Map.of("message", "Share link successfully revoked"));
    }

    @GetMapping("/public/parent-portal/{token}")
    public ResponseEntity<Map<String, Object>> getPublicParentPortalData(@PathVariable String token) {
        return ResponseEntity.ok(parentShareService.getPublicParentPortalData(token));
    }
}
