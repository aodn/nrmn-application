package au.org.aodn.nrmn.restapi.service;

import cyclops.control.Maybe;
import cyclops.control.Try;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import au.org.aodn.nrmn.restapi.service.model.IO;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;

@Service
@Setter
@Log4j2
public class S3IO implements IO {

    private S3Client client;

    @Value("${app.s3.bucket}")
    String bucket;

    @Override
    public Try<String, Exception> write(String path, MultipartFile file) {
        return Try.withCatch(() -> {
            val contentBytes = IOUtils.toByteArray(file.getInputStream());
            val response = client.
                    putObject(PutObjectRequest.builder().bucket(bucket).key(path)
                            .build(), RequestBody.fromBytes(contentBytes));
            log.info(String.format("object put successfully to s3: bucket: %s key: %s", bucket, path) + response.toString());
            return response.toString();
        }, Exception.class).onFail((e) ->
                log.error(String.format("Error while uploading to S3:" + e.getMessage()))
        );
    }
}
