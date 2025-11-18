package com.example.realestate.users;

import com.example.realestate.admin.AuditService;
import com.example.realestate.common.ResourceNotFoundException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import com.example.realestate.users.dto.AdminUserSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuditService auditService;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, AuditService auditService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.auditService = auditService;
    }

    public Page<AdminUserSummary> list(Pageable pageable) {
        return userRepository.findAll(pageable)
            .map(user -> new AdminUserSummary(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getPhone(),
                user.isActive(),
                user.getCreatedAt(),
                user.getPhoneVerifiedAt(),
                user.getRoles().stream().map(Role::getName).collect(Collectors.toSet())
            ));
    }

    public User get(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public void changeActive(Long id, boolean active, User actor) {
        User user = get(id);
        user.setActive(active);
        userRepository.save(user);
        auditService.record(actor, active ? "UNLOCK_USER" : "LOCK_USER", "USER", user.getId(), null);
    }

    public void updateRoles(Long id, Set<String> roles, User actor) {
        User user = get(id);
        Set<Role> newRoles = roles.stream()
            .map(roleName -> roleRepository.findByName(roleName)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName(roleName);
                    return roleRepository.save(role);
                })
            )
            .collect(Collectors.toSet());
        user.setRoles(newRoles);
        userRepository.save(user);
        auditService.record(actor, "UPDATE_ROLES", "USER", user.getId(), Map.of("roles", roles));
    }
}
