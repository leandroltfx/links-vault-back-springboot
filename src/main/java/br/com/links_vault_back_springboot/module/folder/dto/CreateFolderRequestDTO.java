package br.com.links_vault_back_springboot.module.folder.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateFolderRequestDTO {

    @Size(min = 1, max = 80, message = "Informe o nome da coleção. Não deve ultrapassar 80 caracteres.")
    private String title;

    private String description;

    @NotNull(message = "Informe se a coleção é pública ou privada")
    private Boolean isPublic;

}
