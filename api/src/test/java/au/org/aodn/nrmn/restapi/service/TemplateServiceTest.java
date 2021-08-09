package au.org.aodn.nrmn.restapi.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;

import au.org.aodn.nrmn.restapi.model.db.Diver;
import au.org.aodn.nrmn.restapi.model.db.Location;
import au.org.aodn.nrmn.restapi.model.db.Site;
import au.org.aodn.nrmn.restapi.model.db.UiSpeciesAttributes;
import au.org.aodn.nrmn.restapi.repository.DiverRepository;
import au.org.aodn.nrmn.restapi.repository.LetterCodeRepository;
import au.org.aodn.nrmn.restapi.repository.ObservableItemRepository;
import au.org.aodn.nrmn.restapi.repository.ObservationRepository;
import au.org.aodn.nrmn.restapi.repository.SiteRepository;
import au.org.aodn.nrmn.restapi.repository.projections.LetterCodeMapping;
import au.org.aodn.nrmn.restapi.repository.projections.ObservableItemRow;
import au.org.aodn.nrmn.restapi.repository.projections.SpeciesWithAttributesCsvRow;
import lombok.val;

@ExtendWith(MockitoExtension.class)
public class TemplateServiceTest {

    @InjectMocks
    TemplateService templateService;

    @Mock
    DiverRepository diverRepository;

    @Mock
    LetterCodeRepository letterCodeRepository;

    @Mock
    SiteRepository siteRepository;

    @Mock
    ObservationRepository observationRepository;

    @Mock
    ObservableItemRepository observableItemRepository;

    @BeforeEach
    void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void getDiversCsv() throws IOException {
        List<Diver> divers = Arrays.asList(Diver.builder().initials("GWB").fullName("George Bush").build(),
                Diver.builder().initials("BHO").fullName("Barack Obama").build(),
                Diver.builder().initials("DJT").fullName("Donald Trump").build());

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        templateService.writeDiversCsv(printWriter, divers);
        List<String> csvLines = Arrays.stream(stringWriter.toString().split("\n")).map(String::trim)
                .collect(Collectors.toList());

        assertEquals(4, csvLines.size());
        assertEquals("Initials,Full Name", csvLines.get(0));
        assertEquals("BHO,Barack Obama", csvLines.get(1));
        assertEquals("DJT,Donald Trump", csvLines.get(2));
        assertEquals("GWB,George Bush", csvLines.get(3));
    }

    @Test
    void getDiversForTemplate() throws IOException {
        Diver bush = Diver.builder().initials("GWB").fullName("George Bush").build();
        Diver obama = Diver.builder().initials("BHO").fullName("Barack Obama").build();
        Diver trump = Diver.builder().initials("DJT").fullName("Donald Trump").build();
        Diver robot = Diver.builder().initials("101").fullName("Robopresident").build();
        when(diverRepository.findAll()).thenReturn(Arrays.asList(bush, obama, trump, robot));

        List<Diver> divers = templateService.getDiversForTemplate();

        assertEquals(3, divers.size());
        assert (divers.contains(bush));
        assert (divers.contains(obama));
        assert (divers.contains(trump));
        assertFalse("divers should only include alphabetic initials", divers.contains(robot));

    }

    @Test
    void getSitesCsv() throws IOException {
        Site.SiteBuilder builder = Site.builder().state("Tasmania").latitude(-43.1).longitude(147.1)
                .siteName("Springfield");
        Site testSite333 = builder.siteCode("TAS333")
                .location(Location.builder().locationId(333).locationName("Southish").build()).build();
        Site testSite334 = builder.siteCode("TAS334")
                .location(Location.builder().locationId(334).locationName("Southish").build()).build();
        Site testSite335 = builder.siteCode("TAS335")
                .location(Location.builder().locationId(335).locationName("Southish").build()).build();
        Site testSite336 = builder.siteCode("VIC336").state("Victoria")
                .location(Location.builder().locationId(336).locationName("Southish").build()).build();

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        templateService.writeSitesCsv(printWriter,
                Arrays.asList(testSite336, testSite335, testSite334, testSite333));

        List<String> csvLines = Arrays.stream(stringWriter.toString().split("\n")).map(String::trim)
                .collect(Collectors.toList());

        assertEquals(5, csvLines.size());
        assertEquals("Site,Site Name,Latitude,Longitude,Region", csvLines.get(0));
        assertEquals("TAS333,Springfield,-43.1,147.1,Southish", csvLines.get(1));
        assertEquals("TAS334,Springfield,-43.1,147.1,Southish", csvLines.get(2));
        assertEquals("TAS335,Springfield,-43.1,147.1,Southish", csvLines.get(3));
        assertEquals("VIC336,Springfield,-43.1,147.1,Southish", csvLines.get(4));
    }

    @Test
    void getSitesForTemplate() throws IOException {
        Site.SiteBuilder builder = Site.builder().state("Tasmania").latitude(-43.1).longitude(147.1)
                .siteName("Springfield");
        Site testSite333 = builder.siteCode("TAS333")
                .location(Location.builder().locationId(333).locationName("Southish").build()).build();
        Site testSite334 = builder.siteCode("TAS334")
                .location(Location.builder().locationId(334).locationName("Southish").build()).build();
        Site testSite335 = builder.siteCode("TAS335")
                .location(Location.builder().locationId(335).locationName("Southish").build()).build();
        Site testSite336 = builder.siteCode("VIC336").state("Victoria")
                .location(Location.builder().locationId(336).locationName("Southish").build()).build();

        when(siteRepository.findSiteCodesByProvince("Antipodes")).thenReturn(Arrays.asList("TAS333"));
        when(siteRepository.findAll(Example.of(Site.builder().siteCode("TAS333").build())))
                .thenReturn(Arrays.asList(testSite333));

        when(siteRepository.findAll(Example.of(Site.builder().siteCode("TAS334").build())))
                .thenReturn(Arrays.asList(testSite334));

        when(siteRepository.findAll(Example
                .of(Site.builder().location(Location.builder().locationId(335).build()).build())))
                        .thenReturn(Arrays.asList(testSite335));

        when(siteRepository.findAll(Example.of(Site.builder().state("Victoria").build())))
                .thenReturn(Arrays.asList(testSite336));

        Set<Site> sites = templateService.getSitesForTemplate(Arrays.asList(335), Arrays.asList("Antipodes"),
                Arrays.asList("Victoria"), Arrays.asList("TAS334"));

        assertEquals(4, sites.size());
        assert (sites.contains(testSite333));
        assert (sites.contains(testSite334));
        assert (sites.contains(testSite335));
        assert (sites.contains(testSite336));
    }

    @Test
    void getSpeciesCsv() throws IOException {
        SpeciesWithAttributesCsvRow.SpeciesWithAttributesCsvRowBuilder sb = SpeciesWithAttributesCsvRow
                .builder();
        SpeciesWithAttributesCsvRow s1 = sb.letterCode("asa").speciesName("Abudefduf saxatilis").isInvertSized(false)
                .commonName("Sergeant major").l5(2.5).l95(15.0).lMax(20.0).build();

        SpeciesWithAttributesCsvRow s2 = sb.letterCode("aba").speciesName("Acanthurus bahianus").isInvertSized(false)
                .commonName("Ocean surgeon").l5(5.0).l95(30.0).lMax(40.0).build();

        SpeciesWithAttributesCsvRow s3 = sb.letterCode("ach").speciesName("Acanthurus chirurgus").isInvertSized(false)
                .commonName("Doctorfish").l5(7.5).l95(45.0).lMax(60.0).build();

        SpeciesWithAttributesCsvRow s4 = sb.letterCode("sps").speciesName("Species spp.").isInvertSized(null)
                .commonName(null).l5(null).l95(null).lMax(null).build();

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        templateService.writeSpeciesCsv(printWriter, CSVFormat.DEFAULT.withHeader("Letter Code", "Species Name",
                "Common Name", "Species Invert Sizing", "L5", "L95", "LMax"), Arrays.asList(s1, s2, s3, s4));
        List<String> csvLines = Arrays.stream(stringWriter.toString().split("\n")).map(String::trim)
                .collect(Collectors.toList());

        assertEquals(5, csvLines.size());
        assertEquals("Letter Code,Species Name,Common Name,Species Invert Sizing,L5,L95,LMax", csvLines.get(0));
        assertEquals("asa,Abudefduf saxatilis,Sergeant major,No,2.5,15.0,20.0", csvLines.get(1));
        assertEquals("aba,Acanthurus bahianus,Ocean surgeon,No,5.0,30.0,40.0", csvLines.get(2));
        assertEquals("ach,Acanthurus chirurgus,Doctorfish,No,7.5,45.0,60.0", csvLines.get(3));
        assertEquals("sps,Species spp.,,No,,,", csvLines.get(4));
    }

    @Test
    void getSpeciesForTemplate() throws IOException {

        UiSpeciesAttributes usa1 = new UiSpeciesAttributes() {

            @Override
            public Long getId() {
                return 1l;
            }

            @Override
            public String getSpeciesName() {
                return "Abudefduf saxatilis";
            }

            @Override
            public String getCommonName() {
                return "Sergeant major";
            }

            @Override
            public Boolean getIsInvertSized() {
                return null;
            }

            @Override
            public Double getL5() {
                return 2.5;
            }

            @Override
            public Double getL95() {
                return 15.0;
            }

            @Override
            public Long getMaxAbundance() {
                return 20l;
            }

            @Override
            public Double getLmax() {
                return 20.0;
            }
        };

        UiSpeciesAttributes usa2 = new UiSpeciesAttributes() {

            @Override
            public Long getId() {
                return 2l;
            }

            @Override
            public String getSpeciesName() {
                return "Acanthurus bahianus";
            }

            @Override
            public String getCommonName() {
                return "Ocean surgeon";
            }

            @Override
            public Boolean getIsInvertSized() {
                return null;
            }

            @Override
            public Double getL5() {
                return 5.0;
            }

            @Override
            public Double getL95() {
                return 30.0;
            }

            @Override
            public Long getMaxAbundance() {
                return 40l;
            }

            @Override
            public Double getLmax() {
                return 40.0;
            }
        };

        UiSpeciesAttributes usa3 = new UiSpeciesAttributes() {

            @Override
            public Long getId() {
                return 3l;
            }

            @Override
            public String getSpeciesName() {
                return "Acanthurus chirurgus";
            }

            @Override
            public String getCommonName() {
                return "Doctorfish";
            }

            @Override
            public Boolean getIsInvertSized() {
                return null;
            }

            @Override
            public Double getL5() {
                return 7.5;
            }

            @Override
            public Double getL95() {
                return 45.0;
            }

            @Override
            public Long getMaxAbundance() {
                return 60l;
            }

            @Override
            public Double getLmax() {
                return 60.0;
            }
        };

        List<UiSpeciesAttributes> swaList = Arrays.asList(usa1, usa2, usa3);
        ObservableItemRow o1 = new ObservableItemRow() {

            @Override
            public Integer getObservableItemId() {
                return 123;
            }

            @Override
            public String getLetterCode() {
                return null;
            }

            @Override
            public String getTypeName() {
                return null;
            }

            @Override
            public String getName() {
                return null;
            }

            @Override
            public String getCommonName() {
                return "commonName";
            }

            @Override
            public String getSupersededBy() {
                return null;
            }

            @Override
            public String getSupersededNames() {
                return null;
            }

            @Override
            public String getSupersededIDs() {
                return null;
            }

            @Override
            public String getPhylum() {
                return null;
            }

            @Override
            public String getClassName() {
                return "className";
            }

            @Override
            public String getOrder() {
                return null;
            }

            @Override
            public String getFamily() {
                return null;
            }

            @Override
            public String getGenus() {
                return null;
            }
        };

        val lcm1 = new LetterCodeMapping() {

            @Override
            public Long getObservableItemId() {
                return 1l;
            }

            @Override
            public String getLetterCode() {
                return "one";
            }
        };

        val lcm2 = new LetterCodeMapping() {

            @Override
            public Long getObservableItemId() {
                return 2l;
            }

            @Override
            public String getLetterCode() {
                return "two";
            }
        };

        val lcm3 = new LetterCodeMapping() {

            @Override
            public Long getObservableItemId() {
                return 3l;
            }

            @Override
            public String getLetterCode() {
                return "three";
            }
        };

        Site site1 = Site.builder().siteId(1).build();
        val siteIds = Arrays.asList(site1.getSiteId());
        val obsIds = Arrays.asList(123);
        when(observableItemRepository.getAllWithMethodForSites(2, siteIds))
                .thenReturn(Arrays.asList(o1).stream().collect(Collectors.toList()));
        when(observationRepository.getSpeciesAttributesByIds(obsIds)).thenReturn(swaList);
        when(letterCodeRepository.getForSiteIds(siteIds)).thenReturn(Arrays.asList(lcm1, lcm2, lcm3));
        List<LetterCodeMapping> letterCodeMappings = letterCodeRepository.getForSiteIds(siteIds);
        HashMap<Long, String> letterCodeMap = new HashMap<Long, String>();
        letterCodeMappings.forEach(
                m -> letterCodeMap.put(Long.valueOf(m.getObservableItemId()), m.getLetterCode()));
        val speciesWithAttributes = templateService.getSpeciesForTemplate(2, siteIds, letterCodeMap);
        // Add one for 'survey not done' added by the service
        assertEquals(swaList.size() + 1, speciesWithAttributes.size());
        assertEquals("Abudefduf saxatilis", speciesWithAttributes.get(0).getSpeciesName());
        assertEquals("one", speciesWithAttributes.get(0).getLetterCode());
        assertEquals("Doctorfish", speciesWithAttributes.get(2).getCommonName());
        assertEquals("two", speciesWithAttributes.get(1).getLetterCode());
        assertEquals("Doctorfish", speciesWithAttributes.get(2).getCommonName());
        assertEquals("three", speciesWithAttributes.get(2).getLetterCode());
    }
}
