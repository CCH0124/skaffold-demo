package cch.com.example.skaffold.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import cch.com.example.skaffold.entity.Tutorial;

public interface TutorialRepository extends JpaRepository<Tutorial, UUID> {
    List<Tutorial> findByPublished(boolean published);

    List<Tutorial> findByTitleContaining(String title);
}
