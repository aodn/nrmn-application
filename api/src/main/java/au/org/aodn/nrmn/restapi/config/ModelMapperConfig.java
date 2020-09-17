package au.org.aodn.nrmn.restapi.config;

import au.org.aodn.nrmn.restapi.dto.user.get.UserGetSimpleDto;
import au.org.aodn.nrmn.restapi.model.db.SecUserEntity;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public static ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // SecUserEntity to UserDto conversion
        TypeMap<SecUserEntity, UserGetSimpleDto> typeSUEToUserDto = modelMapper.createTypeMap(SecUserEntity.class, UserGetSimpleDto.class);
        typeSUEToUserDto.addMappings(mapper -> {
            mapper.map(src -> src.getRolesAsStringSet(),
                    UserGetSimpleDto::setRoles);
        });


        return modelMapper;
    }
}
