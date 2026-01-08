package org.opendatakit.validate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.isEmptyString;
import static org.opendatakit.validate.TestUtils.getPathOf;

import java.net.URISyntaxException;
import java.nio.file.Path;
import org.junit.Test;

public class ValidateExternalSecondaryInstancesTest {
    @Test
    public void supportsXlsformsDefaultValueAndLabelRefs_inItemsets_usingXMLExternalSecondaryInstance() throws URISyntaxException {
        final Path path = getPathOf("xml_external_instance.xml");
        final FormValidator validator = new FormValidator();

        Output output = Output.runAndGet(() -> validator.validate(path.toString()));
        assertThat(output.getErr(), isEmptyString());
        assertThat(output.getStd(), containsString("Xform is valid"));
    }

    @Test
    public void supportsXlsformsDefaultValueAndLabelRefs_inItemsets_usingCsvSecondaryExternalInstance() throws URISyntaxException {
        final Path path = getPathOf("csv_external_instance.xml");
        final FormValidator validator = new FormValidator();

        Output output = Output.runAndGet(() -> validator.validate(path.toString()));
        assertThat(output.getErr(), isEmptyString());
        assertThat(output.getStd(), containsString("Xform is valid"));
    }

    @Test
    public void supportsCustomValueAndLabelRefs_inItemsets_usingExternalSecondaryInstance() throws URISyntaxException {
        final Path path = getPathOf("external_instance_custom_refs.xml");
        final FormValidator validator = new FormValidator();

        Output output = Output.runAndGet(() -> validator.validate(path.toString()));
        assertThat(output.getErr(), isEmptyString());
        assertThat(output.getStd(), containsString("Xform is valid"));
    }
}
