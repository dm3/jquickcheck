package lt.dm3.jquickcheck.testng;

import org.testng.IObjectFactory;
import org.testng.ITestContext;
import org.testng.annotations.ObjectFactory;

/**
 * A base class to be extended by test cases run by TestNG if you want to avoid specifying object factory in the
 * <code>testng-suite.xml</code> file.
 * 
 * @author dm3
 * 
 */
public class JQuickCheckTestCase {

    @ObjectFactory
    public IObjectFactory create(ITestContext context) {
        return new TestNGObjectFactory();
    }
}
