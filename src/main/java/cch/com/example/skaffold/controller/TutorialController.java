package cch.com.example.skaffold.controller;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import cch.com.example.skaffold.entity.Tutorial;
import cch.com.example.skaffold.service.TutorialService;

@RestController
@RequestMapping("/api")
public class TutorialController {

    private static Logger logger = Logger.getLogger(TutorialController.class.getName());

    @Autowired
    private TutorialService tutorialService;

    @GetMapping("/tutorials")
    public ResponseEntity<List<Tutorial>> getAllTutorials(@RequestParam(required = false) String title) {
        try {
            List<Tutorial> tutorials = tutorialService.getAllTutorials(title);
            if (tutorials.isEmpty()) {
                logger.warning(String.format("Not Search from %s.", title));
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            logger.info(String.format("Get All Tutorials by %s.", title));
            return new ResponseEntity<>(tutorials, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/tutorials/{id}")
    public ResponseEntity<Tutorial> getTutorialById(@PathVariable("id") String id) {
        Tutorial tutorial = tutorialService.getTutorialById(UUID.fromString(id));
        if (Objects.nonNull(tutorial)) {
            logger.info(String.format("Get Tutorial. Id : %s.", tutorial.getId().toString()));
            return new ResponseEntity<>(tutorial, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/tutorials")
    public ResponseEntity<Void> createTutorial(@RequestBody Tutorial tutorial) {
        try {
            Tutorial _tutorial =  tutorialService.createTutorial(tutorial);
            URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(_tutorial.getId())
                .toUri();
                logger.info("Created tutorial");
            return ResponseEntity.created(location).build();
        } catch (Exception e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, "Create tutorial faild.");
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/tutorials/{id}")
    public ResponseEntity<Tutorial> updateTutorial(@PathVariable("id") String id, @RequestBody Tutorial tutorial) {
        Tutorial _tutorial = tutorialService.updateTutorial(UUID.fromString(id), tutorial);
        if (Objects.nonNull(_tutorial)) {
            logger.info(String.format("Updated By %s.", id));
            return new ResponseEntity<>(_tutorial, HttpStatus.OK);
        } else {
            logger.warning("Not found.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/tutorials/{id}")
    public ResponseEntity<HttpStatus> deleteTutorial(@PathVariable("id") String id) {
        try {
            tutorialService.deleteTutorial(UUID.fromString(id));
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Delete failed.");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/tutorials")
    public ResponseEntity<HttpStatus> deleteAllTutorials() {
        try {
            tutorialService.deleteAllTutorials();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Delete failed.");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/tutorials/published")
    public ResponseEntity<List<Tutorial>> findByPublished() {
        try {
            List<Tutorial> tutorials = tutorialService.findByPublished();
            if (tutorials.isEmpty()) {
                logger.warning(String.format("Not Search By Published"));
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            logger.info(String.format("find By Published."));
            return new ResponseEntity<>(tutorials, HttpStatus.OK);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Search failed.");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
