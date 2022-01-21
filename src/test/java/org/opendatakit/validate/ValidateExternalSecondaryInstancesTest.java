package org.opendatakit.validate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.isEmptyString;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;

public class ValidateExternalSecondaryInstancesTest {
    @Test
    public void supportsXlsformsDefaultValueAndLabelRefs_inItemsets_usingXMLExternalSecondaryInstance() throws URISyntaxException {
        final Path path = getPathOf("external_secondary_instance_xml.xml");
        final FormValidator validator = new FormValidator();

        Output output = Output.runAndGet(() -> validator.validate(path.toString()));
        assertThat(output.err, isEmptyString());
        assertThat(output.std, containsString("Xform is valid"));
    }

    @Test
    public void supports_xlsforms_value_and_label_refs_in_itemsets_using_secondary_external_instances_csv() throws URISyntaxException {
        final Path path = getPathOf("external_secondary_instance_csv.xml");
        final FormValidator validator = new FormValidator();

        Output output = Output.runAndGet(() -> validator.validate(path.toString()));
        assertThat(output.err, isEmptyString());
        assertThat(output.std, containsString("Xform is valid"));
    }

    @Test
    public void supportsCustomValueAndLabelRefs_inItemsets_usingExternalSecondaryInstance() throws URISyntaxException {
        final Path path = getPathOf("external_secondary_instance_custom_refs.xml");
        final FormValidator validator = new FormValidator();

        Output output = Output.runAndGet(() -> validator.validate(path.toString()));
        assertThat(output.err, isEmptyString());
        assertThat(output.std, containsString("Xform is valid"));
    }

    private Path getPathOf(String filename) throws URISyntaxException {
        return Paths.get(ValidateExternalSecondaryInstancesTest.class.getResource(filename.startsWith("/") ? filename : "/" + filename).toURI());
    }

    static class Output {
        private final String std;
        private final String err;

        Output(String std, String err) {
            this.std = std;
            this.err = err;
        }

        static Output runAndGet(Runnable runnable) {
            PrintStream outBackup = System.out;
            ByteArrayOutputStream stdBaos = new ByteArrayOutputStream();
            PrintStream stdPs = new PrintStream(stdBaos);
            System.setOut(stdPs);

            PrintStream errBackup = System.err;
            ByteArrayOutputStream errBaos = new ByteArrayOutputStream();
            PrintStream errPs = new PrintStream(errBaos);
            System.setErr(errPs);

            runnable.run();

            stdPs.flush();
            String std = stdBaos.toString();
            System.setOut(outBackup);
            System.out.print(std);

            errPs.flush();
            String err = errBaos.toString();
            System.setErr(errBackup);
            System.err.print(err);

            return new Output(std, err);
        }
    }

}
