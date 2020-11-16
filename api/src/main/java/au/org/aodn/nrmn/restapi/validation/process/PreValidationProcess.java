package au.org.aodn.nrmn.restapi.validation.process;

import au.org.aodn.nrmn.restapi.model.db.*;
import au.org.aodn.nrmn.restapi.model.db.enums.Directions;
import au.org.aodn.nrmn.restapi.repository.DiverRepository;
import au.org.aodn.nrmn.restapi.validation.BaseGlobalValidator;
import au.org.aodn.nrmn.restapi.validation.BaseRowValidator;
import au.org.aodn.nrmn.restapi.validation.StagedRowFormatted;
import au.org.aodn.nrmn.restapi.validation.validators.data.CoordinatesDataCheck;
import au.org.aodn.nrmn.restapi.validation.validators.entities.SpeciesExists;
import au.org.aodn.nrmn.restapi.validation.validators.data.DirectionDataCheck;
import au.org.aodn.nrmn.restapi.validation.validators.entities.DiverExists;
import au.org.aodn.nrmn.restapi.validation.validators.entities.SiteCodeExists;
import au.org.aodn.nrmn.restapi.validation.validators.format.*;
import au.org.aodn.nrmn.restapi.validation.provider.ValidatorProvider;
import com.sun.org.apache.xpath.internal.operations.Bool;
import cyclops.companion.Functions;
import cyclops.companion.Monoids;
import cyclops.control.Maybe;
import cyclops.control.Validated;
import cyclops.data.Seq;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.function.Function;

@Component
public class PreValidationProcess implements ValidatorProvider {
    @Autowired
    DiverRepository diverRepo;
    @Autowired
    SiteCodeExists siteCodeExists;

    @Autowired
    SpeciesExists speciesExists;

    @Override
    public Seq<BaseRowValidator> getExtendedValidators() {
        return Seq.of(
                new IntegerFormat(StagedRow::getInverts, "Inverts", Collections.emptyList()),
                new IntegerFormat(StagedRow::getM2InvertSizingSpecies, "M2InvertSizingSpecies,", Collections.emptyList()),
                new IntegerFormat(StagedRow::getL5, "L5,", Collections.emptyList()),
                new IntegerFormat(StagedRow::getLmax, "L95,", Collections.emptyList()),
                new IntegerFormat(StagedRow::getLmax, "Lmax,", Collections.emptyList()),
                new BooleanFormat(StagedRow::getIsInvertSizing, "IsInvertSizing")
        );
    }


    @Override
    public Seq<BaseRowValidator> getRowValidators() {
        return Seq.of(
                siteCodeExists,
                new DateFormat(),
                new TimeFormat(),

                new DiverExists(StagedRow::getDiver, "Diver", diverRepo),
                new DiverExists(StagedRow::getBuddy, "Buddy", diverRepo),
                new DiverExists(StagedRow::getPqs, "P-Qs", diverRepo),

                new DoubleFormat(StagedRow::getDepth, "Depth"),
                new IntegerFormat(StagedRow::getMethod, "Method", Arrays.asList(0, 1, 2, 3, 4, 5, 7, 10)),
                new IntegerFormat(StagedRow::getBlock, "Block", Arrays.asList(0, 1, 2)),

                speciesExists,

                new IntegerFormat(StagedRow::getVis, "Vis", Collections.emptyList()),


                new IntegerFormat(StagedRow::getCode, "Code", Collections.emptyList()),
                new IntegerFormat(StagedRow::getTotal, "Total", Collections.emptyList()),

                new DoubleFormat(StagedRow::getLatitude, "Latitude"),
                new DoubleFormat(StagedRow::getLongitude, "Longitude"),
                new DirectionDataCheck()

                );
    }


    public Validated<StagedRowError, Seq<Object>> validate(StagedRow target) {
        val baseValidators = getRowValidators();
        val validators = (target.getStagedJob().getIsExtendedSize()) ? baseValidators.appendAll(getExtendedValidators()) : baseValidators;

        return validators.map(v ->
                v.valid(target).bimap(Function.identity(), Seq::of)
        ).stream().reduce(
                Validated.valid(Seq.empty()),
                (v1, v2) -> v1.combine(Monoids.seqConcat(), v2)
        );
    }

    public Maybe<StagedRowFormatted> preValidated(StagedRow target) {

        val mergeValidation = validate(target);

        return mergeValidation.toMaybe().filter(seq -> {
            val site = (Site) seq.get(0).orElseGet(null);
            site.calcGeom();
            return new CoordinatesDataCheck(site).valid(target).isValid();
        }).map(seq -> {
            val site = (Site) seq.get(0).orElseGet(null);
            val date = (Date) seq.get(1).orElseGet(null);
            val time = (LocalTime) seq.get(2).orElseGet(null);

            val diver = (Diver) seq.get(3).orElseGet(null);
            val buddy = (Diver) seq.get(4).orElseGet(null);
            val pqs = (Diver) seq.get(5).orElseGet(null);

            val depth = (Double) seq.get(6).orElseGet(null);
            val method = (Integer) seq.get(7).orElseGet(null);
            val block = (Integer) seq.get(8).orElseGet(null);

            val species = (AphiaRef) seq.get(9).orElseGet(null);

            val vis = (Integer) seq.get(10).orElseGet(null);
            val code = (Integer) seq.get(11).orElseGet(null);
            val total = (Integer) seq.get(12).orElseGet(null);
            val direction = (Directions) seq.get(15).orElseGet(null);


            val dateTime = new Date(date.getTime() + time.toNanoOfDay());
            val rowFormatted = new StagedRowFormatted();
            rowFormatted.setDate(dateTime);
            rowFormatted.setSite(site);
            rowFormatted.setDiver(diver);
            rowFormatted.setBuddy(buddy);
            rowFormatted.setPqs(pqs);
            rowFormatted.setDepth(depth);
            rowFormatted.setMethod(method);
            rowFormatted.setBlock(block);
            rowFormatted.setSpecies(species);
            rowFormatted.setVis(vis);
            rowFormatted.setCode(code);
            rowFormatted.setDirection(direction);
            rowFormatted.setTotal(total);

            if (target.getStagedJob().getIsExtendedSize()) {
                val inverts = (Integer) seq.get(13).orElseGet(null);
                val M2InvertSizingSpecies = (Integer) seq.get(14).orElseGet(null);
                val l5 = (Integer) seq.get(15).orElseGet(null);
                val l95 = (Integer) seq.get(16).orElseGet(null);
                val lmax = (Integer) seq.get(17).orElseGet(null);
                val isInvertSizing = (Boolean) seq.get(18).orElseGet(null);
                rowFormatted.setInverts(inverts);
                rowFormatted.setL5(l5);
                rowFormatted.setL95(l95);
                rowFormatted.setLmax(lmax);
                rowFormatted.setIsInvertSizing(isInvertSizing);
            }
            return rowFormatted;
        });
    }
}

