package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.model.db.Method.MethodBuilder;
import au.org.aodn.nrmn.restapi.repository.MethodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MethodTestData {

    @Autowired
    private MethodRepository methodRepository;

    private int method = 0;

    public Method persistedMethod() {
        Method method = defaultBuilder().build();
        methodRepository.saveAndFlush(method);
        return method;
    }

    public MethodBuilder defaultBuilder() {
        return Method.builder()
                     .methodId(0)
                     .methodName("Method " + ++method)
                     .isActive(true);
    }
}
