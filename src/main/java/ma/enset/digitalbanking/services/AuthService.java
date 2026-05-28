package ma.enset.digitalbanking.services;

import lombok.RequiredArgsConstructor;
import ma.enset.digitalbanking.dtos.LoginRequestDTO;
import ma.enset.digitalbanking.dtos.LoginResponseDTO;
import ma.enset.digitalbanking.dtos.RegisterRequestDTO;
import ma.enset.digitalbanking.entities.AppUser;
import ma.enset.digitalbanking.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDTO.getUsername(),
                        loginRequestDTO.getPassword()
                )
        );
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return buildLoginResponse(userDetails);
    }

    public LoginResponseDTO register(RegisterRequestDTO registerRequestDTO) {
        AppUser appUser = userService.registerUser(registerRequestDTO, UserService.ROLE_USER);
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(appUser.getUsername())
                .password(appUser.getPassword())
                .authorities(appUser.getRoles().stream().map(role -> role.getName()).toArray(String[]::new))
                .build();
        return buildLoginResponse(userDetails);
    }

    private LoginResponseDTO buildLoginResponse(UserDetails userDetails) {
        return LoginResponseDTO.builder()
                .token(jwtService.generateToken(userDetails))
                .tokenType("Bearer")
                .username(userDetails.getUsername())
                .roles(userDetails.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList())
                .build();
    }
}
