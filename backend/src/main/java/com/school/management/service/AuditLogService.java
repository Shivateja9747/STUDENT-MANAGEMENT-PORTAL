package com.school.management.service;

import com.school.management.dto.AuditLogDto;
import com.school.management.dto.PagedResponse;
import com.school.management.entity.AuditLog;
import com.school.management.entity.Role;
import com.school.management.repository.AuditLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public void log(String actorUsername, Role actorRole, String action, String details, String oldValue, String newValue, String ipAddress) {
        AuditLog auditLog = AuditLog.builder()
                .actorUsername(actorUsername != null ? actorUsername : "SYSTEM")
                .actorRole(actorRole)
                .action(action)
                .details(details)
                .oldValue(oldValue)
                .newValue(newValue)
                .ipAddress(ipAddress != null ? ipAddress : "127.0.0.1")
                .occurredAt(LocalDateTime.now())
                .build();
        auditLogRepository.save(auditLog);
    }

    public PagedResponse<AuditLogDto> getAuditLogsPaged(int page, int size, String sortBy, String sortDir, String search) {
        String effectiveSortBy = sortBy.equals("timestamp") ? "occurredAt" : sortBy;
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(effectiveSortBy).ascending() : Sort.by(effectiveSortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<AuditLog> pageResult;
        if (search != null && !search.trim().isEmpty()) {
            String query = search.trim();
            pageResult = auditLogRepository.findByActorUsernameContainingIgnoreCaseOrActionContainingIgnoreCaseOrDetailsContainingIgnoreCase(query, query, query, pageable);
        } else {
            pageResult = auditLogRepository.findAll(pageable);
        }

        List<AuditLogDto> content = pageResult.getContent().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return new PagedResponse<>(content, pageResult.getNumber(), pageResult.getSize(), pageResult.getTotalElements(), pageResult.getTotalPages(), pageResult.isLast());
    }

    public String exportAuditLogsCsv() {
        List<AuditLog> logs = auditLogRepository.findAll(Sort.by(Sort.Direction.DESC, "occurredAt"));
        StringBuilder sb = new StringBuilder();
        sb.append("ID,OccurredAt,Actor,Role,Action,Details,OldValue,NewValue,IPAddress\n");

        for (AuditLog log : logs) {
            sb.append(log.getId()).append(",")
              .append(log.getOccurredAt()).append(",")
              .append(escapeCsv(log.getActorUsername())).append(",")
              .append(log.getActorRole()).append(",")
              .append(escapeCsv(log.getAction())).append(",")
              .append(escapeCsv(log.getDetails())).append(",")
              .append(escapeCsv(log.getOldValue())).append(",")
              .append(escapeCsv(log.getNewValue())).append(",")
              .append(escapeCsv(log.getIpAddress())).append("\n");
        }
        return sb.toString();
    }

    private String escapeCsv(String input) {
        if (input == null) return "";
        String clean = input.replace("\"", "\"\"");
        if (clean.contains(",") || clean.contains("\n") || clean.contains("\"")) {
            return "\"" + clean + "\"";
        }
        return clean;
    }

    private AuditLogDto mapToDto(AuditLog log) {
        return AuditLogDto.builder()
                .id(log.getId())
                .actorUsername(log.getActorUsername())
                .actorRole(log.getActorRole())
                .action(log.getAction())
                .details(log.getDetails())
                .oldValue(log.getOldValue())
                .newValue(log.getNewValue())
                .ipAddress(log.getIpAddress())
                .timestamp(log.getOccurredAt())
                .build();
    }
}
