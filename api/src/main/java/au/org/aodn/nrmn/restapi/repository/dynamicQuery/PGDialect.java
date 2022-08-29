package au.org.aodn.nrmn.restapi.repository.dynamicQuery;

import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.spatial.dialect.postgis.PostgisPG94Dialect;
import org.hibernate.type.StandardBasicTypes;

/**
 * DB specific implementation as some function is not available in jpa, we use the postgis.PostgisDialect but
 * deprecated, since our db is 9 we use the 94Dialect directly
 */
public class PGDialect extends PostgisPG94Dialect {

    public static final String STRING_AGG_DISTINCT_ASC = "string_agg_distinct_asc";
    public static final String STRING_SPLIT_CONTAINS = "string_split_contains";

    public PGDialect() {
        super();
        registerFunction(STRING_AGG_DISTINCT_ASC, new SQLFunctionTemplate(
                StandardBasicTypes.STRING,
                "string_agg(distinct ?1, ',' ORDER BY ?2 ASC)"
        ));
        registerFunction(STRING_SPLIT_CONTAINS, new SQLFunctionTemplate(
                StandardBasicTypes.INTEGER,
                "(select count(i) from unnest(string_to_array(?1, ?2)) as i where trim(i) like '%' || ?3 || '%')"
        ));
    }
}
