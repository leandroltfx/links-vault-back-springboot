package br.com.links_vault_back_springboot.module.user.dto;

import br.com.links_vault_back_springboot.annotation.ValidUsername;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUsernameRequestDTO {

    @ValidUsername
    private String username;

}
