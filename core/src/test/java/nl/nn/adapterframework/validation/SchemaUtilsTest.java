package nl.nn.adapterframework.validation;

import edu.emory.mathcs.backport.java.util.Collections;
import org.junit.Test;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Some methods are tested {@link XSDTest}
 * @author Michiel Meeuwissen

 */
public class SchemaUtilsTest {

	@Test
	public void getXsds() throws IOException, XMLStreamException {
		Set<XSD> xsds = SchemaUtils.getXsds("http://wub2nn.nn.nl/FindIntermediary " +
				"WsdlTest/FindIntermediary/xsd/XSD_FindIntermediary_v1.1_r1.0.xsd", Collections.<String>emptyList(), true, false);
		assertEquals(1, xsds.size());
		XSD xsd = xsds.iterator().next();
		assertEquals("http://wub2nn.nn.nl/FindIntermediary", xsd.getNamespace());
		assertNotNull(xsd.getUrl().openStream());

	}

}
