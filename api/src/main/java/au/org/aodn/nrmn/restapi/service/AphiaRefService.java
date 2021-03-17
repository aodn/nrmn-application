package au.org.aodn.nrmn.restapi.service;

import au.org.aodn.nrmn.restapi.model.db.AphiaRef;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AphiaRefService {
    List<AphiaRef> fuzzyNameSearch(String searchTerm);
}
