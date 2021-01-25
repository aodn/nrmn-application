package au.org.aodn.nrmn.restapi.controller;

import au.org.aodn.nrmn.restapi.model.db.StagedJob;
import au.org.aodn.nrmn.restapi.model.db.Survey;
import au.org.aodn.nrmn.restapi.model.db.audit.UserActionAudit;
import au.org.aodn.nrmn.restapi.model.db.enums.SourceJobType;
import au.org.aodn.nrmn.restapi.model.db.enums.StatusJobType;
import au.org.aodn.nrmn.restapi.repository.*;
import au.org.aodn.nrmn.restapi.service.CorrectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/correction")
@Tag(name = "corrections")
public class CorrectionController {
    @Autowired
    StagedRowRepository stagedRowRepository;

    @Autowired
    UserActionAuditRepository userActionAuditRepository;

    @Autowired
    private StagedJobRepository stagedJobRepository;

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private CorrectionService correctionService;

    @Autowired
    private SecUserRepository userRepo;

    @PostMapping("/correct")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    public ResponseEntity correctSurvey(
            @RequestParam(value = "surveyIdList") List<Integer> surveyIdList,
            Authentication authentication) {

        userActionAuditRepository.save(
                new UserActionAudit(
                        "correction/correct",
                        "correct survey attempt for username " + authentication.getName()
                                + " survey id list: " + surveyIdList)
        );


        val user = userRepo.findByEmail(authentication.getName());
        val stagedJob = stagedJobRepository.save(
                StagedJob.builder()
                        .source(SourceJobType.CORRECTION)
                        .status(StatusJobType.STAGED)
                        .creator(user.get())
                        .build());

        List<Survey> surveys = surveyRepository.findByIdsIn(surveyIdList);

        stagedRowRepository.saveAll(surveys.stream().flatMap(correctionService::convertSurveyToStagedRows)
                .peek(s -> s.setStagedJob(stagedJob)).collect(Collectors.toList()));

        stagedJob.setStatus(StatusJobType.PENDING);
        stagedJobRepository.save(stagedJob);

        return ResponseEntity.status(HttpStatus.OK).body(stagedJob.getId());
    }
}
