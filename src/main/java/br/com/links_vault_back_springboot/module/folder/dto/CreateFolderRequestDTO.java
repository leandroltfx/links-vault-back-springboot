package br.com.links_vault_back_springboot.module.folder.dto;

import br.com.links_vault_back_springboot.annotation.ValidFolderTitle;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateFolderRequestDTO {

    @ValidFolderTitle
    private String title;

    private String description;

    @NotNull(message = "Informe se a coleção é pública ou privada")
    private Boolean isPublic;

}
