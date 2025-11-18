package com.example.realestate.admin;

import com.example.realestate.auth.UserPrincipal;
import com.example.realestate.users.UserService;
import com.example.realestate.users.dto.UserRolesRequest;
import com.example.realestate.users.dto.UserStatusRequest;
import com.example.realestate.users.dto.AdminUserSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
@Validated
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Page<AdminUserSummary> list(Pageable pageable) {
        return userService.list(pageable);
    }

    @PatchMapping("/{id}/status")
    public void changeStatus(
        @PathVariable Long id,
        @RequestBody UserStatusRequest request,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        userService.changeActive(id, request.active(), principal.getUser());
    }

    @PatchMapping("/{id}/roles")
    public void updateRoles(
        @PathVariable Long id,
        @RequestBody @Validated UserRolesRequest request,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        userService.updateRoles(id, request.roles(), principal.getUser());
    }
}
