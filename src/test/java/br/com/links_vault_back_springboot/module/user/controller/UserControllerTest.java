package br.com.links_vault_back_springboot.module.user.controller;

import br.com.links_vault_back_springboot.module.user.dto.CreateUserRequestDTO;
import br.com.links_vault_back_springboot.module.user.dto.CreateUserResponseDTO;
import br.com.links_vault_back_springboot.module.user.dto.UpdateEmailRequestDTO;
import br.com.links_vault_back_springboot.module.user.dto.UserResponseDTO;
import br.com.links_vault_back_springboot.module.user.useCase.CreateUserUseCase;
import br.com.links_vault_back_springboot.module.user.useCase.UpdateEmailUseCase;
import br.com.links_vault_back_springboot.service.JWTService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreateUserUseCase createUserUseCase;

    @MockBean
    private UpdateEmailUseCase updateEmailUseCase;

    @MockBean
    private JWTService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName(value = "Deve retornar o status 201 ao cadastrar um usuário")
    void shouldCreateUserSuccessfully() throws Exception {

        CreateUserRequestDTO request = CreateUserRequestDTO.builder()
                .username("user")
                .email("user@email.com")
                .password("password123")
                .build();

        CreateUserResponseDTO response = CreateUserResponseDTO.builder()
                .accessToken("jwt-token")
                .build();

        when(createUserUseCase.execute(any())).thenReturn(response);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Usuário cadastrado com sucesso!"))
                .andExpect(jsonPath("$.data.accessToken").value("jwt-token"));
    }

    @Test
    @DisplayName(value = "Deve retornar o status 400 em caso de requisição inválida")
    void shouldReturnBadRequestWhenRequestIsInvalid() throws Exception {

        CreateUserRequestDTO request = CreateUserRequestDTO.builder()
                .username("")
                .email("")
                .password("")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName(value = "Deve retornar o status 200 ao alterar o e-mail")
    void shouldUpdateEmailSuccessfully() throws Exception {

        UUID userId = UUID.randomUUID();

        UpdateEmailRequestDTO request = new UpdateEmailRequestDTO();
        request.setEmail("novo@email.com");

        UserResponseDTO response = UserResponseDTO.builder()
                .username("leandro")
                .email("novo@email.com")
                .createdAt(LocalDateTime.now())
                .build();

        when(updateEmailUseCase.execute(eq(userId), any()))
                .thenReturn(response);

        mockMvc.perform(patch("/users/update-email")
                        .requestAttr("user_id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("E-mail alterado com sucesso!"))
                .andExpect(jsonPath("$.data.email")
                        .value("novo@email.com"))
                .andExpect(jsonPath("$.data.username")
                        .value("leandro"));
    }

    @Test
    @DisplayName(value = "Deve retornar o status 400 caso o e-mail não seja informado")
    void shouldReturnBadRequestWhenUpdateEmailRequestIsInvalid() throws Exception {

        UpdateEmailRequestDTO request = new UpdateEmailRequestDTO();
        request.setEmail("");

        mockMvc.perform(patch("/users/update-email")
                        .requestAttr("user_id", UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

}
