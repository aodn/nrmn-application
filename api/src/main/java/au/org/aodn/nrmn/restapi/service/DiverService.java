package au.org.aodn.nrmn.restapi.service;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import au.org.aodn.nrmn.restapi.data.repository.DiverRepository;

@Service
public class DiverService {

    @Autowired
    DiverRepository diverRepository;

    public String deleteDiver(Integer id) {
        try {
            var diverOptional = diverRepository.findById(id);

            if (!diverOptional.isPresent())
                return "Diver does not exist";

            var diver = diverOptional.get();

            if (Objects.nonNull(diver.getCreated()) && diver.getCreated().isAfter(LocalDateTime.now().minusHours(24))) {
                diverRepository.delete(diver);
            } else {
                return "Diver was created more than 24 hours ago and cannot be deleted";
            }
        } catch (Exception e) {
            return "Diver is associated with a survey and cannot be deleted";
        }

        return null;
    }
}
