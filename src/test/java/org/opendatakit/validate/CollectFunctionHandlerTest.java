package org.opendatakit.validate;

import org.junit.Test;

import java.net.URISyntaxException;
import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.isEmptyString;

public class CollectFunctionHandlerTest {

    @Test
    public void acceptsIntersects() throws URISyntaxException {
        final Path path = TestUtils.getPathOf("intersects_function.xml");
        final FormValidator validator = new FormValidator();

        Output output = Output.runAndGet(() -> validator.validate(path.toString()));
        assertThat(output.getErr(), isEmptyString());
        assertThat(output.getStd(), containsString("Xform is valid"));
    }
}
