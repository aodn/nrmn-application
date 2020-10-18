package au.org.aodn.nrmn.restapi.service;

import cyclops.control.Try;

import au.org.aodn.nrmn.restapi.service.model.IO;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;



public interface  S3IO extends IO {

    Try<String, Exception> write(String path, MultipartFile file);
}
