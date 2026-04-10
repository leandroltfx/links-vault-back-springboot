package br.com.links_vault_back_springboot.module.folder.repository;

import br.com.links_vault_back_springboot.module.folder.entity.FolderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FolderRepository extends JpaRepository<FolderEntity, UUID> {

    Optional<FolderEntity> findByUserIdAndTitle(UUID userId, String title);

}
