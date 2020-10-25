package au.org.aodn.nrmn.restapi.service;

import cyclops.control.Try;

import au.org.aodn.nrmn.restapi.service.model.IO;
import org.springframework.web.multipart.MultipartFile;



public interface  S3IO extends IO<String> {

    Try<String, Exception> write(String path, MultipartFile file);
}
