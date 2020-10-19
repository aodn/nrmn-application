package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.repository.MethodRepository;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MethodTestData {

    @Autowired
    private MethodRepository methodRepository;

    public Method persistedMethod() {
        val method = Method.builder()
            .methodName("Macrocystis count")
            .isActive(true)
            .build();
        methodRepository.saveAndFlush(method);
        return method;
    }
}
