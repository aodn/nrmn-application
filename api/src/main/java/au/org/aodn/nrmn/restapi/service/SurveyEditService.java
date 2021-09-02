package au.org.aodn.nrmn.restapi.service;

import static au.org.aodn.nrmn.restapi.util.SpacialUtil.getDistance;
import static au.org.aodn.nrmn.restapi.util.TimeUtils.parseDate;
import static au.org.aodn.nrmn.restapi.util.TimeUtils.parseTime;

import java.sql.Time;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import au.org.aodn.nrmn.restapi.repository.ProgramRepository;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

@Service
public class SurveyEditService {

    @Autowired
    SurveyRepository surveyRepository;

    @Autowired
    SiteRepository siteRepository;

    @Autowired
    DiverRepository diverRepository;

    @Autowired
    ProgramRepository programRepository;

    @Autowired
    ModelMapper mapper;

    public Survey updateSurvey(SurveyDto surveyDto) {

        Survey survey = surveyRepository.findById(surveyDto.getSurveyId()).orElseThrow(ResourceNotFoundException::new);

        survey.setVisibility(surveyDto.getVisibility() != null ? Double.valueOf(surveyDto.getVisibility()) : null);
        survey.setDirection(surveyDto.getDirection());
        survey.setLongitude(surveyDto.getLongitude() != null ? Double.valueOf(surveyDto.getLongitude()) : null);
        survey.setLatitude(surveyDto.getLatitude() != null ? Double.valueOf(surveyDto.getLatitude()) : null);

        Site site = siteRepository.findBySiteCode(surveyDto.getSiteCode());
        if(!site.getSiteName().equals(surveyDto.getSiteName())) {
            site.setSiteName(surveyDto.getSiteName());
            siteRepository.save(site);
        }
        survey.setSite(site);

        survey.setProgram(programRepository.findById(surveyDto.getProgramId()).orElseThrow(ResourceNotFoundException::new));
        survey.setBlockAbundanceSimulated(surveyDto.getBlockAbundanceSimulated());
        survey.setSurveyDate(parseDate(surveyDto.getSurveyDate()));
        survey.setSurveyTime(Time.valueOf(parseTime(surveyDto.getSurveyTime()).get()));
        survey.setDepth(Integer.valueOf(surveyDto.getDepth()));
        survey.setSurveyNum(surveyDto.getSurveyNum());

        Diver pqDiver = diverRepository.findByCriteria(surveyDto.getPqDiverInitials()).get(0);
        survey.setPqDiverId(pqDiver.getDiverId());

        survey.setProjectTitle(surveyDto.getProjectTitle());
        survey.setProtectionStatus(surveyDto.getProtectionStatus());
        survey.setInsideMarinePark(surveyDto.getInsideMarinePark());
        survey.setNotes(surveyDto.getNotes());

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

        // Visibility validation
        if (!StringUtils.isBlank(surveyDto.getVisibility())) {
            Double vis = NumberUtils.toDouble(surveyDto.getVisibility(), Double.NEGATIVE_INFINITY);
            if (vis < 0) {
                errors.add(new ValidationError("Survey", "visibility", surveyDto.getVisibility(), (vis == Double.NEGATIVE_INFINITY) ? "Vis is not a decimal" : "Vis is not positive"));
            } else {
                if(vis.toString().split("\\.")[1].length() > 1)
                    errors.add(new ValidationError("Survey", "visibility", surveyDto.getVisibility(), "Vis is more than one decimal place"));
            }
        }

        // Commented out as it is unclear how to validate the site name matching the site code as there is no unique
        // site name constraint nor a way to determine if it is a valid change.

//        // Site code and site name exist and match
//        if (!surveyDtoSite.getSiteName().equals(surveyDto.getSiteName())) {
//
//            List<Site> siteNameExistsAlready = siteRepository.findAll().stream()
//                    .filter(site -> site.getSiteName().equals(surveyDto.getSiteName())).collect(Collectors.toList());
//
//            if(siteNameExistsAlready.size() > 0) {
//            errors.add(new ValidationError("Site", "siteName", surveyDto.getSiteName(),
//                    String.format("The site name \"%s\" already exists for site code \"%s\"",
//                            surveyDto.getSiteName(), siteNameExistsAlready.get(0).getSiteCode())));
//            }
//
//        }

        // Lat and Lon match site
        Double lat = null;
        Double lon = null;
        try {
            lat = StringUtils.isEmpty(surveyDto.getLatitude()) ? null : Double.parseDouble(surveyDto.getLatitude());
        } catch (NumberFormatException e) {
            errors.add(new ValidationError("Survey", "latitude", surveyDto.getLatitude(),
                    "Latitude must contain a valid number"));
        }

        try {
            lon = StringUtils.isEmpty(surveyDto.getLongitude()) ? null : Double.parseDouble(surveyDto.getLongitude());
        } catch (NumberFormatException e) {
            errors.add(new ValidationError("Survey", "longitude", surveyDto.getLongitude(),
                    "Longitude must contain a valid number"));
        }

        boolean hasValidCoords = lat != null && lon != null && !lat.isNaN() && !lon.isNaN();
        double dist = hasValidCoords ? getDistance(lat, lon, surveyDtoSite.getLatitude(), surveyDtoSite.getLongitude()) : 0;
        if(dist > 0.2){
            errors.add(new ValidationError("Survey", "latitude", surveyDto.getLatitude(),
                    String.format("Coordinates are further than 0.2km from the Site (%.2fkm)", dist)));
            errors.add(new ValidationError("Survey", "longitude", surveyDto.getLongitude(),
                    String.format("Coordinates are further than 0.2km from the Site (%.2fkm)", dist)));
        }

        if(lat != null && !lat.isNaN() && (lat < -90 || lat > 90) ) {
            errors.add(new ValidationError("Survey", "latitude", surveyDto.getLatitude(),
                    "The latitude must be a valid number between -90 and 90"));
        }

        if(lon != null && !lon.isNaN() && (lon < -180 || lon > 180) ) {
            errors.add(new ValidationError("Survey", "longitude", surveyDto.getLongitude(),
                    "The longitude must be a valid number between -180 and 180"));
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
}
