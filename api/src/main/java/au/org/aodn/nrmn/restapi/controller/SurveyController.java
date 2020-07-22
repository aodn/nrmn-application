package au.org.aodn.nrmn.restapi.controller;

import au.org.aodn.nrmn.restapi.model.SurveyEntity;
import au.org.aodn.nrmn.restapi.repository.SurveyEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api")
public class SurveyController {

    @Autowired
    SurveyEntityRepository surveyRepo;

    @GetMapping(path="/survey", produces = "application/json")
    public List<SurveyEntity> getEmployees() {
        return surveyRepo.findAll();
    }
}
