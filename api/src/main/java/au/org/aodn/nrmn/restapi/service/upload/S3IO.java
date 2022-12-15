package au.org.aodn.nrmn.restapi.service.upload;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Service
public class S3IO {

    @Value("${app.s3.bucket}")
    private String bucket;

    S3Client client;

    public S3Client getClient() {
        if (client == null)
            client = S3Client.create();
        return client;
    }

    public void uploadMaterializedView(String path, File file) {
        try {
            var fullPath = "materialized_views/" + path + ".csv";
            var res = RequestBody.fromFile(file);
            getClient().putObject(PutObjectRequest.builder().bucket(bucket).key(fullPath).build(), res);
        } catch (AwsServiceException e) {
            throw new RuntimeException("Failed to write to S3: " + e.getMessage());
        }
    }

    public String generatedSignedS3URL(String path) {
        var presigner = S3Presigner.create();
        var getObjectRequest = GetObjectRequest.builder().bucket(bucket).key("materialized_views/" + path + ".csv")
                .build();
        var getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(getObjectRequest)
                .build();
        var presignedGetObjectRequest = presigner.presignGetObject(getObjectPresignRequest);
        return presignedGetObjectRequest.url().toString();
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
