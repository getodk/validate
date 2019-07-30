package org.opendatakit.validate;

import org.javarosa.core.reference.Reference;

import java.io.InputStream;
import java.io.OutputStream;

import java.net.URISyntaxException;

/**
 * Provides the local URI of a simple XML or CSV document. This allows forms with external secondary instances to
 * pass validation.
 *
 * The fake XML instance has the following structure:
 *
 * <pre>{@code
 * <root>
 *     <item>
 *         <name>an-xml-item</name>
 *         <label>An XML Item</label>
 *     </item>
 * </root>
 *}</pre>
 *
 * The fake CSV instance has the following structure:
 *
 * <pre>{@code
 * name,label
 * a-csv-item, "A CSV Item"
 *}</pre>
 *
 * This means only itemset declarations that define {@code name} as the node name referring to the select underlying
 * value and {@code label} as the node name referring to the label will pass validation.
 *
 * Passes validation:
 * <pre>{@code
 *  <itemset nodeset="instance('lgas')/root/item[state=/nigeria_wards_external/state]">
 *    <value ref="name"/>
 *    <label ref="label"/>
 *  </itemset>
 * }</pre>
 *
 * Fails validation:
 * <pre>{@code
 *  <itemset nodeset="instance('lgas')/root/item[state=/nigeria_wards_external/state]">
 *    <value ref="value"/>
 *    <label ref="something-else"/>
 *  </itemset>
 * }</pre>
 *
 *
 *
 */
public class StubReference implements Reference {

    private String uri;

    public StubReference(String URI) {
        uri = URI;
    }

    @Override
    public boolean doesBinaryExist() {
        return true;
    }

    @Override
    public InputStream getStream() {
        return null;
    }

    @Override
    public String getURI() {
        return null;
    }

    @Override
    public String getLocalURI() {
        try {
            if (uri.toLowerCase().startsWith("jr://file/")) {
                return getClass().getClassLoader().getResource("fake-itemset.xml").toURI().getPath();
            } else if (uri.toLowerCase().startsWith("jr://file-csv/")) {
                return getClass().getClassLoader().getResource("fake-itemset.csv").toURI().getPath();
            } else {
                return null;
            }
        } catch (URISyntaxException e) {
            return null;
        }
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public OutputStream getOutputStream() {
        return null;
    }

    @Override
    public void remove() {

    }

    @Override
    public Reference[] probeAlternativeReferences() {
        return new Reference[0];
    }
}
