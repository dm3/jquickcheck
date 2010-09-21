package lt.dm3.jquickcheck.api;

import java.lang.reflect.Type;

public interface LookupDefaultByType<X> {

    boolean hasDefault(Type t);
    
    X getDefault(Type t);
}
