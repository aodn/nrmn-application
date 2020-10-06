package au.org.aodn.nrmn.restapi.it.utils;

import au.org.aodn.nrmn.restapi.dto.payload.JwtAuthenticationResponse;
import lombok.val;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.net.URI;

public class RequestWrapper<E, R> {

    HttpHeaders headers = new HttpHeaders();
    E entity;
    HttpMethod methdod;
    URI url;
    Class<R> responseClass;

    public RequestWrapper<E, R> withAppJson() {
        headers.set("Content-type", "Application/json");
        return this;
    }
    public RequestWrapper<E, R> withResponseType(Class<R> responseType) {
        responseClass = responseType;
        return this;
    }

    public RequestWrapper<E, R>  withEntity(E entity) {
        this.entity = entity;
        return this;
    }
    public RequestWrapper<E, R> withToken(String token) {
        headers.set("Authorization", "Bearer " + token);
        return this;
    }

    public RequestWrapper<E, R>  withMethod( HttpMethod method) {
        this.methdod = method;
        return this;
    }

    public RequestWrapper<E, R> withUri(String path) throws Exception {
        url = new URI(path);
        return this;
    }

    public ResponseEntity<R> build(TestRestTemplate testRestTemplate) throws Exception{
       return testRestTemplate.exchange(
                url,
                methdod,
                new HttpEntity<>(entity, headers),
               responseClass);
    }

}
