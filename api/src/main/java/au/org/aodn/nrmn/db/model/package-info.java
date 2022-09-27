@org.hibernate.annotations.TypeDefs({
        @org.hibernate.annotations.TypeDef(name = "json", typeClass = JsonStringType.class),
        @org.hibernate.annotations.TypeDef(name = "jsonb", typeClass = JsonBinaryType.class),
        @org.hibernate.annotations.TypeDef(name = "list-array", typeClass = ListArrayType.class),
})

package au.org.aodn.nrmn.db.model;

import com.vladmihalcea.hibernate.type.array.ListArrayType;
import com.vladmihalcea.hibernate.type.json.*;
