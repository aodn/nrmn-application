package au.org.aodn.nrmn.restapi.service;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import au.org.aodn.nrmn.restapi.model.db.Diver;
import au.org.aodn.nrmn.restapi.model.db.Location;
import au.org.aodn.nrmn.restapi.model.db.Site;
import au.org.aodn.nrmn.restapi.repository.DiverRepository;
import au.org.aodn.nrmn.restapi.repository.LetterCodeRepository;
import au.org.aodn.nrmn.restapi.repository.ObservableItemRepository;
import au.org.aodn.nrmn.restapi.repository.ObservationRepository;
import au.org.aodn.nrmn.restapi.repository.SiteRepository;
import au.org.aodn.nrmn.restapi.repository.projections.LetterCodeMapping;
import au.org.aodn.nrmn.restapi.repository.projections.ObservableItemRow;
import au.org.aodn.nrmn.restapi.repository.projections.SpeciesWithAttributesCsvRow;
import cyclops.companion.Streams;

@Service
public class TemplateService {
        private static final CSVFormat DIVERS_FORMAT = CSVFormat.DEFAULT.withHeader("Initials", "Full Name");
        private static final CSVFormat SITES_FORMAT = CSVFormat.DEFAULT.withHeader("Site", "Site Name", "Latitude",
                        "Longitude", "Region");
        private static final CSVFormat M1_M2_FORMAT = CSVFormat.DEFAULT.withHeader("Letter Code", "Species Name",
                        "Common Name", "Species Invert Sizing", "L5", "L95", "LMax");
        private static final CSVFormat M3_FORMAT = CSVFormat.DEFAULT.withHeader("Letter Code", "Species Name",
                        "Common Name");

        @Autowired
        DiverRepository diverRepository;

        @Autowired
        SiteRepository siteRepository;

        @Autowired
        ObservableItemRepository observableItemRepository;

        @Autowired
        LetterCodeRepository LetterCodeRepository;

        @Autowired
        private ObservationRepository observationRepository;

        public void writeZip(OutputStream outputStream, Collection<Integer> locations, Collection<String> provinces,
                        Collection<String> states, Collection<String> siteCodes) throws IOException {

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

                List<Integer> siteIds = sites.stream().map(s -> s.getSiteId()).collect(Collectors.toList());
                List<LetterCodeMapping> letterCodeMappings = LetterCodeRepository.getForSiteIds(siteIds);
                HashMap<Long, String> letterCodeMap = new HashMap<Long, String>();
                letterCodeMappings.forEach(
                                m -> letterCodeMap.put(Long.valueOf(m.getObservableItemId()), m.getLetterCode()));

                List<SpeciesWithAttributesCsvRow> speciesWithAttributes = getSpeciesForTemplate(1, siteIds,
                                letterCodeMap);
                zipOutputStream.putNextEntry(new ZipEntry("m1species.csv"));
                writeSpeciesCsv(writer, M1_M2_FORMAT, speciesWithAttributes);
                writer.flush();
                zipOutputStream.closeEntry();

                zipOutputStream.putNextEntry(new ZipEntry("m2species.csv"));
                writeSpeciesCsv(writer, M1_M2_FORMAT, getSpeciesForTemplate(2, siteIds, letterCodeMap));
                writer.flush();
                zipOutputStream.closeEntry();

                zipOutputStream.putNextEntry(new ZipEntry("m3species.csv"));
                List<ObservableItemRow> observableItems = observableItemRepository.getAllWithMethodForSites(3, siteIds);
                writeM3SpeciesCsv(writer, observableItems);
                writer.flush();
                zipOutputStream.closeEntry();

                zipOutputStream.flush();
                zipOutputStream.close();
        }

        private void writeM3SpeciesCsv(Writer writer, Collection<ObservableItemRow> observableItems)
                        throws IOException {

                List<SpeciesWithAttributesCsvRow> species = observableItems.stream()
                                .map(s -> SpeciesWithAttributesCsvRow.builder().speciesName(s.getName())
                                                .commonName(s.getCommonName()).letterCode(s.getLetterCode()).build())
                                .collect(Collectors.toList());

                List<SpeciesWithAttributesCsvRow> speciesResult = species.stream().collect(Collectors.toList());
                speciesResult.add(SpeciesWithAttributesCsvRow.builder().letterCode("snd").speciesName("Survey Not Done")
                                .isInvertSized(false).build());

                CSVPrinter csvPrinter = M3_FORMAT.print(writer);
                List<List<String>> records = speciesResult.stream().distinct()
                                .sorted(Comparator.comparing(SpeciesWithAttributesCsvRow::getSpeciesName))
                                .map(this::getSpeciesAsCsvRecord).collect(toList());
                csvPrinter.printRecords(records);
        }

        public void writeDiversCsv(Writer writer, Collection<Diver> divers) throws IOException {
                CSVPrinter csvPrinter = DIVERS_FORMAT.print(writer);
                List<List<String>> records = divers.stream().distinct().sorted(Comparator.comparing(Diver::getInitials))
                                .map(this::getDiverAsCsvRecord).collect(toList());
                csvPrinter.printRecords(records);
        }

        private List<String> getDiverAsCsvRecord(Diver diver) {
                return Arrays.asList(diver.getInitials(), diver.getFullName());
        }

        public List<Diver> getDiversForTemplate() {
                return diverRepository.findAll().stream().filter(d -> d.getInitials().matches("[A-Z]*"))
                                .collect(Collectors.toList());
        }

        public void writeSitesCsv(Writer writer, Collection<Site> sites) throws IOException {
                CSVPrinter csvPrinter = SITES_FORMAT.print(writer);
                List<List<String>> records = sites.stream().distinct().sorted(Comparator.comparing(Site::getSiteCode))
                                .map(this::getSiteAsCsvRecord).collect(toList());

                csvPrinter.printRecords(records);
        }

        private List<String> getSiteAsCsvRecord(Site site) {
                return Arrays.asList(site.getSiteCode(), site.getSiteName(), toString(site.getLatitude()),
                                toString(site.getLongitude()), site.getLocation().getLocationName());
        }

        public Set<Site> getSitesForTemplate(Collection<Integer> locations, Collection<String> provinces,
                        Collection<String> states, Collection<String> siteCodes) {

                Stream<String> siteCodesFromProvinces = provinces.stream()
                                .flatMap(p -> siteRepository.findSiteCodesByProvince(p).stream());

                Stream<Site> sites = Streams.concat(siteCodesFromProvinces, siteCodes.stream()).flatMap(
                                sc -> siteRepository.findAll(Example.of(Site.builder().siteCode(sc).build())).stream());

                sites = Stream.concat(sites, locations.stream()
                                .flatMap(l -> siteRepository.findAll(Example.of(Site.builder()
                                                .location(Location.builder().locationId(l).build()).build()))
                                                .stream()));

                sites = Stream.concat(sites, states.stream().flatMap(
                                s -> siteRepository.findAll(Example.of(Site.builder().state(s).build())).stream()));

                return sites.collect(toSet());
        }

        public void writeSpeciesCsv(Writer writer, CSVFormat csvFormat, Collection<SpeciesWithAttributesCsvRow> species)
                        throws IOException {
                CSVPrinter csvPrinter = csvFormat.print(writer);
                List<List<String>> records = species.stream().distinct()
                                .sorted(Comparator.comparing(SpeciesWithAttributesCsvRow::getSpeciesName))
                                .map(this::getSpeciesAsCsvRecord).collect(toList());
                csvPrinter.printRecords(records);
        }

        private List<String> getSpeciesAsCsvRecord(SpeciesWithAttributesCsvRow species) {
                return Arrays.asList(species.getLetterCode(), species.getSpeciesName(), species.getCommonName(),
                                species.getIsInvertSized() != null && species.getIsInvertSized() == true ? "Yes" : "No", toString(species.getL5()),
                                toString(species.getL95()), toString(species.getLMax()));
        }

        public List<SpeciesWithAttributesCsvRow> getSpeciesForTemplate(Integer mode, Collection<Integer> siteIds,
                        HashMap<Long, String> letterCodeMap) {
                List<ObservableItemRow> observableItemRows = observableItemRepository.getAllWithMethodForSites(mode,
                                siteIds);

                List<Integer> observableItemIds = observableItemRows.stream()
                                .map(ObservableItemRow::getObservableItemId).collect(toList());
                List<SpeciesWithAttributesCsvRow> species = observationRepository
                                .getSpeciesAttributesByIds(observableItemIds).stream()
                                .map(s -> SpeciesWithAttributesCsvRow.builder().letterCode(letterCodeMap.get(s.getId()))
                                                .speciesName(s.getSpeciesName()).commonName(s.getCommonName())
                                                .isInvertSized(s.getIsInvertSized() == true).l5(s.getL5()).l95(s.getL95())
                                                .lMax(s.getLmax()).build())
                                .collect(Collectors.toList());
                List<SpeciesWithAttributesCsvRow> speciesResult = species.stream().collect(Collectors.toList());
                speciesResult.add(SpeciesWithAttributesCsvRow.builder().letterCode("snd").speciesName("Survey Not Done")
                                .isInvertSized(false).build());
                return speciesResult;
        }

        private String toString(Object couldBeNull) {
                return couldBeNull == null ? null : couldBeNull.toString();
        }
}
