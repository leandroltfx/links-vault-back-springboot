package br.com.links_vault_back_springboot.module.folder.controller;

import br.com.links_vault_back_springboot.dto.ApiResponseDTO;
import br.com.links_vault_back_springboot.module.folder.dto.CreateFolderRequestDTO;
import br.com.links_vault_back_springboot.module.folder.dto.FolderResponseDTO;
import br.com.links_vault_back_springboot.module.folder.useCase.CreateFolderUseCase;
import br.com.links_vault_back_springboot.service.UtilService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FolderControllerTest {

    @Mock
    private CreateFolderUseCase createFolderUseCase;

    @Mock
    private UtilService utilService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private FolderController folderController;

    @Test
    @DisplayName(value = "Deve retornar o status 201 ao criar uma pasta de links")
    void shouldCreateFolderSuccessfully() {
        UUID userId = UUID.randomUUID();

        CreateFolderRequestDTO requestDTO = new CreateFolderRequestDTO();
        requestDTO.setTitle("Links úteis");
        requestDTO.setDescription("10 links mais acessados");
        requestDTO.setIsPublic(true);

        FolderResponseDTO responseDTO = FolderResponseDTO.builder()
                .id(UUID.randomUUID())
                .title("Links úteis")
                .description("10 links mais acessados")
                .isPublic(true)
                .createdAt(LocalDateTime.now())
                .build();

        when(utilService.extractUserIdFromRequest(request)).thenReturn(userId);
        when(createFolderUseCase.execute(userId, requestDTO)).thenReturn(responseDTO);

        ResponseEntity<ApiResponseDTO> response = folderController.createFolder(request, requestDTO);

        assertNotNull(response);
        assertEquals(201, response.getStatusCode().value());

        ApiResponseDTO body = response.getBody();
        assertNotNull(body);
        assertEquals("Pasta criada com sucesso!", body.getMessage());
        assertEquals(responseDTO, body.getData());

        verify(utilService, times(1)).extractUserIdFromRequest(request);
        verify(createFolderUseCase, times(1)).execute(userId, requestDTO);
    }
}
