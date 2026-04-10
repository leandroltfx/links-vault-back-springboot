package br.com.links_vault_back_springboot.module.folder.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FolderResponseDTO {

    private UUID id;

    private String title;

    private String description;

    private Boolean isPublic;

    private LocalDateTime createdAt;

}
