package au.org.aodn.nrmn.restapi.controller.utils;

import java.net.URI;
import java.util.Map;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class RequestWrapper<E, R> {

    HttpHeaders headers = new HttpHeaders();
    E entity;
    HttpMethod method;
    String url;
    Map<String, ?> param;
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
        this.url = path;
        return this;
    }

    public RequestWrapper<E, R> withParams(Map<String, ?> param) {
        this.param = param;
        return this;
    }

    public ResponseEntity<R> build(TestRestTemplate testRestTemplate) throws Exception{
        // It is important that this value cannot be null, else the call success
        // but jackson fail to convert to the correct type
        assert responseClass != null;

        if(param == null) {
            return testRestTemplate.exchange(
                    new URI(url),
                    method,
                    new HttpEntity<E>(entity, headers),
                    responseClass);
        }
        else {
            return testRestTemplate.exchange(
                    url,
                    method,
                    new HttpEntity<E>(entity, headers),
                    responseClass,
                    param);
        }
    }

}
