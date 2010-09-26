package lt.dm3.jquickcheck.api.impl.lookup;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lt.dm3.jquickcheck.api.Lookup;

public abstract class AbstractLookup<C, By, GEN> implements Lookup<By, GEN> {
    private final Map<By, Set<GEN>> xToGenerator = new HashMap<By, Set<GEN>>();

    protected void put(C container) {
        By key = keyByContainer(container);
        Set<GEN> existing = xToGenerator.get(key);
        if (existing == null) {
            existing = new HashSet<GEN>();
        }
        existing.add(generatorByContainer(container));
        putTo(xToGenerator, container, existing);
    }

    protected void putTo(Map<By, Set<GEN>> values, C container, Set<GEN> existing) {
        xToGenerator.put(keyByContainer(container), existing);
    }

    protected abstract GEN generatorByContainer(C container);

    protected abstract By keyByContainer(C container);

    private boolean hasOne(By by) {
        if (xToGenerator.containsKey(by)) {
            return xToGenerator.get(by).size() == 1;
        }
        return false;
    }

    @Override
    public Set<GEN> getAll(By by) {
        Set<GEN> existing = xToGenerator.get(by);
        return existing == null ? Collections.<GEN> emptySet() : existing;
    }

    @Override
    public boolean has(By by) {
        return xToGenerator.containsKey(by) ? !xToGenerator.get(by).isEmpty() : false;
    }

    @Override
    public GEN get(By by) {
        if (!hasOne(by)) {
            throw new IllegalArgumentException("No generator (or several) found for " + by);
        }
        return xToGenerator.get(by).iterator().next();
    }

}
