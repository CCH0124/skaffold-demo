package cch.com.example.skaffold.service;

import java.util.List;
import java.util.UUID;

import cch.com.example.skaffold.entity.Tutorial;

public interface TutorialService {
    List<Tutorial> getAllTutorials(String title);
    Tutorial getTutorialById(UUID id);
    Tutorial createTutorial(Tutorial tutorial);
    Tutorial updateTutorial(UUID id, Tutorial tutorial);
    void deleteTutorial(UUID id);
    void deleteAllTutorials();
    List<Tutorial> findByPublished();
}
