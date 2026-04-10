package br.com.links_vault_back_springboot.module.folder.useCase;

import br.com.links_vault_back_springboot.module.folder.dto.CreateFolderRequestDTO;
import br.com.links_vault_back_springboot.module.folder.dto.FolderResponseDTO;
import br.com.links_vault_back_springboot.module.folder.entity.FolderEntity;
import br.com.links_vault_back_springboot.module.folder.repository.FolderRepository;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateFolderUseCaseTest {

    @Mock
    private FolderRepository folderRepository;

    @InjectMocks
    private CreateFolderUseCase createFolderUseCase;

    @Test
    @DisplayName(value = "Deve criar uma pasta de links")
    void shouldCreateCollectionSuccessfully() {
        UUID userId = UUID.randomUUID();

        CreateFolderRequestDTO requestDTO = new CreateFolderRequestDTO();
        requestDTO.setTitle("Links úteis");
        requestDTO.setDescription("10 links mais acessados");
        requestDTO.setIsPublic(true);

        when(folderRepository.findByUserIdAndTitle(userId, "Links úteis")).thenReturn(Optional.empty());

        FolderEntity savedEntity = FolderEntity.builder()
                .id(UUID.randomUUID())
                .title(requestDTO.getTitle())
                .description(requestDTO.getDescription())
                .isPublic(requestDTO.getIsPublic())
                .userId(userId)
                .createdAt(LocalDateTime.now())
                .build();

        when(folderRepository.save(any(FolderEntity.class))).thenReturn(savedEntity);

        FolderResponseDTO response = createFolderUseCase.execute(userId, requestDTO);

        assertNotNull(response);
        assertEquals(savedEntity.getId(), response.getId());
        assertEquals(savedEntity.getTitle(), response.getTitle());
        assertEquals(savedEntity.getDescription(), response.getDescription());
        assertEquals(savedEntity.getIsPublic(), response.getIsPublic());
        assertEquals(savedEntity.getCreatedAt(), response.getCreatedAt());

        verify(folderRepository, times(1)).findByUserIdAndTitle(userId, "Links úteis");
        verify(folderRepository, times(1)).save(any(FolderEntity.class));
    }

    @Test
    @DisplayName("Deve lançar a EntityExistsException caso já exista na base de dados uma pasta com o mesmo nome dá que está sendo cadastrada")
    void shouldThrowEntityExistsExceptionWhenCollectionAlreadyExists() {
        UUID userId = UUID.randomUUID();

        CreateFolderRequestDTO requestDTO = new CreateFolderRequestDTO();
        requestDTO.setTitle("Links úteis");

        when(folderRepository.findByUserIdAndTitle(userId, "Links úteis")).thenReturn(Optional.of(new FolderEntity()));

        EntityExistsException exception = assertThrows(
                EntityExistsException.class,
                () -> createFolderUseCase.execute(userId, requestDTO)
        );

        assertEquals("Já existe uma pasta com esse nome.", exception.getMessage());

        verify(folderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar a RuntimeException em caso de um erro desconhecido na criação de pasta")
    void shouldThrowRuntimeExceptionWhenUnexpectedErrorOccurs() {
        UUID userId = UUID.randomUUID();

        CreateFolderRequestDTO requestDTO = new CreateFolderRequestDTO();
        requestDTO.setTitle("Links úteis");

        when(folderRepository.findByUserIdAndTitle(any(), any())).thenThrow(new RuntimeException("Erro desconhecido"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> createFolderUseCase.execute(userId, requestDTO)
        );

        assertEquals(
                "Ocorreu um erro durante a criação da pasta, tente novamente.",
                exception.getMessage()
        );
    }
}
