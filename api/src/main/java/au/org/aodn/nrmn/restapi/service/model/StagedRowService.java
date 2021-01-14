package au.org.aodn.nrmn.restapi.service.model;

import au.org.aodn.nrmn.restapi.dto.payload.ErrorInput;
import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.repository.StagedRowRepository;
import cyclops.control.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StagedRowService {

    @Autowired
    StagedRowRepository rowRepo;

    public Validated<ErrorInput, StagedRow> update(Long id, StagedRow provider) {
       return  rowRepo.findById(id).map(found -> {
            found.setDiver(provider.getDiver());
            found.setBuddy(provider.getBuddy());
            found.setSiteCode(provider.getSiteCode());
            found.setSiteName(provider.getSiteName());
            found.setDate(provider.getDate());
            found.setTime(provider.getTime());
            found.setDepth(provider.getDepth());
            found.setMethod(provider.getMethod());
            found.setBlock(provider.getBlock());
            found.setSpecies(provider.getSpecies());
            found.setLongitude(provider.getLongitude());
            found.setLatitude(provider.getLatitude());
            found.setVis(provider.getVis());
            found.setDirection(provider.getDirection());
            found.setCode(provider.getCode());
            found.setCommonName(provider.getCommonName());
            found.setTotal(provider.getTotal());
            found.setL5(provider.getL5());
            found.setL95(provider.getL95());
            found.setLMax(provider.getLMax());
            found.setInverts(provider.getInverts());
            found.setIsInvertSizing(provider.getIsInvertSizing());
            found.setM2InvertSizingSpecies(provider.getM2InvertSizingSpecies());
            found.setTotal(provider.getTotal());
            found.setMeasureJson(provider.getMeasureJson());
            rowRepo.save(found);
            return Validated.<ErrorInput, StagedRow>valid(found);
        }).orElseGet(() -> Validated.invalid(new ErrorInput("Couldn't find row " + id, "id")));
    }

}
