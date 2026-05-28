package ma.enset.digitalbanking.web;

import lombok.RequiredArgsConstructor;
import ma.enset.digitalbanking.dtos.ChangePasswordDTO;
import ma.enset.digitalbanking.dtos.LoginRequestDTO;
import ma.enset.digitalbanking.dtos.LoginResponseDTO;
import ma.enset.digitalbanking.dtos.RegisterRequestDTO;
import ma.enset.digitalbanking.services.AuthService;
import ma.enset.digitalbanking.services.UserService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO loginRequestDTO) {
        return authService.login(loginRequestDTO);
    }

    @PostMapping("/register")
    public LoginResponseDTO register(@RequestBody RegisterRequestDTO registerRequestDTO) {
        return authService.register(registerRequestDTO);
    }

    @PostMapping("/change-password")
    public void changePassword(@RequestBody ChangePasswordDTO changePasswordDTO) {
        userService.changePassword(changePasswordDTO);
    }

    @GetMapping("/me")
    public Map<String, Object> me() {
        return userService.me();
    }
}
