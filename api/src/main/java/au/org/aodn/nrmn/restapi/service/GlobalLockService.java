package au.org.aodn.nrmn.restapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import au.org.aodn.nrmn.restapi.data.model.GlobalLock;
import au.org.aodn.nrmn.restapi.data.repository.GlobalLockRepository;

@Service
public class GlobalLockService {

    @Autowired 
    GlobalLockRepository globalLockRepository;

    public Boolean setLock() {
        try {
            var lock = globalLockRepository.findById(1L);
            if(lock.isPresent()) return false;
            globalLockRepository.save(new GlobalLock());    
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public void releaseLock() {
        globalLockRepository.deleteAll();
    }
    
}
