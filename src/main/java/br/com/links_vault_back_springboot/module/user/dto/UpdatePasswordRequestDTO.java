package br.com.links_vault_back_springboot.module.user.dto;

import br.com.links_vault_back_springboot.annotation.ValidPassword;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdatePasswordRequestDTO {

    @ValidPassword
    private String password;

}
