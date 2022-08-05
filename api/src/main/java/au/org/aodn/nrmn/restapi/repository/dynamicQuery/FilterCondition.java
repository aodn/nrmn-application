package au.org.aodn.nrmn.restapi.repository.dynamicQuery;

import au.org.aodn.nrmn.restapi.controller.filter.Filter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import java.sql.Date;
import java.util.Arrays;

public abstract class FilterCondition {

    interface DBField {
        String getDBFieldName();
    }

    protected static <T extends Enum<T>> boolean containsSupportField(Filter[] fs, Class<T> e) {
        return Arrays.stream(fs).filter(filter -> isSupportedField(filter.getFieldName(), e)).findFirst().isPresent();
    }

    protected static <T extends Enum<T>> boolean isSupportedField(String f, Class<T> e) {
        return Arrays.stream(e.getEnumConstants()).filter(v -> v.toString().equals(f)).findFirst().isPresent();
    }

    protected static <T extends Enum<T>> T getFieldEnum(String f, Class<T> e) {
        return Arrays.stream(e.getEnumConstants()).filter(v -> v.toString().equals(f)).findFirst().get();
    }

    protected Predicate getSimpleFieldSpecification(
            From<?, ?> root, From<?,?> table, CriteriaBuilder criteriaBuilder, String field, String value, String operation) {

        From<?,?> r = table == null ? root : table;

        // Need to handle date field, as we cannot assume date format always yyyy-MM-dd
        Expression<String> target = null;

        if(r.get(field).getJavaType() == Date.class) {
            // DB specific function call !!!!
            target = criteriaBuilder.function("to_char", String.class, r.get(field), criteriaBuilder.literal("yyyy-MM-dd"));
        }
        else{
            target = criteriaBuilder.lower(r.get(field).as(String.class));
        }

        // Single condition
        switch(operation) {

            case "startsWith": {
                return criteriaBuilder.like(target, value.toLowerCase() + "%");
            }
            case "endsWith": {
                return criteriaBuilder.like(target, "%" + value.toLowerCase());
            }
            case "contains": {
                return criteriaBuilder.like(target, "%" + value.toLowerCase() + "%");
            }
            case "notContains" : {
                return criteriaBuilder.notLike(target, "%" + value.toLowerCase() + "%");
            }
            case "equals" : {
                return criteriaBuilder.equal(target, value.toLowerCase());
            }
            case "notEqual" : {
                return criteriaBuilder.notEqual(target, value.toLowerCase());
            }
            case "blank" : {
                return criteriaBuilder.or(
                        criteriaBuilder.equal(target, ""),
                        criteriaBuilder.isNull(r.get(field)));
            }
            case "notBlank" : {
                return criteriaBuilder.notEqual(target, "");
            }
            default: {
                return null;
            }
        }
    }
}
