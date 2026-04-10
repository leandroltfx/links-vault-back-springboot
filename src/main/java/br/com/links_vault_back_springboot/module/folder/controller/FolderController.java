package br.com.links_vault_back_springboot.module.folder.controller;

import br.com.links_vault_back_springboot.dto.ApiResponseDTO;
import br.com.links_vault_back_springboot.module.folder.dto.CreateFolderRequestDTO;
import br.com.links_vault_back_springboot.module.folder.useCase.CreateFolderUseCase;
import br.com.links_vault_back_springboot.service.UtilService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/folders")
@AllArgsConstructor
public class FolderController {

    private final CreateFolderUseCase createFolderUseCase;
    private final UtilService utilService;

    @PostMapping
    public ResponseEntity<ApiResponseDTO> createFolder(
            HttpServletRequest httpServletRequest,
            @RequestBody @Valid CreateFolderRequestDTO createFolderRequestDTO
    ) {

        var createdFolder = this.createFolderUseCase.execute(
                this.utilService.extractUserIdFromRequest(httpServletRequest),
                createFolderRequestDTO
        );
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponseDTO
                                .builder()
                                .message("Pasta criada com sucesso!")
                                .data(createdFolder)
                                .build()
                );
    }

}
