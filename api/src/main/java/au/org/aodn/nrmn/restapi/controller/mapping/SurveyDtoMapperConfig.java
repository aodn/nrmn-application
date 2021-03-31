package au.org.aodn.nrmn.restapi.controller.mapping;

import au.org.aodn.nrmn.restapi.dto.survey.SurveyDto;
import au.org.aodn.nrmn.restapi.model.db.Survey;
import au.org.aodn.nrmn.restapi.repository.DiverRepository;
import au.org.aodn.nrmn.restapi.repository.SurveyMethodRepository;
import lombok.val;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SurveyDtoMapperConfig {

    @Autowired
    SurveyDtoMapperConfig(SurveyMethodRepository surveyMethodRepository, DiverRepository diverRepository, ModelMapper modelMapper) {
        Converter<Integer, String> toMethodString = ctx -> String.join(", ", surveyMethodRepository.findSurveyMethodsForSurveyId(ctx.getSource()));
        Converter<Integer, String> toBlockString = ctx -> String.join(", ", surveyMethodRepository.findBlocksForSurveyId(ctx.getSource()));
        Converter<Integer, String> toSurveyNotDoneString = ctx -> String.join(", ", surveyMethodRepository.findSurveyNotDoneForSurveyId(ctx.getSource()));
        Converter<Integer, String> toDiverName = ctx -> {
            val diver = diverRepository.findById(ctx.getSource()); 
            return diver.isPresent() ? diver.get().getFullName() : "";
        };
        modelMapper.typeMap(Survey.class, SurveyDto.class).addMappings(mapper -> {
            mapper.using(toDiverName).map(Survey::getPqDiverId, SurveyDto::setPqDiver);
            mapper.using(toBlockString).map(Survey::getSurveyId, SurveyDto::setBlock);
            mapper.using(toMethodString).map(Survey::getSurveyId, SurveyDto::setMethod);
            mapper.using(toSurveyNotDoneString).map(Survey::getSurveyId, SurveyDto::setSurveyNotDone);
        });
    }
}
