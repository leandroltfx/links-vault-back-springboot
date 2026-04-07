package br.com.links_vault_back_springboot.module.user.useCase;

import br.com.links_vault_back_springboot.module.user.dto.CreateUserRequestDTO;
import br.com.links_vault_back_springboot.module.user.dto.CreateUserResponseDTO;
import br.com.links_vault_back_springboot.module.user.entity.UserEntity;
import br.com.links_vault_back_springboot.module.user.repository.UserRepository;
import br.com.links_vault_back_springboot.service.JWTService;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreateUserUseCaseTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JWTService jwtService;

    private CreateUserUseCase createUserUseCase;

    @BeforeEach
    void setup() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtService = mock(JWTService.class);

        createUserUseCase = new CreateUserUseCase(
                userRepository,
                passwordEncoder,
                jwtService
        );
    }

    @Test
    @DisplayName(value = "Deve cadastrar um usuário")
    void shouldCreateUserSuccessfully() {

        UUID userId = UUID.randomUUID();

        CreateUserRequestDTO request = CreateUserRequestDTO
                .builder()
                .username("user")
                .email("user@email.com")
                .password("password123")
                .build();

        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        UserEntity savedUser = UserEntity.builder()
                .id(userId)
                .username("user")
                .email("user@email.com")
                .password("encodedPassword")
                .build();

        when(userRepository.save(any(UserEntity.class))).thenReturn(savedUser);
        when(jwtService.generateToken(userId)).thenReturn("jwt-token");

        CreateUserResponseDTO response = createUserUseCase.execute(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.getAccessToken());

        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(UserEntity.class));
        verify(jwtService).generateToken(userId);
    }

    @Test
    @DisplayName(value = "Deve criptografar a senha durante o cadastro do usuário")
    void shouldEncodePasswordBeforeSavingUser() {

        CreateUserRequestDTO request = CreateUserRequestDTO
                .builder()
                .username("user")
                .email("user@email.com")
                .password("password123")
                .build();

        when(passwordEncoder.encode("password123")).thenReturn("encoded");

        UserEntity savedUser = UserEntity.builder()
                .id(UUID.randomUUID())
                .build();

        when(userRepository.save(any())).thenReturn(savedUser);
        when(jwtService.generateToken(any())).thenReturn("token");

        createUserUseCase.execute(request);

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(captor.capture());

        UserEntity capturedUser = captor.getValue();

        assertEquals("encoded", capturedUser.getPassword());
    }

    @Test
    @DisplayName(value = "Deve lançar a exceção EntityExistsException se o nome de usuário e/ou e-mail já estiverem cadastrados")
    void shouldThrowEntityExistsExceptionWhenUsernameOrEmailExists() {

        CreateUserRequestDTO request = CreateUserRequestDTO
                .builder()
                .username("user")
                .email("user@email.com")
                .password("password123")
                .build();

        when(passwordEncoder.encode(any())).thenReturn("encoded");

        when(userRepository.save(any()))
                .thenThrow(new DataIntegrityViolationException(""));

        EntityExistsException exception = assertThrows(
                EntityExistsException.class,
                () -> createUserUseCase.execute(request)
        );

        assertEquals(
                "Este nome de usuário ou e-mail já está em uso.",
                exception.getMessage()
        );
    }

    @Test
    @DisplayName(value = "Deve lançar a exceção RuntimeException quando houver algum erro desconhecido")
    void shouldThrowRuntimeExceptionWhenRepositoryFails() {

        CreateUserRequestDTO request = CreateUserRequestDTO
                .builder()
                .username("user")
                .email("user@email.com")
                .password("password123")
                .build();

        when(passwordEncoder.encode(any())).thenReturn("encoded");

        when(userRepository.save(any()))
                .thenThrow(new RuntimeException("Ocorreu um erro durante o cadastro, tente novamente."));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> createUserUseCase.execute(request)
        );

        assertEquals(
                "Ocorreu um erro durante o cadastro, tente novamente.",
                exception.getMessage()
        );
    }
}
