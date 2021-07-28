package au.org.aodn.nrmn.restapi.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@Service
public class S3IO {

    @Value("${app.s3.bucket}")
    private String bucket;

    S3Client client;

    public S3Client getClient() {
        if(client == null) client = S3Client.create();
        return client;
    }

    public String write(String path, MultipartFile file) throws Exception {
        try (InputStream in = file.getInputStream()) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[4];
            while ((nRead = in.read(data, 0, data.length)) != -1)
                buffer.write(data, 0, nRead);
            buffer.flush();
            RequestBody res = RequestBody.fromBytes(buffer.toByteArray());
            PutObjectResponse response = getClient().putObject(PutObjectRequest.builder().bucket(bucket).key(path).build(), res);
            return response.toString();
        } catch (AwsServiceException e) {
            throw new Exception("Failed to write to S3: " + e.getMessage());
        }
    }
}
