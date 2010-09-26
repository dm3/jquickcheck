package lt.dm3.jquickcheck.api.impl.lookup;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import lt.dm3.jquickcheck.api.Lookup;
import lt.dm3.jquickcheck.api.LookupDefaultByType;

public class LookupByTypeThenDefault<GEN> implements Lookup<Type, GEN> {

    private final Lookup<Type, GEN> lookupByType;
    private final LookupDefaultByType<GEN> lookupDefault;

    public LookupByTypeThenDefault(Lookup<Type, GEN> lookupByType, LookupDefaultByType<GEN> lookupDefault) {
        this.lookupByType = lookupByType;
        this.lookupDefault = lookupDefault;
    }

    @Override
    public boolean has(Type by) {
        return lookupByType.has(by) || lookupDefault.hasDefault(by);
    }

    @Override
    public Set<GEN> getAll(Type by) {
        Set<GEN> result = lookupByType.getAll(by);
        if (result.isEmpty()) {
            result = new HashSet<GEN>();
            result.add(lookupDefault.getDefault(by));
        }
        return Collections.unmodifiableSet(result);
    }

    @Override
    public GEN get(Type by) {
        try {
            return lookupByType.get(by);
        } catch (Exception e) {
            return lookupDefault.getDefault(by);
        }
    }

}
