package cch.com.example.skaffold.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cch.com.example.skaffold.entity.Tutorial;
import cch.com.example.skaffold.repository.TutorialRepository;
import cch.com.example.skaffold.service.TutorialService;

@Service
public class TutorialServiceImpl implements TutorialService {
    
    private static Logger logger = Logger.getLogger(TutorialServiceImpl.class.getName());

    @Autowired
    private TutorialRepository tutorialRepository;

    @Override
    public List<Tutorial> getAllTutorials(String title) {
        // TODO Auto-generated method stub
        if (Objects.isNull(title)) {
            return tutorialRepository.findAll();
        }
        return tutorialRepository.findByTitleContaining(title);
    }

    @Override
    public Tutorial getTutorialById(UUID id) {
        // TODO Auto-generated method stub
        var tutorialData = tutorialRepository.findById(id);
        if (tutorialData.isPresent()) {
            return tutorialData.get();
        }
        return null;
    }

    @Override
    public Tutorial createTutorial(Tutorial tutorial) {
        // TODO Auto-generated method stub
        return tutorialRepository.save(tutorial);

    }

    @Override
    public Tutorial updateTutorial(UUID id, Tutorial tutorial) {
        // TODO Auto-generated method stub
        var findTutorial = tutorialRepository.findById(id);
        if (findTutorial.isPresent()) {
            logger.info("found tutorial");
            var _tutorial = findTutorial.get();
            Optional.ofNullable(tutorial.getTitle()).ifPresent(_tutorial::setTitle);
            Optional.ofNullable(tutorial.getDescription()).ifPresent(_tutorial::setDescription);
            Optional.ofNullable(tutorial.getPublished()).ifPresent(_tutorial::setPublished);
            return tutorialRepository.save(_tutorial);
        }
        return null;
    }

    @Override
    public void deleteTutorial(UUID id) {
        // TODO Auto-generated method stub
        tutorialRepository.deleteById(id);
        
    }

    @Override
    public void deleteAllTutorials() {
        // TODO Auto-generated method stub
        tutorialRepository.deleteAll();
    }

    @Override
    public List<Tutorial> findByPublished() {
        // TODO Auto-generated method stub
        return tutorialRepository.findByPublished(true);
    }
    
}
