package com.example.realestate.admin;

import com.example.realestate.users.User;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void record(User actor, String action, String entityType, Long entityId, Map<String, Object> metadata) {
        AuditLog log = new AuditLog();
        log.setActor(actor);
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        if (metadata != null && !metadata.isEmpty()) {
            log.setMetadata(metadata.toString());
        }
        auditLogRepository.save(log);
    }
}
