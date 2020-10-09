package au.org.aodn.nrmn.restapi.controller.utils;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.net.URI;

public class      RequestWrapper<E, R> {

    HttpHeaders headers = new HttpHeaders();
    E entity;
    HttpMethod method;
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

    public RequestWrapper<E, R> withContentType(MediaType mediaType) {
        this.headers.setContentType(mediaType);
        return this;
    }
    public RequestWrapper<E, R> withToken(String token) {
        headers.set("Authorization", "Bearer " + token);
        return this;
    }

    public RequestWrapper<E, R>  withMethod( HttpMethod method) {
        this.method = method;
        return this;
    }

    public RequestWrapper<E, R> withUri(String path) throws Exception {
        url = new URI(path);
        return this;
    }

    public ResponseEntity<R> build(TestRestTemplate testRestTemplate) throws Exception{
       return testRestTemplate.exchange(
                url,
               method,
                new HttpEntity<E>(entity, headers),
               responseClass);
    }

}
