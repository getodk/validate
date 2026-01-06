package org.opendatakit.validate;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestUtils {
    public static Path getPathOf(String filename) throws URISyntaxException {
        return Paths.get(ValidateExternalSecondaryInstancesTest.class.getResource(filename.startsWith("/") ? filename : "/" + filename).toURI());
    }
}
