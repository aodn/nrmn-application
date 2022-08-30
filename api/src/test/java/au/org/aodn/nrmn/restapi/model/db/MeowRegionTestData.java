package au.org.aodn.nrmn.restapi.model.db;

import au.org.aodn.nrmn.restapi.repository.MeowRegionsRepository;
import org.locationtech.jts.geom.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.Policy;
import java.util.ArrayList;
import java.util.List;

@Component
public class MeowRegionTestData {

    @Autowired
    private MeowRegionsRepository meowRegionRepository;

    private GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    public MeowEcoRegions persistedMeowRegion(MeowEcoRegions meow) {
        meowRegionRepository.saveAndFlush(meow);
        return meow;
    }

    public MeowEcoRegions buildWith(int itemNumber) {

        List<Polygon> polygonList = new ArrayList<>();

        Coordinate[] coordinates1 = new Coordinate[] {
                new Coordinate(0, 0),
                new Coordinate(0, 1),
                new Coordinate(itemNumber, 1),
                new Coordinate(itemNumber, 0),
                new Coordinate(0, 0)};

        Coordinate[] coordinates2 = new Coordinate[] {
                new Coordinate(itemNumber, 0),
                new Coordinate(itemNumber, 1),
                new Coordinate(itemNumber + 1, 1),
                new Coordinate(itemNumber + 1, 0),
                new Coordinate(itemNumber, 0)};


        Coordinate[] coordinates3 = new Coordinate[] {
                new Coordinate(itemNumber + 6, 0),
                new Coordinate(itemNumber + 6, 1),
                new Coordinate(itemNumber + 7, 1),
                new Coordinate(itemNumber + 7, 0),
                new Coordinate(itemNumber + 6, 0)};

        switch(itemNumber % 3) {
            case 1: {
                polygonList.add(geometryFactory.createPolygon(coordinates1));
            }
        }
        polygonList.add(geometryFactory.createPolygon(coordinates1));
        polygonList.add(geometryFactory.createPolygon(coordinates2));
        polygonList.add(geometryFactory.createPolygon(coordinates3));

        return MeowEcoRegions.builder()
                .id(itemNumber)
                .ecoRegion("Region " + itemNumber)
                .latZone("Zone " + itemNumber)
                .polygon(new MultiPolygon(polygonList.toArray(new Polygon[polygonList.size()]), geometryFactory))
                .build();
    }
}
