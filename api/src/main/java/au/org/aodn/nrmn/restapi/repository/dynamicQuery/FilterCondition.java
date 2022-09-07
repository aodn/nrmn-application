package au.org.aodn.nrmn.restapi.repository.dynamicQuery;

import au.org.aodn.nrmn.restapi.controller.transform.Field;
import au.org.aodn.nrmn.restapi.controller.transform.Filter;
import au.org.aodn.nrmn.restapi.controller.transform.Sorter;
import au.org.aodn.nrmn.restapi.model.db.Survey;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static au.org.aodn.nrmn.restapi.repository.dynamicQuery.PGDialect.STRING_SPLIT_EQUALS;
import static au.org.aodn.nrmn.restapi.repository.dynamicQuery.PGDialect.STRING_SPLIT_LIKE;

public abstract class FilterCondition<T> {

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
    public static final String NOT_IN = "notIn";

    protected Specification<T> filtersSpec = null;
    protected Specification<T> sortingSpec = null;

    interface DBField {

        String getDBFieldName();

        default Boolean isRequireSplitString() { return Boolean.FALSE; }

        default Expression<String> getDBField(From<?,?> table, CriteriaBuilder criteriaBuilder) {
            // Need to handle date field, as we cannot assume date format always yyyy-MM-dd
            if(table.get(getDBFieldName()).getJavaType() == Date.class) {
                // DB specific function call !!!!
                return criteriaBuilder.function("to_char", String.class, table.get(getDBFieldName()), criteriaBuilder.literal("yyyy-MM-dd"));
            }
            else{
                return criteriaBuilder.lower(table.get(getDBFieldName()).as(String.class));
            }
        }
    }

    protected Specification<T> build() {
        if(filtersSpec == null) {
            // We need to do join to get the relationship even we do not have any search criteria, if we have
            // search criteria, table already join so no need to do it here again
            return sortingSpec;
        }
        else {
            return filtersSpec.and(sortingSpec);
        }
    }

    protected abstract FilterCondition<T> applySort(List<Sorter> sort);

    protected abstract FilterCondition<T> applyFilters(List<Filter> filters);

    protected <E extends Enum<E> & FilterCondition.DBField> Order getItemOrdering(From<?,?> from, CriteriaBuilder criteriaBuilder, Sorter sort, Class<E> clazz) {
        Expression<Survey> e = from.get(FilterCondition.getFieldEnum(sort.getFieldName(), clazz).getDBFieldName());
        return (sort.isAsc()  ? criteriaBuilder.asc(e) : criteriaBuilder.desc(e));
    }

    public static <T> List<T> parse(ObjectMapper objectMapper, String values, Class<T[]> clazz) throws JsonProcessingException {
        return values != null ? Arrays.stream((objectMapper.readValue(values, clazz))).collect(Collectors.toList()) : null;
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

    protected Predicate getSimpleFieldSpecification(From<?,?> table, CriteriaBuilder criteriaBuilder, DBField field,  boolean isAnd, Filter filter1, Filter filter2) {
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

    protected Predicate getSimpleFieldSpecification(From<?,?> table, CriteriaBuilder criteriaBuilder, DBField field, String value, String operation) {

        Expression<String> target = field.getDBField(table, criteriaBuilder);

        // Single condition
        switch(operation) {

            case STARTS_WITH: {
                if(field.isRequireSplitString()) {
                    return criteriaBuilder.greaterThan(
                            criteriaBuilder.function(
                                    STRING_SPLIT_LIKE,
                                    Integer.class,
                                    target,
                                    criteriaBuilder.literal(","),
                                    criteriaBuilder.literal(""),
                                    criteriaBuilder.literal(value.toLowerCase()),
                                    criteriaBuilder.literal("%")),
                            0);
                }
                else {
                    return criteriaBuilder.like(target, value.toLowerCase() + "%");
                }
            }
            case ENDS_WITH: {
                if(field.isRequireSplitString()) {
                    return criteriaBuilder.greaterThan(
                            criteriaBuilder.function(
                                    STRING_SPLIT_LIKE,
                                    Integer.class,
                                    target,
                                    criteriaBuilder.literal(","),
                                    criteriaBuilder.literal("%"),
                                    criteriaBuilder.literal(value.toLowerCase()),
                                    criteriaBuilder.literal("")),
                            0);

                }
                else {
                    return criteriaBuilder.like(target, "%" + value.toLowerCase());
                }
            }
            case NOT_ENDS_WITH: {
                if(field.isRequireSplitString()) {
                    return criteriaBuilder.equal(
                            criteriaBuilder.function(
                                    STRING_SPLIT_LIKE,
                                    Integer.class,
                                    target,
                                    criteriaBuilder.literal(","),
                                    criteriaBuilder.literal("%"),
                                    criteriaBuilder.literal(value.toLowerCase()),
                                    criteriaBuilder.literal("")),
                            0);
                }
                else {
                    return criteriaBuilder.not(criteriaBuilder.like(target, "%" + value.toLowerCase()));
                }
            }
            case CONTAINS: {
                if(field.isRequireSplitString()) {
                    return criteriaBuilder.greaterThan(
                            criteriaBuilder.function(
                                    STRING_SPLIT_LIKE,
                                    Integer.class,
                                    target,
                                    criteriaBuilder.literal(","),
                                    criteriaBuilder.literal("%"),
                                    criteriaBuilder.literal(value.toLowerCase()),
                                    criteriaBuilder.literal("%")),
                            0);
                }
                else {
                    return criteriaBuilder.like(target, "%" + value.toLowerCase() + "%");
                }
            }
            case NOT_CONTAINS: {
                if(field.isRequireSplitString()) {
                    return criteriaBuilder.equal(
                            criteriaBuilder.function(
                                    STRING_SPLIT_LIKE,
                                    Integer.class,
                                    target,
                                    criteriaBuilder.literal(","),
                                    criteriaBuilder.literal("%"),
                                    criteriaBuilder.literal(value.toLowerCase()),
                                    criteriaBuilder.literal("%")),
                            0);
                }
                else {
                    return criteriaBuilder.notLike(target, "%" + value.toLowerCase() + "%");
                }
            }
            case EQUALS : {
                if(field.isRequireSplitString()) {
                    return criteriaBuilder.greaterThan(
                            criteriaBuilder.function(
                                    STRING_SPLIT_EQUALS,
                                    Integer.class,
                                    target,
                                    criteriaBuilder.literal(","),
                                    criteriaBuilder.literal(value.toLowerCase())),
                            0);
                }
                else {
                    return criteriaBuilder.equal(target, value.toLowerCase());
                }
            }
            case NOT_EQUALS: {
                if(field.isRequireSplitString()) {
                    return criteriaBuilder.equal(
                            criteriaBuilder.function(
                                    STRING_SPLIT_EQUALS,
                                    Integer.class,
                                    target,
                                    criteriaBuilder.literal(","),
                                    criteriaBuilder.literal(value.toLowerCase())),
                            0);
                }
                else {
                    return criteriaBuilder.notEqual(target, value.toLowerCase());
                }
            }
            case IN : {
                return target.in((Object[])value.split(","));
            }
            case NOT_IN : {
                return criteriaBuilder.not(target.in((Object[])value.split(",")));
            }
            case BLANK: {
                return criteriaBuilder.or(
                        criteriaBuilder.equal(target, ""),
                        criteriaBuilder.isNull(table.get(field.getDBFieldName())));
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
