package au.org.aodn.nrmn.restapi.repository.dynamicQuery;

import au.org.aodn.nrmn.restapi.controller.transform.Field;
import au.org.aodn.nrmn.restapi.controller.transform.Filter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public abstract class FilterCondition {

    public static final String STARTS_WITH = "startsWith";
    public static final String ENDS_WITH = "endsWith";
    public static final String NOT_ENDS_WITH = "notEndsWith";
    public static final String CONTAINS = "contains";
    public static final String NOT_CONTAINS = "notContains";
    public static final String EQUALS = "equals";
    public static final String NOT_EQUALS = "notEqual";
    public static final String BLANK = "blank";
    public static final String NOT_BLANK = "notBlank";
    public static final String IN = "in";

    interface DBField {
        String getDBFieldName();
    }

    public static <T extends Enum<T>> Optional<Filter> getSupportField(List<Filter> fs, T v) {
        return fs != null ? fs.stream().filter(filter -> filter.getFieldName().equals(v.toString())).findFirst() : Optional.empty();
    }

    protected static <T extends Enum<T>> boolean containsSupportField(List<? extends Field> fs, Class<T> e) {
        return fs.stream().filter(filter -> isSupportedField(filter.getFieldName(), e)).findFirst().isPresent();
    }

    protected static <T extends Enum<T>> boolean isSupportedField(String f, Class<T> e) {
        return Arrays.stream(e.getEnumConstants()).filter(v -> v.toString().equals(f)).findFirst().isPresent();
    }

    protected static <T extends Enum<T>> T getFieldEnum(String f, Class<T> e) {
        Optional<T> k = Arrays.stream(e.getEnumConstants()).filter(v -> v.toString().equals(f)).findFirst();
        return k.isPresent() ? k.get() : null;
    }

    protected Predicate getSimpleFieldSpecification(From<?,?> table, CriteriaBuilder criteriaBuilder, String field,  boolean isAnd, Filter filter1, Filter filter2) {
        if(isAnd) {
            return criteriaBuilder.and(
                    getSimpleFieldSpecification(table, criteriaBuilder, field, filter1.getValue(), filter1.getOperation()),
                    getSimpleFieldSpecification(table, criteriaBuilder, field, filter2.getValue(), filter2.getOperation())
            );
        }
        else {
            return criteriaBuilder.or(
                    getSimpleFieldSpecification(table, criteriaBuilder, field, filter1.getValue(), filter1.getOperation()),
                    getSimpleFieldSpecification(table, criteriaBuilder, field, filter2.getValue(), filter2.getOperation())
            );
        }
    }

    protected Predicate getSimpleFieldSpecification(From<?,?> table, CriteriaBuilder criteriaBuilder, String field, String value, String operation) {

        // Need to handle date field, as we cannot assume date format always yyyy-MM-dd
        Expression<String> target = null;

        if(table.get(field).getJavaType() == Date.class) {
            // DB specific function call !!!!
            target = criteriaBuilder.function("to_char", String.class, table.get(field), criteriaBuilder.literal("yyyy-MM-dd"));
        }
        else{
            target = criteriaBuilder.lower(table.get(field).as(String.class));
        }

        // Single condition
        switch(operation) {

            case STARTS_WITH: {
                return criteriaBuilder.like(target, value.toLowerCase() + "%");
            }
            case ENDS_WITH: {
                return criteriaBuilder.like(target, "%" + value.toLowerCase());
            }
            case NOT_ENDS_WITH: {
                return criteriaBuilder.not(criteriaBuilder.like(target, "%" + value.toLowerCase()));
            }
            case CONTAINS: {
                return criteriaBuilder.like(target, "%" + value.toLowerCase() + "%");
            }
            case NOT_CONTAINS: {
                return criteriaBuilder.notLike(target, "%" + value.toLowerCase() + "%");
            }
            case EQUALS : {
                return criteriaBuilder.equal(target, value.toLowerCase());
            }
            case NOT_EQUALS: {
                return criteriaBuilder.notEqual(target, value.toLowerCase());
            }
            case IN : {
                return target.in(value.split(","));
            }
            case BLANK: {
                return criteriaBuilder.or(
                        criteriaBuilder.equal(target, ""),
                        criteriaBuilder.isNull(table.get(field)));
            }
            case NOT_BLANK : {
                return criteriaBuilder.notEqual(target, "");
            }
            default: {
                return null;
            }
        }
    }
}
