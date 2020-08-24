@org.hibernate.annotations.TypeDefs({
        @org.hibernate.annotations.TypeDef(name = "json", typeClass = JsonStringType.class),
        @org.hibernate.annotations.TypeDef(name = "jsonb", typeClass = JsonBinaryType.class),
        @org.hibernate.annotations.TypeDef(name = "JsonType", typeClass = JsonType.class)

})

package au.org.aodn.nrmn.restapi.model.db;

import com.vladmihalcea.hibernate.type.json.*;