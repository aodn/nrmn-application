package au.org.aodn.nrmn.restapi.data.repository.dynamicQuery;

import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.spatial.dialect.postgis.PostgisPG94Dialect;
import org.hibernate.type.StandardBasicTypes;

/**
 * DB specific implementation as some function is not available in jpa, we use the postgis.PostgisDialect but
 * deprecated, since our db is 9 we use the 94Dialect directly
 */
public class PGDialect extends PostgisPG94Dialect {

    public static final String STRING_AGG_DISTINCT_ASC = "string_agg_distinct_asc";
    public static final String STRING_SPLIT_LIKE = "string_split_contains";
    public static final String STRING_SPLIT_EQUALS = "string_split_equals";

    public PGDialect() {
        super();
        registerFunction(STRING_AGG_DISTINCT_ASC, new SQLFunctionTemplate(
                StandardBasicTypes.STRING,
                "string_agg(distinct ?1, ',' ORDER BY ?2 ASC)"
        ));
        // A special function to handle string where it is concat by delimiter ?2, but we want to verify it by splitting
        // it to individual string.
        registerFunction(STRING_SPLIT_LIKE, new SQLFunctionTemplate(
                StandardBasicTypes.INTEGER,
                "(select count(i) from unnest(string_to_array(?1, ?2)) as i where trim(i) like ?3 || ?4 || ?5)"
        ));
        registerFunction(STRING_SPLIT_EQUALS, new SQLFunctionTemplate(
                StandardBasicTypes.INTEGER,
                "(select count(i) from unnest(string_to_array(?1, ?2)) as i where trim(i) = ?3)"
        ));
    }
}
