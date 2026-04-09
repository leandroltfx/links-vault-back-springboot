package br.com.links_vault_back_springboot.module.user.controller;

import br.com.links_vault_back_springboot.dto.ApiResponseDTO;
import br.com.links_vault_back_springboot.module.user.dto.CreateUserRequestDTO;
import br.com.links_vault_back_springboot.module.user.dto.UpdateEmailRequestDTO;
import br.com.links_vault_back_springboot.module.user.dto.UpdateUsernameRequestDTO;
import br.com.links_vault_back_springboot.module.user.useCase.CreateUserUseCase;
import br.com.links_vault_back_springboot.module.user.useCase.UpdateEmailUseCase;
import br.com.links_vault_back_springboot.module.user.useCase.UpdateUsernameUseCase;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final UpdateEmailUseCase updateEmailUseCase;
    private final UpdateUsernameUseCase updateUsernameUseCase;

    @PostMapping
    public ResponseEntity<ApiResponseDTO> createUser(
            @Valid @RequestBody CreateUserRequestDTO createUserRequestDTO
    ) {
        var result = this.createUserUseCase.execute(createUserRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponseDTO
                        .builder()
                        .message("Usuário cadastrado com sucesso!")
                        .data(result)
                        .build()
        );
    }

    @PatchMapping("/update-email")
    public ResponseEntity<ApiResponseDTO> updateEmail(
            HttpServletRequest httpServletRequest,
            @Valid @RequestBody UpdateEmailRequestDTO updateEmailRequestDTO
    ) {
        var result = this.updateEmailUseCase.execute(
                this.extractUserIdFromRequest(httpServletRequest),
                updateEmailRequestDTO
        );
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponseDTO
                                .builder()
                                .message("E-mail alterado com sucesso!")
                                .data(result)
                                .build()
                );
    }

    @PatchMapping("/update-username")
    public ResponseEntity<ApiResponseDTO> updateUsername(
            HttpServletRequest httpServletRequest,
            @Valid @RequestBody UpdateUsernameRequestDTO updateUsernameRequestDTO
    ) {
        var result = this.updateUsernameUseCase.execute(
                this.extractUserIdFromRequest(httpServletRequest),
                updateUsernameRequestDTO
        );
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        ApiResponseDTO
                                .builder()
                                .message("Nome de usuário alterado com sucesso!")
                                .data(result)
                                .build()
                );
    }

    private UUID extractUserIdFromRequest(
            HttpServletRequest httpServletRequest
    ) {
        var userId = httpServletRequest.getAttribute("user_id");
        return UUID.fromString(userId.toString());
    }

}
