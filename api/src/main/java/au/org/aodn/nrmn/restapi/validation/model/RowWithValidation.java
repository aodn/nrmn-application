package au.org.aodn.nrmn.restapi.validation.model;

import au.org.aodn.nrmn.restapi.model.db.StagedRow;
import au.org.aodn.nrmn.restapi.model.db.StagedRowError;
import cyclops.control.Validated;
import cyclops.data.Seq;
import cyclops.data.tuple.Tuple2;
import cyclops.function.Monoid;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RowWithValidation<T> {
   private Seq<StagedRow> rows;
   private  Validated<StagedRowError, T> valid;
}
