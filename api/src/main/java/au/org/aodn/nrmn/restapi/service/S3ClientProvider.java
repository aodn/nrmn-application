package au.org.aodn.nrmn.restapi.service;

import software.amazon.awssdk.services.s3.S3Client;

public interface S3ClientProvider {
    S3Client getClient();
}
