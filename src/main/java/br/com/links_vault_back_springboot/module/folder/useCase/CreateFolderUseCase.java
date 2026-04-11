package br.com.links_vault_back_springboot.module.folder.useCase;

import br.com.links_vault_back_springboot.module.folder.dto.CreateFolderRequestDTO;
import br.com.links_vault_back_springboot.module.folder.dto.FolderResponseDTO;
import br.com.links_vault_back_springboot.module.folder.entity.FolderEntity;
import br.com.links_vault_back_springboot.module.folder.repository.FolderRepository;
import jakarta.persistence.EntityExistsException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class CreateFolderUseCase {

    private final FolderRepository folderRepository;

    public FolderResponseDTO execute(
            UUID userId,
            CreateFolderRequestDTO createFolderRequestDTO
    ) {
        try {

            var optionalFolderEntity = this.folderRepository.findByUserIdAndTitle(
                    userId,
                    createFolderRequestDTO.getTitle()
            );
            if (optionalFolderEntity.isPresent()) {
                throw new EntityExistsException();
            }

            FolderEntity folderEntity = FolderEntity
                    .builder()
                    .title(createFolderRequestDTO.getTitle())
                    .description(createFolderRequestDTO.getDescription())
                    .isPublic(createFolderRequestDTO.getIsPublic())
                    .userId(userId)
                    .build();
            var createdFolder = this.folderRepository.save(folderEntity);
            return FolderResponseDTO
                    .builder()
                    .id(createdFolder.getId())
                    .title(createdFolder.getTitle())
                    .description(createdFolder.getDescription())
                    .isPublic(createdFolder.getIsPublic())
                    .createdAt(createdFolder.getCreatedAt())
                    .build();
        } catch (EntityExistsException entityExistsException) {
            throw new EntityExistsException("Já existe uma pasta com esse nome.");
        } catch (Exception exception) {
            throw new RuntimeException("Ocorreu um erro durante a criação da pasta, tente novamente.");
        }
    }

}
