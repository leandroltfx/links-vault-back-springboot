package br.com.links_vault_back_springboot.module.login.useCase;

import br.com.links_vault_back_springboot.module.login.dto.LoginRequestDTO;
import br.com.links_vault_back_springboot.module.login.dto.LoginResponseDTO;
import br.com.links_vault_back_springboot.module.user.repository.UserRepository;
import br.com.links_vault_back_springboot.service.JWTService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.security.auth.login.CredentialException;

@Service
@AllArgsConstructor
public class LoginUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;

    public LoginResponseDTO execute(
            LoginRequestDTO loginRequestDTO
    ) throws CredentialException {
        try {
            var user = this.userRepository.findByUsernameOrEmail(
                    loginRequestDTO.getUsername(),
                    loginRequestDTO.getEmail()
            ).orElseThrow(() -> new CredentialException());

            var passwordMatches = this.passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword());

            if (!passwordMatches) {
                throw new CredentialException();
            }

            return LoginResponseDTO
                    .builder()
                    .accessToken(this.jwtService.generateToken(user.getId()))
                    .build();
        } catch (CredentialException credentialException) {
            throw new CredentialException("Credenciais inválidas.");
        } catch (Exception exception) {
            throw new RuntimeException("Ocorreu um erro durante o login, tente novamente.");
        }
    }

}