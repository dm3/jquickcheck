package lt.dm3.jquickcheck;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * G is used to annotate
 * <ul>
 * <li>
 * Property arguments, like
 * 
 * <pre>
 * &#064;Property
 * public boolean prop1(@G(gen="intGen", shrink="intShrink") int x) {
 *     ...
 * }
 * </pre>
 * 
 * Annotation on a property argument will require the <tt>x</tt> parameter be supplied from a generator called
 * <tt>intGen</tt>.
 * <p>
 * The result of the generator will be shrinked with the shrink called <tt>intShrink</tt>. If the generator or the
 * shrink with the given name aren't resolved - the property will fail with an exception.
 * </p>
 * <p>
 * If the annotation contains neither <tt>gen</tt> nor <tt>shrink</tt> parameters, it's a noop.
 * </p>
 * <li>
 * Generators assigned to fields, like
 * 
 * <pre>
 * &#064;G(gen = &quot;intGen&quot;, shrink = &quot;intShrink&quot;)
 * private final Generator&lt;Integer&gt; gen = new IntegerGenerator();
 * </pre>
 * 
 * Annotation on a field containing a generator will rename the generator to the supplied <tt>gen</tt> parameter value.
 * By default the name of such generator will be equal to the fields' name.
 * <p>
 * <tt>shrink</tt> parameter determines the default shrink which will have to exist in order for this generator to be
 * used in a property.
 * </p>
 * <p>
 * If the annotation contains neither <tt>gen</tt> nor <tt>shrink</tt> parameters, it's a noop. Generators contained in
 * the fields of the test case are always registered unless marked as {@link Disabled}.
 * </p>
 * </li>
 * <li>
 * Methods which return generators, like
 * 
 * <pre>
 * &#064;G(gen = &quot;intGen&quot;, shrink = &quot;intShrink&quot;)
 * public Generator&lt;Integer&gt; makeIntGen() {
 *     return new IntegerGenerator();
 * }
 * </pre>
 * 
 * Annotation on a method returning a generator will rename the generator to the supplied <tt>gen</tt> parameter value.
 * By default the name of such generator will be equal to the methods' name.
 * <p>
 * <tt>shrink</tt> parameter determines the default shrink which will have to exist in order for this generator to be
 * used in a property.
 * </p>
 * <p>
 * If the annotation contains neither <tt>gen</tt> nor <tt>shrink</tt> parameters, the name of the generator returned by
 * the method will be equal to the name of the method. Shrink will be chosen from the set of default shrinks based on
 * the generators' type.
 * </p>
 * </li>
 * <li>
 * Methods which return implicit generators, like
 * 
 * <pre>
 * &#064;G(gen = &quot;stringGen&quot;, shrink = &quot;stringShrink&quot;)
 * public String makeStringGen(Integer value) {
 *     return value.toString();
 * }
 * </pre>
 * 
 * Annotation on a method returning an implicit generator will rename the generator to the supplied <tt>gen</tt>
 * parameter value. By default the name of such generator will be equal to the methods' name.
 * <p>
 * <tt>shrink</tt> parameter determines the default shrink which will have to exist in order for this generator to be
 * used in a property.
 * <p>
 * If the annotation contains neither <tt>gen</tt> nor <tt>shrink</tt> parameters, the name of the generator returned by
 * the method will be equal to the name of the method. Shrink will be chosen from the set of default shrinks based on
 * the generators' type.
 * </p>
 * </li>
 * </ul>
 * 
 * @author dm3
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER, ElementType.METHOD })
public @interface G {
    String gen() default "";
}
