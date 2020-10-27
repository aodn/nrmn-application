package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.model.db.AphiaRef.AphiaRefBuilder;
import au.org.aodn.nrmn.restapi.repository.AphiaRefRepository;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Component
public class AphiaRefTestData {

    @Autowired
    AphiaRefRepository aphiaRefRepository;

    public AphiaRef persistedAphiaRef() {
        val aphiaRef = defaultBuilder().build();
        aphiaRefRepository.saveAndFlush(aphiaRef);
        return aphiaRef;
    }

    public AphiaRefBuilder defaultBuilder() {
        return AphiaRef.builder()
            .aphiaId(217950)
            .url("http://www.marinespecies.org/aphia.php?p=taxdetails&id=217950")
            .scientificName("Sargocentron spiniferum")
            .authority("(Forsskål, 1775)")
            .status("accepted")
            .unacceptReason(null)
            .taxonRankId(220)
            .rank("Species")
            .validAphiaId(217950)
            .validName("Sargocentron spiniferum")
            .validAuthority("(Forsskål, 1775)")
            .parentNameUsageId(125704)
            .rankKingdom("Animalia")
            .rankPhylum("Chordata")
            .rankClass("Actinopterygii")
            .rankOrder("Beryciformes")
            .rankFamily("Holocentridae")
            .rankGenus("Sargocentron")
            .citation("Froese, R. and D. Pauly. Editors. (2019). FishBase. Sargocentron spiniferum (Forsskål, 1775). " +
                "Accessed through: World Register of Marine Species at: http://www.marinespecies.org/aphia" +
                ".php?p=taxdetails&id=217950 on 2019-11-14")
            .lsid("urn:lsid:marinespecies.org:taxname:217950")
            .isMarine(true)
            .isBrackish(false)
            .isFreshwater(false)
            .isTerrestrial(false)
            .isExtinct(null)
            .matchType("exact")
            .modified(Timestamp.valueOf("2008-01-15 17:27:08.177000"));
    }
}
