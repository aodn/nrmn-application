package au.org.aodn.nrmn.restapi.service;

import au.org.aodn.nrmn.restapi.model.db.Diver;
import au.org.aodn.nrmn.restapi.model.db.Location;
import au.org.aodn.nrmn.restapi.model.db.Site;
import au.org.aodn.nrmn.restapi.repository.DiverRepository;
import au.org.aodn.nrmn.restapi.repository.SiteRepository;
import cyclops.companion.Streams;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.util.stream.Collectors.toList;

@Service
public class TemplateService {
    private static final CSVFormat DIVERS_FORMAT = CSVFormat.DEFAULT.withHeader("INITIALS", "FULL NAME");
    private static final CSVFormat SITES_FORMAT = CSVFormat.DEFAULT.withHeader("SITE", "Site Name", "Latitude", "Longitude", "Region");

    @Autowired
    DiverRepository diverRepository;

    @Autowired
    SiteRepository siteRepository;

    public void writeZip(OutputStream outputStream,
                         Collection<Integer> locations,
                         Collection<String> provinces,
                         Collection<String> states,
                         Collection<String> siteCodes) throws IOException {

        ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
        Writer writer = new OutputStreamWriter(zipOutputStream);

        zipOutputStream.putNextEntry(new ZipEntry("divers.csv"));
        writeDiversCsv(writer, getDiversForTemplate());
        writer.flush();
        zipOutputStream.closeEntry();

        zipOutputStream.putNextEntry(new ZipEntry("sites.csv"));
        writeSitesCsv(writer, getSitesForTemplate(locations, provinces, states, siteCodes));
        writer.flush();
        zipOutputStream.closeEntry();

        zipOutputStream.flush();
        zipOutputStream.close();
    }

    public void writeDiversCsv(Writer writer, Collection<Diver> divers) throws IOException {
        CSVPrinter csvPrinter = DIVERS_FORMAT.print(writer);
        List<List<String>> records = divers.stream()
                .distinct()
                .sorted(Comparator.comparing(Diver::getInitials))
                .map(this::getDiverAsCsvRecord).collect(toList());
        csvPrinter.printRecords(records);
    }

    private List<String> getDiverAsCsvRecord(Diver diver) {
        return Arrays.asList(diver.getInitials(), diver.getFullName());
    }

    public List<Diver> getDiversForTemplate() {
        return diverRepository.findAll().stream()
                .filter(d -> d.getInitials().matches("[A-Z]*"))
                .collect(Collectors.toList());
    }

    public void writeSitesCsv(Writer writer, Collection<Site> sites) throws IOException {
        CSVPrinter csvPrinter = SITES_FORMAT.print(writer);

        List<List<String>> records = sites.stream()
                .distinct()
                .sorted(Comparator.comparing(Site::getSiteCode))
                .map(this::getSiteAsCsvRecord)
                .collect(toList());

        csvPrinter.printRecords(records);
    }

    private List<String> getSiteAsCsvRecord(Site site) {
        return Arrays.asList(
                site.getSiteCode(),
                site.getSiteName(),
                toString(site.getLatitude()),
                toString(site.getLongitude()),
                site.getLocation().getLocationName());
    }

    public List<Site> getSitesForTemplate(Collection<Integer> locations,
                                Collection<String> provinces,
                                Collection<String> states,
                                Collection<String> siteCodes) {

        Stream<String> siteCodesFromProvinces = provinces == null ? Stream.empty() : provinces.stream()
                .flatMap(p -> siteRepository.findSiteCodesByProvince(p).stream());

        Stream<Site> sites = (siteCodes == null
                ? siteCodesFromProvinces
                : Streams.concat(siteCodesFromProvinces, siteCodes.stream()))
                .flatMap(sc -> siteRepository.findAll(Example.of(Site.builder().siteCode(sc).build())).stream());

        if(locations != null) {
            sites = Stream.concat(sites, locations.stream()
                    .flatMap(l -> siteRepository.findAll(
                            Example.of(Site.builder()
                                    .location(Location.builder().locationId(l).build()).build())).stream()));
        }

        if(states != null) {
            sites = Stream.concat(sites, states.stream()
                    .flatMap(s -> siteRepository.findAll(Example.of(Site.builder().state(s).build())).stream()));
        }

        return sites.distinct().collect(toList());
    }

    private String toString(Object couldBeNull) {
        return couldBeNull == null ? null : couldBeNull.toString();
    }
}
