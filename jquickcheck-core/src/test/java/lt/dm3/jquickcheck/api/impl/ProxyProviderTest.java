package lt.dm3.jquickcheck.api.impl;

import lt.dm3.jquickcheck.api.QuickCheckException;

import org.junit.Test;

public class ProxyProviderTest {

    @Test(expected = QuickCheckException.class)
    public void shouldFailIfNoValidCandidateFoundForAProvider() {
        new ProxyProvider();
    }
}
