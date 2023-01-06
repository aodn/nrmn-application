package au.org.aodn.nrmn.restapi.service.upload;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3IO {

    @Value("${app.s3.bucket}")
    private String bucket;

    @Value("${app.s3.bucket-shared}")
    private String bucketShared;

    S3Client client;

    public S3Client getClient() {
        if (client == null)
            client = S3Client.create();
        return client;
    }

    public void uploadEndpoint(String viewName, File file) {
        try {
            var fullPath = String.join("/", List.of("endpoints", viewName + ".csv"));
            var res = RequestBody.fromFile(file);
            getClient().putObject(PutObjectRequest.builder().bucket(bucket).key(fullPath).build(), res);
        } catch (AwsServiceException e) {
            throw new RuntimeException("Failed to write to S3: " + e.getMessage());
        }
    }

    public String createS3Link(String viewName, LocalDateTime expires) throws Exception {
        try {
            var sessionId = RandomStringUtils.random(20, 0, 0, true, true, null, new SecureRandom());
            var sourceKey = String.join("/", List.of("endpoints", viewName + ".csv"));
            var destinationKey = String.join("/", List.of("endpoints", sessionId, viewName + ".csv"));
            var request = CopyObjectRequest.builder()
                    .sourceBucket(bucket).sourceKey(sourceKey)
                    .destinationBucket(bucketShared).destinationKey(destinationKey)
                    .acl(ObjectCannedACL.PUBLIC_READ)
                    .expires(expires.toInstant(OffsetDateTime.now().getOffset()))
                    .build();
                    getClient().copyObject(request);
            return getClient().utilities().getUrl(builder -> builder.bucket(bucketShared).key(destinationKey))
                    .toExternalForm();
        } catch (Exception e) {
            throw new Exception("Failed to generate shared link in bucket "+ bucketShared + " : " + e.getMessage());
        }
    }

    public void deleteS3Link(String externalForm) throws Exception {
        try {
            var key = externalForm.split("amazonaws.com/")[1];
            getClient().deleteObject(DeleteObjectRequest.builder().bucket(bucketShared).key(key).build());
        } catch (Exception e) {
            throw new Exception("Failed to delete shared linkin bucket "+ bucketShared + " : " + e.getMessage());
        }
    }

    public String write(String path, MultipartFile file) throws Exception {
        try (var in = file.getInputStream()) {
            var buffer = new ByteArrayOutputStream();
            var nRead = 0;
            var data = new byte[4];
            while ((nRead = in.read(data, 0, data.length)) != -1)
                buffer.write(data, 0, nRead);
            buffer.flush();
            var res = RequestBody.fromBytes(buffer.toByteArray());
            var response = getClient()
                    .putObject(PutObjectRequest.builder().bucket(bucket).key(path).build(), res);
            return response.toString();
        } catch (AwsServiceException e) {
            throw new Exception("Failed to write to S3: " + e.getMessage());
        }
    }
}
