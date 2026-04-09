package br.com.links_vault_back_springboot.module.user.useCase;

import br.com.links_vault_back_springboot.module.user.dto.UpdatePasswordRequestDTO;
import br.com.links_vault_back_springboot.module.user.dto.UserResponseDTO;
import br.com.links_vault_back_springboot.module.user.entity.UserEntity;
import br.com.links_vault_back_springboot.module.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class UpdatePasswordUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UpdatePasswordUseCase updatePasswordUseCase;

    @Test
    @DisplayName(value = "Deve alterar alterar a senha e criptografá-la")
    void shouldUpdatePasswordSuccessfully() {
        UUID userId = UUID.randomUUID();

        UserEntity existingUser = new UserEntity();
        existingUser.setId(userId);
        existingUser.setUsername("leandro");
        existingUser.setEmail("leandro@email.com");
        existingUser.setPassword("oldPassword");
        existingUser.setCreatedAt(LocalDateTime.now());

        UpdatePasswordRequestDTO requestDTO = new UpdatePasswordRequestDTO();
        requestDTO.setPassword("newPassword");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedPassword");

        UserResponseDTO response = updatePasswordUseCase.execute(userId, requestDTO);

        assertNotNull(response);
        assertEquals(existingUser.getUsername(), response.getUsername());
        assertEquals(existingUser.getEmail(), response.getEmail());
        assertEquals(existingUser.getCreatedAt(), response.getCreatedAt());

        verify(userRepository, times(1)).save(any(UserEntity.class));
        verify(passwordEncoder, times(1)).encode("newPassword");
    }

    @Test
    @DisplayName(value = "Deve lançar a EntityNotFoundException caso o usuário não seja encontrado pelo ID")
    void shouldThrowEntityNotFoundExceptionWhenUserDoesNotExist() {
        UUID userId = UUID.randomUUID();

        UpdatePasswordRequestDTO requestDTO = new UpdatePasswordRequestDTO();
        requestDTO.setPassword("newPassword");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> updatePasswordUseCase.execute(userId, requestDTO)
        );

        assertEquals("Usuário não encontrado.", exception.getMessage());

        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    @DisplayName(value = "Deve lançar a RuntimeException caso um erro desconhecido ocorra")
    void shouldThrowRuntimeExceptionWhenUnexpectedErrorOccurs() {
        UUID userId = UUID.randomUUID();

        UserEntity existingUser = new UserEntity();
        existingUser.setId(userId);

        UpdatePasswordRequestDTO requestDTO = new UpdatePasswordRequestDTO();
        requestDTO.setPassword("newPassword");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode(any())).thenThrow(new RuntimeException());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> updatePasswordUseCase.execute(userId, requestDTO)
        );

        assertEquals(
                "Ocorreu um erro durante a alteração de senha, tente novamente.",
                exception.getMessage()
        );
    }
}
