package br.com.links_vault_back_springboot.module.login.useCase;

import br.com.links_vault_back_springboot.module.login.dto.LoginRequestDTO;
import br.com.links_vault_back_springboot.module.login.dto.LoginResponseDTO;
import br.com.links_vault_back_springboot.module.user.entity.UserEntity;
import br.com.links_vault_back_springboot.module.user.repository.UserRepository;
import br.com.links_vault_back_springboot.service.JWTService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.security.auth.login.CredentialException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginUseCaseTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JWTService jwtService;

    private LoginUseCase loginUseCase;

    @BeforeEach
    void setup() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtService = mock(JWTService.class);

        loginUseCase = new LoginUseCase(
                userRepository,
                passwordEncoder,
                jwtService
        );
    }

    @Test
    @DisplayName(value = "Deve realizar o login")
    void shouldLoginSuccessfully() throws Exception {

        UUID userId = UUID.randomUUID();

        LoginRequestDTO request = LoginRequestDTO.builder()
                .username("leandro")
                .password("123456")
                .build();

        UserEntity user = UserEntity.builder()
                .id(userId)
                .username("leandro")
                .password("encodedPassword")
                .build();

        when(userRepository.findByUsernameOrEmail("leandro", null))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("123456", "encodedPassword"))
                .thenReturn(true);

        when(jwtService.generateToken(userId))
                .thenReturn("jwt-token");

        LoginResponseDTO response = loginUseCase.execute(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.getAccessToken());

        verify(userRepository).findByUsernameOrEmail("leandro", null);
        verify(passwordEncoder).matches("123456", "encodedPassword");
        verify(jwtService).generateToken(userId);
    }

    @Test
    @DisplayName(value = "Deve lançar a CredentialException no caso de nome de usuário não cadastrado")
    void shouldThrowExceptionWhenUserNotFound() {

        LoginRequestDTO request = LoginRequestDTO.builder()
                .username("leandro")
                .password("123456")
                .build();

        when(userRepository.findByUsernameOrEmail("leandro", null))
                .thenReturn(Optional.empty());

        CredentialException exception = assertThrows(
                CredentialException.class,
                () -> loginUseCase.execute(request)
        );

        assertEquals("Credenciais inválidas.", exception.getMessage());
    }

    @Test
    @DisplayName(value = "Deve lançar a CredentialException no caso de e-mail não cadastrado")
    void shouldThrowExceptionWhenEmailDoesNotMatch() {

        LoginRequestDTO request = LoginRequestDTO.builder()
                .email("leandro@email.com")
                .password("123456")
                .build();

        when(userRepository.findByUsernameOrEmail(null, "leandro@email.com"))
                .thenReturn(Optional.empty());

        CredentialException exception = assertThrows(
                CredentialException.class,
                () -> loginUseCase.execute(request)
        );

        assertEquals("Credenciais inválidas.", exception.getMessage());
    }

    @Test
    @DisplayName(value = "Deve lançar a CredentialException no caso de senha não cadastrada")
    void shouldThrowExceptionWhenPasswordDoesNotMatch() {

        LoginRequestDTO request = LoginRequestDTO.builder()
                .username("leandro")
                .password("wrongPassword")
                .build();

        UserEntity user = UserEntity.builder()
                .id(UUID.randomUUID())
                .password("encodedPassword")
                .build();

        when(userRepository.findByUsernameOrEmail("leandro", null))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("wrongPassword", "encodedPassword"))
                .thenReturn(false);

        CredentialException exception = assertThrows(
                CredentialException.class,
                () -> loginUseCase.execute(request)
        );

        assertEquals("Credenciais inválidas.", exception.getMessage());
    }

    @Test
    @DisplayName(value = "Deve lançar a RuntimeException no caso um erro desconhecido")
    void shouldThrowRuntimeExceptionWhenRepositoryFails() {

        LoginRequestDTO request = LoginRequestDTO.builder()
                .username("leandro")
                .password("wrongPassword")
                .build();

        when(userRepository.findByUsernameOrEmail("leandro", null))
                .thenThrow(new RuntimeException("Ocorreu um erro durante o login, tente novamente."));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> loginUseCase.execute(request)
        );

        assertEquals(
                "Ocorreu um erro durante o login, tente novamente.",
                exception.getMessage()
        );
    }
}
