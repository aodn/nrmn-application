package au.org.aodn.nrmn.restapi.service;

import au.org.aodn.nrmn.restapi.controller.exception.ResourceNotFoundException;
import au.org.aodn.nrmn.restapi.controller.validation.ValidationError;
import au.org.aodn.nrmn.restapi.controller.validation.ValidationErrors;
import au.org.aodn.nrmn.restapi.dto.survey.SurveyDto;
import au.org.aodn.nrmn.restapi.model.db.Diver;
import au.org.aodn.nrmn.restapi.model.db.Site;
import au.org.aodn.nrmn.restapi.model.db.Survey;
import au.org.aodn.nrmn.restapi.model.db.enums.Directions;
import au.org.aodn.nrmn.restapi.repository.DiverRepository;
import au.org.aodn.nrmn.restapi.repository.SiteRepository;
import au.org.aodn.nrmn.restapi.repository.SurveyRepository;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static au.org.aodn.nrmn.restapi.util.TimeUtils.parseDate;
import static au.org.aodn.nrmn.restapi.util.TimeUtils.parseTime;

@Service
public class SurveyEditService {

    @Autowired
    SurveyRepository surveyRepository;

    @Autowired
    SiteRepository siteRepository;

    @Autowired
    DiverRepository diverRepository;

    @Autowired
    ModelMapper mapper;

    public Survey updateSurvey(SurveyDto surveyDto) {

        Survey survey = surveyRepository.findById(surveyDto.getSurveyId()).orElseThrow(ResourceNotFoundException::new);

        mapper.map(surveyDto, survey);

        Site site = siteRepository.findBySiteCode(surveyDto.getSiteCode());
        survey.setSite(site);

        Diver pqDiver = diverRepository.findByCriteria(surveyDto.getPqDiverInitials()).get(0);
        survey.setPqDiverId(pqDiver.getDiverId());

        return survey;
    }

    public ValidationErrors validateSurvey(SurveyDto surveyDto) {
        List<ValidationError> errors = new ArrayList<>();
        Site surveyDtoSite = siteRepository.findBySiteCode(surveyDto.getSiteCode());
        Date surveyDate = null;

        // Date validations
        try {
            surveyDate = parseDate(surveyDto.getSurveyDate());
        } catch (DateTimeException e) {
            errors.add(new ValidationError("Survey", "surveyDate", surveyDto.getSurveyDate(),
                    e.getMessage()));
        }

        if(surveyDate != null) {
            if (surveyDate.after(new Date())) {
                errors.add(new ValidationError("Survey", "surveyDate", surveyDto.getSurveyDate(),
                        "A survey date cannot be in the future."));
            }

            if (surveyDate.before(Date.from(LocalDate.of(2006, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()))) {
                errors.add(new ValidationError("Survey", "surveyDate", surveyDto.getSurveyDate(),
                        "A survey date cannot be before January 1st, 2006."));
            }
        }

        // Time validations
        try {
            parseTime(surveyDto.getSurveyTime());
        } catch (DateTimeException e) {
            errors.add(new ValidationError("Survey", "surveyTime", surveyDto.getSurveyTime(),
                    "The survey time must be in the format hh:mm[:ss]"));
        }

        // Site code and site name exist and match
        if (!surveyDtoSite.getSiteName().equals(surveyDto.getSiteName())) {
            errors.add(new ValidationError("Site", "siteCode", surveyDto.getSiteName(),
                    String.format("The site code %s does not match the site name \"%s\"", surveyDto.getSiteCode(), surveyDto.getSiteName())));
        }

        // Lat and Lon match site
        String lat = surveyDto.getLatitude();
        if(!StringUtils.isEmpty(lat) && !truncateNumber(surveyDtoSite.getLatitude()).equals(truncateNumber(Double.parseDouble(lat)))){
            errors.add(new ValidationError("Survey", "latitude", surveyDto.getLatitude(),
                    String.format("The survey latitude does not match the latitude of the site (%s)", surveyDtoSite.getLatitude())));
        }

        String lon = surveyDto.getLongitude();
        if(!StringUtils.isEmpty(lon) && !truncateNumber(surveyDtoSite.getLongitude()).equals(truncateNumber(Double.parseDouble(lon)))){
            errors.add(new ValidationError("Survey", "longitude", surveyDto.getLongitude(),
                    String.format("The survey longitude does not match the longitude of the site (%s)", surveyDtoSite.getLongitude())));
        }

        // Direction Validation
        if (!EnumUtils.isValidEnum(Directions.class, surveyDto.getDirection())) {
            errors.add(new ValidationError("Survey", "direction", surveyDto.getDirection(),
                    surveyDto.getDirection() + " is invalid, expected: N,NE,E,SE,S,SW,W,NW"));
        }

        // Ensure site/date/depth.surveyNum is unique
        Integer depth = surveyDto.getDepth() != null && surveyDto.getDepth().length() > 0 ? Integer.valueOf(surveyDto.getDepth()) : null;
        List<Survey> duplicateSurveys = surveyRepository.findBySiteDepthSurveyNumDate(
                surveyDtoSite, depth, surveyDto.getSurveyNum(), surveyDate).stream()
                .filter(s -> !s.getSurveyId().equals(surveyDto.getSurveyId()))
                .collect(Collectors.toList());

        if (duplicateSurveys.size() > 0) {
            errors.add(new ValidationError("Survey", "surveyNum", surveyDto.getSurveyNum().toString(),
                    String.format("A survey with the site, date and depth of \"%s/%s/%s.%s\" already exists.",
                            surveyDto.getSiteCode(), surveyDto.getSurveyDate(), surveyDto.getDepth(), surveyDto.getSurveyNum())));
        }

        return new ValidationErrors(errors);
    }

    private String truncateNumber(Double number) {

        if(number == null) {
            return null;
        }

        DecimalFormat df = new DecimalFormat("#.####");
        df.setRoundingMode(RoundingMode.HALF_UP);
        return String.valueOf(Double.parseDouble(df.format(number)));
    }
}
