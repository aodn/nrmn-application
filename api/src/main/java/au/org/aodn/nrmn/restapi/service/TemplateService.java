package au.org.aodn.nrmn.restapi.service;

import au.org.aodn.nrmn.restapi.model.db.*;
import au.org.aodn.nrmn.restapi.repository.DiverRepository;
import au.org.aodn.nrmn.restapi.repository.ObservableItemRepository;
import au.org.aodn.nrmn.restapi.repository.SiteRepository;
import au.org.aodn.nrmn.restapi.repository.SpeciesWithAttributesRepository;
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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Service
public class TemplateService {
    private static final CSVFormat DIVERS_FORMAT = CSVFormat.DEFAULT.withHeader("INITIALS", "FULL NAME");
    private static final CSVFormat SITES_FORMAT = CSVFormat.DEFAULT.withHeader("SITE", "Site Name", "Latitude", "Longitude", "Region");
    private static final CSVFormat SPECIES_FORMAT = CSVFormat.DEFAULT.withHeader("Species_name", "Common_name", "SpeciesInvertSizing", "L5", "L95", "LMax");
    private static final CSVFormat M3_FORMAT = CSVFormat.DEFAULT.withHeader("Letter_code", "Species_name", "Common_name");

    @Autowired
    DiverRepository diverRepository;

    @Autowired
    SiteRepository siteRepository;

    @Autowired
    ObservableItemRepository observableItemRepository;

    @Autowired
    private SpeciesWithAttributesRepository speciesWithAttributesRepository;

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
        Set<Site> sites = getSitesForTemplate(locations, provinces, states, siteCodes);
        writeSitesCsv(writer, sites);
        writer.flush();
        zipOutputStream.closeEntry();

        //TODO: make sure debris and "no species found" are included
        zipOutputStream.putNextEntry(new ZipEntry("m1species.csv"));
        writeSpeciesCsv(writer, getM1SpeciesForTemplate(sites));
        writer.flush();
        zipOutputStream.closeEntry();

        zipOutputStream.putNextEntry(new ZipEntry("m2species.csv"));
        writeSpeciesCsv(writer, getM2SpeciesForTemplate(sites));
        writer.flush();
        zipOutputStream.closeEntry();

        zipOutputStream.putNextEntry(new ZipEntry("m3species.csv"));
        Set<ObservableItem> observableItems = observableItemRepository.getAllM3ObservableItems(sites);
        writeM3SpeciesCsv(writer, observableItems);
        writer.flush();
        zipOutputStream.closeEntry();

        zipOutputStream.flush();
        zipOutputStream.close();
    }

    private void writeM3SpeciesCsv(Writer writer, Collection<ObservableItem> observableItems) throws IOException {
        CSVPrinter csvPrinter = M3_FORMAT.print(writer);
        List<List<String>> records = observableItems.stream()
                .distinct()
                .sorted(Comparator.comparing(ObservableItem::getObservableItemName))
                .map(this::getSpeciesAsM3Record).collect(toList());
        csvPrinter.printRecords(records);

    }

    private List<String> getSpeciesAsM3Record(ObservableItem observableItem) {
        return Arrays.asList(
                observableItem.getLetterCode(),
                observableItem.getSupersededBy() != null ? observableItem.getSupersededBy() : observableItem.getObservableItemName(),
                observableItem.getCommonName());
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

    public Set<Site> getSitesForTemplate(Collection<Integer> locations,
                                         Collection<String> provinces,
                                         Collection<String> states,
                                         Collection<String> siteCodes) {

        Stream<String> siteCodesFromProvinces = provinces.stream()
                .flatMap(p -> siteRepository.findSiteCodesByProvince(p).stream());

        Stream<Site> sites = Streams.concat(siteCodesFromProvinces, siteCodes.stream())
                .flatMap(sc -> siteRepository.findAll(Example.of(Site.builder().siteCode(sc).build())).stream());

        sites = Stream.concat(sites, locations.stream()
                .flatMap(l -> siteRepository.findAll(
                        Example.of(Site.builder()
                                .location(Location.builder().locationId(l).build()).build())).stream()));

        sites = Stream.concat(sites, states.stream()
                .flatMap(s -> siteRepository.findAll(Example.of(Site.builder().state(s).build())).stream()));

        return sites.collect(toSet());
    }

    public void writeSpeciesCsv(Writer writer, Collection<SpeciesWithAttributes> species) throws IOException {
        CSVPrinter csvPrinter = SPECIES_FORMAT.print(writer);
        List<List<String>> records = species.stream()
                .distinct()
                .sorted(Comparator.comparing(SpeciesWithAttributes::getObservableItemName))
                .map(this::getSpeciesAsCsvRecord).collect(toList());
        csvPrinter.printRecords(records);
    }

    private List<String> getSpeciesAsCsvRecord(SpeciesWithAttributes species) {
        return Arrays.asList(
                species.getObservableItemName(),
                species.getCommonName(),
                null,
                toString(species.getL5()),
                toString(species.getL95()),
                toString(species.getLMax()));
    }

    public List<SpeciesWithAttributes> getM1SpeciesForTemplate(Collection<Site> sites) {
        Set<Integer> speciesIds = siteRepository.findM1SpeciesBySites(sites.stream()
                .map(Site::getSiteCode).collect(toSet()));

        return speciesWithAttributesRepository.findAllById(speciesIds);
    }

    public List<SpeciesWithAttributes> getM2SpeciesForTemplate(Collection<Site> sites) {
        Set<ObservableItem> observableItems = observableItemRepository.getAllM2ObservableItems(sites);

        return speciesWithAttributesRepository.findAllById(
                observableItems.stream().map(ObservableItem::getObservableItemId).collect(toList()));
    }

    private String toString(Object couldBeNull) {
        return couldBeNull == null ? null : couldBeNull.toString();
    }
}
