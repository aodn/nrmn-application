package au.org.aodn.nrmn.restapi.validation.process;

import au.org.aodn.nrmn.restapi.model.db.*;
import au.org.aodn.nrmn.restapi.model.db.enums.Directions;
import au.org.aodn.nrmn.restapi.model.db.enums.ValidationLevel;
import au.org.aodn.nrmn.restapi.repository.DiverRepository;
import au.org.aodn.nrmn.restapi.repository.ObservationRepository;
import au.org.aodn.nrmn.restapi.util.ValidatorHelpers;
import au.org.aodn.nrmn.restapi.validation.BaseRowValidator;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;
import au.org.aodn.nrmn.restapi.validation.model.RowWithValidation;
import au.org.aodn.nrmn.restapi.validation.provider.ATRCValidators;
import au.org.aodn.nrmn.restapi.validation.provider.RLSValidators;
import au.org.aodn.nrmn.restapi.validation.validators.data.DirectionDataCheck;
import au.org.aodn.nrmn.restapi.validation.validators.entities.DiverExists;
import au.org.aodn.nrmn.restapi.validation.validators.entities.ObservableItemExists;
import au.org.aodn.nrmn.restapi.validation.validators.entities.SiteCodeExists;
import au.org.aodn.nrmn.restapi.validation.validators.format.*;
import au.org.aodn.nrmn.restapi.validation.validators.passThu.PassThruRef;
import au.org.aodn.nrmn.restapi.validation.validators.passThu.PassThruString;
import cyclops.companion.Monoids;
import cyclops.control.Validated;
import cyclops.data.HashMap;
import cyclops.data.Seq;
import cyclops.data.tuple.Tuple2;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class RawValidation extends ValidatorHelpers {
    @Autowired
    DiverRepository diverRepo;
    @Autowired
    SiteCodeExists siteCodeExists;

    @Autowired
    ObservableItemExists observableItemExists;

    @Autowired
    RLSValidators rlsValidators;

    @Autowired
    ATRCValidators atrcValidators;

    @Autowired
    ObservationRepository obsRepo;


    public HashMap<String, BaseRowValidator> getExtendedValidators() {
        return HashMap.fromStream(
                Stream.of(
                        Tuple2.of("Inverts", new PassThruString(StagedRow::getInverts, "Inverts")),
                        Tuple2.of("M2InvertSizingSpecies", new PassThruString(StagedRow::getM2InvertSizingSpecies, "M2InvertSizingSpecies")),
                        Tuple2.of("L5", new DoubleFormatValidation(StagedRow::getL5, "L5,")),
                        Tuple2.of("L95", new DoubleFormatValidation(StagedRow::getL95, "L95,")),
                        Tuple2.of("Lmax", new DoubleFormatValidation(StagedRow::getLMax, "Lmax,")),
                        Tuple2.of("IsInvertSizing", new BooleanFormatValidation(StagedRow::getIsInvertSizing, "IsInvertSizing"))
                )
        );
    }


    public Seq<Tuple2<String, BaseRowValidator>> getRowValidators() {
        return
                Seq.of(
                        Tuple2.of("Site", siteCodeExists),
                        Tuple2.of("Date", new DateFormatValidation()),
                        Tuple2.of("Time", new TimeFormatValidation()),
                        Tuple2.of("Diver", new DiverExists(StagedRow::getDiver, "Diver", diverRepo, ValidationLevel.BLOCKING)),
                        Tuple2.of("Buddy", new DiverExists(StagedRow::getBuddy, "Buddy", diverRepo, ValidationLevel.WARNING)),
                        Tuple2.of("P-Qs", new DiverExists(StagedRow::getPqs, "P-Qs", diverRepo, ValidationLevel.BLOCKING)),
                        Tuple2.of("Block", new IntegerFormatValidation(StagedRow::getBlock, "Block", Arrays.asList(0, 1, 2, 10))),
                        Tuple2.of("Code", new PassThruString(StagedRow::getCode, "Code")),
                        Tuple2.of("Species", observableItemExists),

                        Tuple2.of("Vis", new OptionalIntegerFormatValidation(StagedRow::getVis, "Vis")),

                        Tuple2.of("Total", new IntegerFormatValidation(StagedRow::getTotal, "Total", Collections.emptyList())),
                        Tuple2.of("MeasureJson", new MeasureJsonValidation()),
                        Tuple2.of("Latitude", new DoubleFormatValidation(StagedRow::getLatitude, "Latitude")),
                        Tuple2.of("Longitude", new DoubleFormatValidation(StagedRow::getLongitude, "Longitude")),
                        Tuple2.of("Direction", new DirectionDataCheck()),
                        Tuple2.of("Ref", new PassThruRef())
                );
    }

    public Seq<Tuple2<String, BaseRowValidator>> getRawValidators(StagedJob job) {
        val program = job.getProgram();

        val extendedChecks = job.getIsExtendedSize() ?
                getExtendedValidators() :
                HashMap.<String, BaseRowValidator>empty();

        Seq<Tuple2<String, BaseRowValidator>> programChecks = program.getProgramName().equals("RLS") ?
                rlsValidators.getRowValidators() :
                atrcValidators.getRowValidators();

        return getRowValidators().appendAll(programChecks).appendAll(extendedChecks);
    }


    public RowWithValidation<Seq<Tuple2<String, Object>>> validate(StagedRow target,
                                                                   Seq<Tuple2<String, BaseRowValidator>> validators) {
        val validation = validators.map(tuple ->
                tuple._2().valid(target)
                        .bimap(Function.identity(),
                                content -> Seq.of(Tuple2.of(tuple._1(), content)))
        ).stream().reduce(
                Validated.valid(Seq.empty()),
                (v1, v2) -> v1.combine(Monoids.seqConcat(), v2)
        );
        val errors = toErrorList(validation);
        target.setErrors(errors);
        return new RowWithValidation(Seq.of(target), validation);
    }

    public StagedRowFormatted toFormat(HashMap<String, Object> values) {
        val site = (Site) values.get("Site").orElseGet(null);
        val date = (LocalDate) values.get("Date").orElseGet(null);
        val time = (Optional<LocalTime>) values.get("Time").orElse(Optional.empty());

        val diver = (Diver) values.get("Diver").orElseGet(null);
        val buddy = (Diver) values.get("Buddy").orElseGet(null);
        val pqs = (Diver) values.get("P-Qs").orElseGet(null);

        val splitDepth = values.get("Depth").orElseGet(null).toString().split("\\.");
        val depth = Integer.parseInt(splitDepth[0]);

        Optional<Integer> survey_num = splitDepth.length == 1
                ? Optional.empty()
                : Optional.of(Integer.parseInt(splitDepth[1]));

        val method = (Integer) values.get("Method").orElseGet(null);
        val block = (Integer) values.get("Block").orElseGet(null);

        val species = (ObservableItem) values.get("Species").orElseGet(null);
        //val speciesAttributesOtp = obsRepo.getSpeciesAttributesById(new Long(species.getObservableItemId()));
//        val speciesAttributes = speciesAttributesOtp
//                .stream()
//                .findFirst()

        val mayBeSpeciesAttributes = Optional.<UiSpeciesAttributes>empty();

        val code = (String) values.get("Code").orElseGet(null);

        val vis = (Optional<Integer>) values.get("Vis").orElse(Optional.empty());
        val total = (Integer) values.get("Total").orElseGet(null);
        val direction = (Directions) values.get("Direction").orElseGet(null);
        val measureJson = (java.util.Map<Integer, Integer>) values.get("MeasureJson").orElseGet(null);

        val ref = (StagedRow) values.get("Ref").orElseGet(null);

        val rowFormatted = new StagedRowFormatted();
        rowFormatted.setDate(date);
        rowFormatted.setTime(time);
        rowFormatted.setSite(site);
        rowFormatted.setDiver(diver);
        rowFormatted.setBuddy(buddy);
        rowFormatted.setPqs(pqs);
        rowFormatted.setDepth(depth);
        rowFormatted.setSurveyNum(survey_num);
        rowFormatted.setMethod(method);
        rowFormatted.setBlock(block);
        rowFormatted.setSpecies(species);
        rowFormatted.setVis(vis);
        rowFormatted.setCode(code);
        rowFormatted.setDirection(direction);
        rowFormatted.setTotal(total);
        rowFormatted.setMeasureJson(measureJson);
        rowFormatted.setRef(ref);
        rowFormatted.setSpeciesAttributesOpt(mayBeSpeciesAttributes);

        if (values.containsKey("Inverts") && values.containsKey("IsInvertSizing")) {
            val inverts = (Integer) values.get("Inverts").orElseGet(null);
            val m2InvertSizingSpecies = (Integer) values.get("M2InvertSizingSpecies").orElseGet(null);
            val l5 = (Double) values.get("L5").orElseGet(null);
            val l95 = (Double) values.get("L95").orElseGet(null);
            val lmax = (Integer) values.get("Lmax").orElseGet(null);
            val isInvertSizing = (Boolean) values.get("IsInvertSizing").orElseGet(null);
            rowFormatted.setInverts(inverts);
            rowFormatted.setM2InvertSizingSpecies(m2InvertSizingSpecies);
            rowFormatted.setL5(l5);
            rowFormatted.setL95(l95);
            rowFormatted.setLMax(lmax);
            rowFormatted.setIsInvertSizing(isInvertSizing);
        }
        return rowFormatted;
    }

    public List<StagedRowFormatted> preValidated(List<StagedRow> targets, StagedJob job) {

        val validators = getRawValidators(job);

        return targets
                .stream()
                .flatMap(row -> {
                    val validatedRow = validate(row, validators);
                    val validated = validatedRow.getValid();
                    val validatorsWithMap =
                            validated.map(seq -> toFormat(seq.toHashMap(Tuple2::_1, Tuple2::_2)));
                    return validatorsWithMap.stream();
                }).collect(Collectors.toList());
    }
}

