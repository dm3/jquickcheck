package lt.dm3.jquickcheck.api.impl;

import java.lang.reflect.Type;

import lt.dm3.jquickcheck.internal.Primitives;

final class DefaultGeneratorId {

    private static final Type ANY = new Type() {
            };

    private final Type type;
    private final String name;

    public DefaultGeneratorId(String name, Type type) {
        this.name = name;
        this.type = type == null ? ANY : type;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!DefaultGeneratorId.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        DefaultGeneratorId other = (DefaultGeneratorId) obj;
        return this.name.equals(other.name) && Primitives.equalIgnoreWrapping(type, other.type);
    }

    @Override
    public int hashCode() {
        return 31 * this.name.hashCode() + 17 * this.type.hashCode();
    }

    @Override
    public String toString() {
        return "Generator id for name: " + name + ", type: " + type;
    }
}
