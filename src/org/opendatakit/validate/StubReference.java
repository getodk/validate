package org.opendatakit.validate;

import org.javarosa.core.reference.Reference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

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
        if (uri.toLowerCase().startsWith("jr://file/")) {
            return getResourceAsFile("fake-itemset.xml").toURI().getPath();
        } else if (uri.toLowerCase().startsWith("jr://file-csv/")) {
            return getResourceAsFile("fake-itemset.csv").toURI().getPath();
        } else {
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

    // getResource() on a file will fail when Validate is run as a jar.
    // getResourceAsStream() is the workaround, but because we need a path,
    // we need to copy the stream to a temp file. The temp file is deleted
    // when the JVM exits
    // https://stackoverflow.com/a/35466006/152938
    private static File getResourceAsFile(String resourcePath) {
        try {
            InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream(resourcePath);
            if (in == null) {
                return null;
            }

            File tempFile = File.createTempFile(String.valueOf(in.hashCode()), ".tmp");
            tempFile.deleteOnExit();

            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            return tempFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
