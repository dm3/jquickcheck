package lt.dm3.jquickcheck.api;

import java.lang.reflect.Type;

/**
 * Looks up a synthetic generator by type.
 * 
 * @author dm3
 * 
 * @param <GEN>
 *            type of a generator to look up
 */
public interface LookupSynthetic<GEN> {

    boolean hasSynthetic(Type t);

    GEN getSynthetic(Type t);

}
