package au.org.aodn.nrmn.restapi.controller.mapping;

import au.org.aodn.nrmn.restapi.data.model.Diver;
import au.org.aodn.nrmn.restapi.data.model.Site;
import au.org.aodn.nrmn.restapi.data.model.Survey;
import au.org.aodn.nrmn.restapi.data.repository.DiverRepository;
import au.org.aodn.nrmn.restapi.data.repository.SurveyMethodRepository;
import au.org.aodn.nrmn.restapi.dto.survey.SurveyDto;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Configuration
public class SurveyDtoMapperConfig {

    @Autowired
    SurveyDtoMapperConfig(SurveyMethodRepository surveyMethodRepository, DiverRepository diverRepository,
            ModelMapper modelMapper) {
        Converter<Integer, String> toMethodString = ctx -> String.join(", ",
                surveyMethodRepository.findSurveyMethodsForSurveyId(ctx.getSource()));
        Converter<Integer, String> toBlockString = ctx -> String.join("\n",
                surveyMethodRepository.findBlocksForSurveyId(ctx.getSource()));
        Converter<Integer, String> toSurveyNotDoneString = ctx -> {
            List<String> surveysNotDone = surveyMethodRepository.findSurveyNotDoneForSurveyId(ctx.getSource());
            if (surveysNotDone.isEmpty()) {
                return "All Surveys Completed";
            } else {
                return String.join("\n", surveysNotDone);
            }
        };
        Converter<Integer, String> toDiverName = ctx -> {
            Optional<Integer> diverId = Optional.ofNullable(ctx.getSource());
            Optional<Diver> diver = diverId.isPresent() ? diverRepository.findById(diverId.get()) : Optional.empty();
            return diver.isPresent() ? diver.get().getFullName() : "";
        };
        Converter<Integer, String> toDiverInitial = ctx -> {
            Optional<Integer> diverId = Optional.ofNullable(ctx.getSource());
            Optional<Diver> diver = diverId.isPresent() ? diverRepository.findById(diverId.get()) : Optional.empty();
            return diver.isPresent() ? diver.get().getInitials() : "";
        };
        Converter<Survey, String> toDecimalDepthString = ctx -> String.format("%d.%d", ctx.getSource().getDepth(),
                ctx.getSource().getSurveyNum());
        Converter<Integer, String> toDiverList = ctx -> String.join("\n",
                surveyMethodRepository.findDiversForSurvey(ctx.getSource()));
        Converter<String, String> passThrough = ctx -> Optional.ofNullable(ctx.getSource()).orElse("");

        Converter<Site, String> toSiteLatitude = ctx -> {
            return Objects.nonNull(ctx.getSource().getLatitude()) ? Double.parseDouble(String.format("%.5f", ctx.getSource().getLatitude())) + "" : "";
        };

        Converter<Site, String> toSiteLongitude = ctx -> {
            return Objects.nonNull(ctx.getSource().getLongitude()) ? Double.parseDouble(String.format("%.5f", ctx.getSource().getLongitude())) + "" : "";
        };

        modelMapper.typeMap(Survey.class, SurveyDto.class).addMappings(mapper -> {
            mapper.using(toSiteLatitude).map(Survey::getSite, SurveyDto::setSiteLatitude);
            mapper.using(toSiteLongitude).map(Survey::getSite, SurveyDto::setSiteLongitude);
            mapper.using(toDiverName).map(Survey::getPqDiverId, SurveyDto::setPqDiver);
            mapper.using(toDiverInitial).map(Survey::getPqDiverId, SurveyDto::setPqDiverInitials);
            mapper.using(toBlockString).map(Survey::getSurveyId, SurveyDto::setBlock);
            mapper.using(toMethodString).map(Survey::getSurveyId, SurveyDto::setMethod);
            mapper.using(toSurveyNotDoneString).map(Survey::getSurveyId, SurveyDto::setSurveyNotDone);
            mapper.using(toDecimalDepthString).map(survey -> survey, SurveyDto::setDecimalDepth);
            mapper.using(passThrough).map(survey -> survey.getSite().getMpa(), SurveyDto::setArea);
            mapper.using(passThrough).map(survey -> survey.getSite().getLocation().getLocationName(),
                    SurveyDto::setLocationName);
            mapper.using(passThrough).map(survey -> survey.getSite().getCountry(), SurveyDto::setCountry);
            mapper.using(passThrough).map(
                    survey -> survey.getInsideMarinePark() == null ? "Unsure" : survey.getInsideMarinePark(),
                    SurveyDto::setInsideMarinePark);
            mapper.using(toDiverList).map(Survey::getSurveyId, SurveyDto::setDivers);
        });
    }
}
