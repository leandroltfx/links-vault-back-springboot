package br.com.links_vault_back_springboot.module.user.useCase;

import br.com.links_vault_back_springboot.module.user.dto.UpdateEmailRequestDTO;
import br.com.links_vault_back_springboot.module.user.dto.UserResponseDTO;
import br.com.links_vault_back_springboot.module.user.entity.UserEntity;
import br.com.links_vault_back_springboot.module.user.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateEmailUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UpdateEmailUseCase updateEmailUseCase;

    private UUID userId;
    private UserEntity userEntity;
    private UpdateEmailRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setUsername("testuser");
        userEntity.setEmail("old@email.com");
        userEntity.setPassword("123");
        userEntity.setCreatedAt(LocalDateTime.now());

        requestDTO = new UpdateEmailRequestDTO();
        requestDTO.setEmail("new@email.com");
    }

    @Test
    @DisplayName(value = "Deve alterar o e-mail do usuário")
    void shouldUpdateEmailSuccessfully() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponseDTO response = updateEmailUseCase.execute(userId, requestDTO);

        assertNotNull(response);
        assertEquals("testuser", response.getUsername());
        assertEquals("new@email.com", response.getEmail());
        assertEquals(userEntity.getCreatedAt(), response.getCreatedAt());

        verify(userRepository).findById(userId);
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    @DisplayName(value = "Deve lançar a EntityNotFoundException caso o usuário não seja encontrado pelo ID")
    void shouldThrowEntityNotFoundExceptionWhenUserDoesNotExist() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> updateEmailUseCase.execute(userId, requestDTO)
        );

        assertEquals("Usuário não encontrado.", exception.getMessage());

        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName(value = "Deve lançar a EntityExistsException caso o e-mail escolhido para alteração já exista")
    void shouldThrowEntityExistsExceptionWhenEmailAlreadyExists() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(userRepository.save(any(UserEntity.class)))
                .thenThrow(new DataIntegrityViolationException("constraint"));

        EntityExistsException exception = assertThrows(
                EntityExistsException.class,
                () -> updateEmailUseCase.execute(userId, requestDTO)
        );

        assertEquals("Este e-mail já está em uso.", exception.getMessage());

        verify(userRepository).findById(userId);
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    @DisplayName(value = "Deve lançar a RuntimeException caso um erro desconhecido ocorra")
    void shouldThrowGenericExceptionWhenUnexpectedErrorOccurs() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(userRepository.save(any(UserEntity.class)))
                .thenThrow(new RuntimeException("unexpected"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> updateEmailUseCase.execute(userId, requestDTO)
        );

        assertEquals(
                "Ocorreu um erro durante a alteração do e-mail, tente novamente.",
                exception.getMessage()
        );

        verify(userRepository).findById(userId);
        verify(userRepository).save(any(UserEntity.class));
    }
}
