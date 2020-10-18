package au.org.aodn.nrmn.restapi.service;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;

@Service
public class S3ClientProviderImpl implements S3ClientProvider {
   @Override
    public S3Client getClient() {
        return S3Client.create();
    }
}
