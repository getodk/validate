package org.opendatakit.validate;

import org.javarosa.core.reference.Reference;

import java.io.InputStream;
import java.io.OutputStream;

import java.net.URISyntaxException;

/**
 * Provides the local URI of a simple XML document. This allows forms with external secondary instances to
 * pass validation.
 *
 * The fake instance has the following structure:
 *
 * <pre>{@code
 * <root>
 *     <item>
 *         <name>an-item</name>
 *         <label>An Item</label>
 *     </item>
 * </root>
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
            return getClass().getClassLoader().getResource("fake-itemset.xml").toURI().getPath();
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
