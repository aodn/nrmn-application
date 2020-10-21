package au.org.aodn.nrmn.restapi.service;

import au.org.aodn.nrmn.restapi.service.model.IO;
import au.org.aodn.nrmn.restapi.util.loan.InputStreamLender;
import cyclops.control.Try;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.annotation.Resource;

@Service
@Log4j2
public class S3IOImpl implements S3IO {


    @Value("${app.s3.bucket}")
    private String bucket;

    @Autowired
    S3ClientProvider provider;

    @Override
    public Try<String, Exception> write(String path, MultipartFile file) {
        return Try.withCatch(() -> {

            val requestBody = Try.withResources(() -> file.getInputStream(), (in) -> {
                val res = RequestBody.fromBytes(IOUtils.toByteArray(in));
                in.close();
                return res;
            }).onFail(e ->
                    log.error(String.format("Error while reading the file:" + e.getMessage()))
            ).toOptional();

            val client = provider.getClient();
            val response = client.
                    putObject(PutObjectRequest.builder().bucket(bucket).key(path)
                            .build(), requestBody.get());
            log.info(String.format("object put successfully to s3: bucket: %s key: %s", bucket, path) + response.toString());
            return response.toString();
        }, Exception.class).onFail((e) ->
                log.error(String.format("Error while uploading to S3:" + e.getMessage()))
        );
    }
}
