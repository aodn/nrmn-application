package au.org.aodn.nrmn.restapi.service.model;

import cyclops.control.Try;
import org.springframework.web.multipart.MultipartFile;

public interface IO<T> {
  Try<T, ? extends Exception> write(String path , MultipartFile file);
}
