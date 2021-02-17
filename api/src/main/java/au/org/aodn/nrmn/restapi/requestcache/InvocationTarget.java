package au.org.aodn.nrmn.restapi.requestcache;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Arrays;

class InvocationTarget {

    private static final String TO_STRING_TEMPLATE = "%s.%s(%s)";

    private final Class targetClass;
    private final String targetMethod;
    private final Object[] args;

    InvocationTarget(Class targetClass, String targetMethod, Object[] args) {
        this.targetClass = targetClass;
        this.targetMethod = targetMethod;
        this.args = args;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return String.format(TO_STRING_TEMPLATE, targetClass.getName(), targetMethod, Arrays.toString(args));
    }
}
