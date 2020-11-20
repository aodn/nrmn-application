package au.org.aodn.nrmn.restapi.validation.process;

import au.org.aodn.nrmn.restapi.model.db.*;
import au.org.aodn.nrmn.restapi.model.db.enums.Directions;
import au.org.aodn.nrmn.restapi.repository.DiverRepository;
import au.org.aodn.nrmn.restapi.validation.BaseRowValidator;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;
import au.org.aodn.nrmn.restapi.validation.validators.data.CoordinatesDataCheck;
import au.org.aodn.nrmn.restapi.validation.validators.entities.SpeciesExists;
import au.org.aodn.nrmn.restapi.validation.validators.data.DirectionDataCheck;
import au.org.aodn.nrmn.restapi.validation.validators.entities.DiverExists;
import au.org.aodn.nrmn.restapi.validation.validators.entities.SiteCodeExists;
import au.org.aodn.nrmn.restapi.validation.validators.format.*;
import cyclops.companion.Monoids;
import cyclops.control.Maybe;
import cyclops.control.Validated;
import cyclops.data.HashMap;
import cyclops.data.Seq;
import cyclops.data.tuple.Tuple2;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class RawValidation {
    @Autowired
    DiverRepository diverRepo;
    @Autowired
    SiteCodeExists siteCodeExists;

    @Autowired
    SpeciesExists speciesExists;

    public HashMap<String, BaseRowValidator> getExtendedValidators() {
        return HashMap.fromStream(
                Stream.of(
                        Tuple2.of("Inverts", new IntegerFormatValidation(StagedRow::getInverts, "Inverts", Collections.emptyList())),
                        Tuple2.of("M2InvertSizingSpecies", new IntegerFormatValidation(StagedRow::getM2InvertSizingSpecies, "M2InvertSizingSpecies,", Collections.emptyList())),
                        Tuple2.of("L5", new IntegerFormatValidation(StagedRow::getL5, "L5,", Collections.emptyList())),
                        Tuple2.of("L95", new IntegerFormatValidation(StagedRow::getLMax, "L95,", Collections.emptyList())),
                        Tuple2.of("Lmax", new IntegerFormatValidation(StagedRow::getLMax, "Lmax,", Collections.emptyList())),
                        Tuple2.of("IsInvertSizing", new BooleanFormatValidation(StagedRow::getIsInvertSizing, "IsInvertSizing"))
                )
        );
    }


    public Seq<Tuple2<String, BaseRowValidator>> getRowValidators() {
        return
                Seq.of(
                        Tuple2.of("site", siteCodeExists),
                        Tuple2.of("Date", new DateFormatValidation()),
                        Tuple2.of("Time", new TimeFormatValidation()),
                        Tuple2.of("Diver", new DiverExists(StagedRow::getDiver, "Diver", diverRepo)),
                        Tuple2.of("Buddy", new DiverExists(StagedRow::getBuddy, "Buddy", diverRepo)),
                        Tuple2.of("P-Qs", new DiverExists(StagedRow::getPqs, "P-Qs", diverRepo)),

                        Tuple2.of("Depth", new DoubleFormatValidation(StagedRow::getDepth, "Depth")),
                        Tuple2.of("Method", new IntegerFormatValidation(StagedRow::getMethod, "Method", Arrays.asList(0, 1, 2, 3, 4, 5, 7, 10))),
                        Tuple2.of("Block", new IntegerFormatValidation(StagedRow::getBlock, "Block", Arrays.asList(0, 1, 2))),

                        Tuple2.of("Species", speciesExists),

                        Tuple2.of("Vis", new IntegerFormatValidation(StagedRow::getVis, "Vis", Collections.emptyList())),

                        Tuple2.of("Total", new IntegerFormatValidation(StagedRow::getTotal, "Total", Collections.emptyList())),

                        Tuple2.of("Latitude", new DoubleFormatValidation(StagedRow::getLatitude, "Latitude")),
                        Tuple2.of("Longitude", new DoubleFormatValidation(StagedRow::getLongitude, "Longitude")),
                        Tuple2.of("Direction", new DirectionDataCheck())
                );
    }


    public Validated<StagedRowError, Seq<Tuple2<String, Object>>> validate(StagedRow target) {
        val baseValidators = getRowValidators();
        val validators =
                (target.getStagedJob().getIsExtendedSize()) ?
                        baseValidators.appendAll(getExtendedValidators()) :
                        baseValidators;

        return validators.map(tuple ->
                tuple._2().valid(target)
                        .bimap(Function.identity(),
                                content -> Seq.of(Tuple2.of(tuple._1(), content)))
        ).stream().reduce(
                Validated.valid(Seq.empty()),
                (v1, v2) -> v1.combine(Monoids.seqConcat(), v2)
        );
    }


    public Maybe<StagedRowFormatted> preValidated(
            StagedRow target,
            Validated<StagedRowError, Seq<Tuple2<String, Object>>> mergeValidators) {
        val validatorsWithMap =
                mergeValidators
                        .map(seq -> seq.stream()
                                .toHashMap(key -> key._1(), value -> value._2()));
        return validatorsWithMap.toMaybe()
                .filter(hMap -> {
                    val site = (Site) hMap.get("Site").orElseGet(null);
                    site.calcGeom();
                    return new CoordinatesDataCheck(site).valid(target).isValid();
                }).map(hMap -> {
                    val site = (Site) hMap.get("Site").orElseGet(null);
                    val date = (LocalDate) hMap.get("Date").orElseGet(null);
                    val time = (LocalTime) hMap.get("Time").orElseGet(null);

                    val diver = (Diver) hMap.get("Diver").orElseGet(null);
                    val buddy = (Diver) hMap.get("Buddy").orElseGet(null);
                    val pqs = (Diver) hMap.get("P-Qs").orElseGet(null);

                    val depth = (Double) hMap.get("Depth").orElseGet(null);
                    val method = (Integer) hMap.get("Method").orElseGet(null);
                    val block = (Integer) hMap.get("Block").orElseGet(null);

                    val species = (AphiaRef) hMap.get("Species").orElseGet(null);

                    val vis = (Integer) hMap.get("Vis").orElseGet(null);
                    val total = (Integer) hMap.get("Total").orElseGet(null);
                    val direction = (Directions) hMap.get("Direction").orElseGet(null);


                    val rowFormatted = new StagedRowFormatted();
                    rowFormatted.setDate(date);
                    rowFormatted.setTime(time);
                    rowFormatted.setSite(site);
                    rowFormatted.setDiver(diver);
                    rowFormatted.setBuddy(buddy);
                    rowFormatted.setPqs(pqs);
                    rowFormatted.setDepth(depth);
                    rowFormatted.setMethod(method);
                    rowFormatted.setBlock(block);
                    rowFormatted.setSpecies(species);
                    rowFormatted.setVis(vis);
                    rowFormatted.setCode(target.getCode());
                    rowFormatted.setDirection(direction);
                    rowFormatted.setTotal(total);

                    if (target.getStagedJob().getIsExtendedSize()) {
                        val inverts = (Integer) hMap.get("Inverts").orElseGet(null);
                        val m2InvertSizingSpecies = (Integer) hMap.get("M2InvertSizingSpecies").orElseGet(null);
                        val l5 = (Integer) hMap.get("L5").orElseGet(null);
                        val l95 = (Integer) hMap.get("L95").orElseGet(null);
                        val lmax = (Integer) hMap.get("Lmax").orElseGet(null);
                        val isInvertSizing = (Boolean) hMap.get("IsInvertSizing").orElseGet(null);
                        rowFormatted.setInverts(inverts);
                        rowFormatted.setM2InvertSizingSpecies(m2InvertSizingSpecies);
                        rowFormatted.setL5(l5);
                        rowFormatted.setL95(l95);
                        rowFormatted.setLMax(lmax);
                        rowFormatted.setIsInvertSizing(isInvertSizing);
                    }
                    return rowFormatted;
                });
    }


    public List<StagedRowFormatted> preValidated(List<StagedRow> targets) {
        return targets
                .stream()
                .flatMap(row -> {
                    val validatedRow = validate(row);
                    return preValidated(row, validatedRow).stream();
                }).collect(Collectors.toList());
    }
}

