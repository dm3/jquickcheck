package lt.dm3.jquickcheck;

import lt.dm3.jquickcheck.junit4.Generator;

public interface QuickCheckAdapter {

    QuickCheckResult check(Generator<?>[] generators, Invocation invocation);

}
