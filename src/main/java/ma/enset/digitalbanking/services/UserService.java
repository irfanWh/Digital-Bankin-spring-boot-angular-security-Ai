package ma.enset.digitalbanking.services;

import lombok.RequiredArgsConstructor;
import ma.enset.digitalbanking.dtos.ChangePasswordDTO;
import ma.enset.digitalbanking.dtos.RegisterRequestDTO;
import ma.enset.digitalbanking.entities.AppRole;
import ma.enset.digitalbanking.entities.AppUser;
import ma.enset.digitalbanking.exceptions.OperationNotAllowedException;
import ma.enset.digitalbanking.repositories.AppRoleRepository;
import ma.enset.digitalbanking.repositories.AppUserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_USER = "ROLE_USER";

    private final AppUserRepository appUserRepository;
    private final AppRoleRepository appRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public AppRole saveRoleIfMissing(String roleName) {
        return appRoleRepository.findByName(roleName)
                .orElseGet(() -> appRoleRepository.save(AppRole.builder().name(roleName).build()));
    }

    public AppUser registerUser(RegisterRequestDTO request, String roleName) {
        if (appUserRepository.existsByUsername(request.getUsername())) {
            throw new OperationNotAllowedException("Username already exists");
        }
        if (appUserRepository.existsByEmail(request.getEmail())) {
            throw new OperationNotAllowedException("Email already exists");
        }

        AppRole role = saveRoleIfMissing(roleName);
        AppUser appUser = AppUser.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(true)
                .roles(List.of(role))
                .build();
        return appUserRepository.save(appUser);
    }

    public AppUser createUserIfMissing(String username, String email, String rawPassword, List<String> roleNames) {
        return appUserRepository.findByUsername(username)
                .orElseGet(() -> {
                    List<AppRole> roles = roleNames.stream()
                            .map(this::saveRoleIfMissing)
                            .toList();
                    AppUser appUser = AppUser.builder()
                            .username(username)
                            .email(email)
                            .password(passwordEncoder.encode(rawPassword))
                            .enabled(true)
                            .roles(roles)
                            .build();
                    return appUserRepository.save(appUser);
                });
    }

    public void changePassword(ChangePasswordDTO changePasswordDTO) {
        AppUser appUser = currentUser();
        if (!passwordEncoder.matches(changePasswordDTO.getOldPassword(), appUser.getPassword())) {
            throw new OperationNotAllowedException("Old password is invalid");
        }
        appUser.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        appUserRepository.save(appUser);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> me() {
        AppUser appUser = currentUser();
        return Map.of(
                "id", appUser.getId(),
                "username", appUser.getUsername(),
                "email", appUser.getEmail(),
                "roles", appUser.getRoles().stream().map(AppRole::getName).toList(),
                "enabled", appUser.isEnabled()
        );
    }

    private AppUser currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new OperationNotAllowedException("No authenticated user found");
        }
        return appUserRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new OperationNotAllowedException("Authenticated user not found"));
    }
}
