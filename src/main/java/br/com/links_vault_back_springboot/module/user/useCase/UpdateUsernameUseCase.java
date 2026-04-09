package br.com.links_vault_back_springboot.module.user.useCase;

import br.com.links_vault_back_springboot.module.user.dto.UpdateUsernameRequestDTO;
import br.com.links_vault_back_springboot.module.user.dto.UserResponseDTO;
import br.com.links_vault_back_springboot.module.user.entity.UserEntity;
import br.com.links_vault_back_springboot.module.user.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class UpdateUsernameUseCase {

    private final UserRepository userRepository;

    public UserResponseDTO execute(
            UUID userId,
            UpdateUsernameRequestDTO updateUsernameRequestDTO
    ) {
        try {
            var userEntityOptional = this.userRepository.findById(userId);
            UserEntity userToUpdate = new UserEntity();
            userEntityOptional.ifPresentOrElse(
                    user -> {
                        userToUpdate.setId(user.getId());
                        userToUpdate.setUsername(updateUsernameRequestDTO.getUsername());
                        userToUpdate.setEmail(user.getEmail());
                        userToUpdate.setPassword(user.getPassword());
                        userToUpdate.setCreatedAt(user.getCreatedAt());
                        this.userRepository.save(userToUpdate);
                    },
                    () -> {
                        throw new EntityNotFoundException();
                    }
            );
            return UserResponseDTO
                    .builder()
                    .username(userToUpdate.getUsername())
                    .email(userToUpdate.getEmail())
                    .createdAt(userToUpdate.getCreatedAt())
                    .build();

        } catch (EntityNotFoundException entityNotFoundException) {
            throw new EntityNotFoundException("Usuário não encontrado.");
        } catch (DataIntegrityViolationException dataIntegrityViolationException) {
            throw new EntityExistsException("Este nome de usuário já está em uso.");
        } catch (Exception e) {
            throw new RuntimeException("Ocorreu um erro durante a alteração do nome de usuário, tente novamente.");
        }
    }

}

