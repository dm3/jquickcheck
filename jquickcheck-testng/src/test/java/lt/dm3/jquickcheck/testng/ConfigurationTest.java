package lt.dm3.jquickcheck.testng;

import java.util.ArrayList;
import java.util.List;

import lt.dm3.jquickcheck.Property;
import lt.dm3.jquickcheck.QuickCheck;
import lt.dm3.jquickcheck.sample.SampleProvider;

import org.testng.TestNG;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.xml.ClassSuite;
import org.testng.xml.XmlSuite;

public class ConfigurationTest {

    @QuickCheck(provider = SampleProvider.class, useDefaults = true)
    public static class SampleTestCase extends JQuickCheckTestCase {
        @Test(dataProvider = "lol")
        @Property
        public void shouldname(int i) {

        }

        // non-static provider won't work as MethodHelper#invokeMethod tries to find the method on the instance of the
        // javassist proxy.
        @DataProvider
        public static Object[][] lol() {
            return new Object[][] { { 1 } };
        }
    }

    @org.junit.Test
    public void shouldRunTestNGTest() {
        TestNG testNg = new TestNG();
        List<XmlSuite> suites = new ArrayList<XmlSuite>();
        XmlSuite suite = new ClassSuite("sample", new Class[] { SampleTestCase.class });
        suite.setVerbose(Integer.MAX_VALUE);
        suites.add(suite);
        testNg.setXmlSuites(suites);

        testNg.run();
    }
}