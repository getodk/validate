package org.opendatakit.validate;
import org.javarosa.core.model.FormDef;
import org.javarosa.test.Scenario;

import org.javarosa.xform.parse.XFormParser;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.isEmptyString;
import static org.javarosa.test.BindBuilderXFormsElement.bind;
import static org.javarosa.test.XFormsElement.body;
import static org.javarosa.test.XFormsElement.head;
import static org.javarosa.test.XFormsElement.html;
import static org.javarosa.test.XFormsElement.input;
import static org.javarosa.test.XFormsElement.mainInstance;
import static org.javarosa.test.XFormsElement.model;
import static org.javarosa.test.XFormsElement.t;
import static org.javarosa.test.XFormsElement.title;

public class CollectFunctionHandlersTest {

    @Test
    public void acceptsPulldata() throws IOException, XFormParser.ParseException {
        FormDef fd = Scenario.createFormDef("Pulldata", html(
            head(
                title("Form with pulldata"),
                model(
                    mainInstance(t("data id=\"pulldata\"",
                        t("q1"),
                        t("calc")
                    )),
                    // Below is an invalid pulldata call. The handler doesn't currently validate args but could.
                    bind("/data/calc").type("string").calculate("pulldata(/data/q1)")
                )
            ),
            body(
                input("/data/q1")
            )
        ));

        final FormValidator validator = new FormValidator();
        Output output = Output.runAndGet(() -> validator.validate(fd));

        assertThat(output.getErr(), isEmptyString());
        assertThat(output.getStd(), containsString("Xform parsing completed"));
        assertThat(output.getStd(), containsString("Xform is valid"));
    }

    @Test
    public void rejectsPuldata() throws IOException, XFormParser.ParseException {
        FormDef fd = Scenario.createFormDef("Puldata with typo", html(
            head(
                title("Form with function name typo"),
                model(
                    mainInstance(t("data id=\"puldata\"",
                        t("q1"),
                        t("calc")
                    )),
                    bind("/data/calc").type("string").calculate("puldata(/data/q1)")
                )
            ),
            body(
                input("/data/q1")
            )
        ));

        final FormValidator validator = new FormValidator();
        Output output = Output.runAndGet(() -> validator.validate(fd));

        assertThat(output.getErr(), containsString("cannot handle function 'puldata'"));
        assertThat(output.getErr(), containsString("Xform is invalid"));
        assertThat(output.getStd(), isEmptyString());
    }

    @Test
    public void acceptsIntersects() throws IOException, XFormParser.ParseException {
        FormDef fd = Scenario.createFormDef("Intersects", html(
            head(
                title("Form with intersects"),
                model(
                    mainInstance(t("data id=\"intersects\"",
                        t("q1"),
                        t("calc")
                    )),
                    bind("/data/calc").type("string").calculate("intersects(/data/q1)")
                )
            ),
            body(
                input("/data/q1")
            )
        ));

        final FormValidator validator = new FormValidator();
        Output output = Output.runAndGet(() -> validator.validate(fd));

        assertThat(output.getErr(), isEmptyString());
        assertThat(output.getStd(), containsString("Xform parsing completed"));
        assertThat(output.getStd(), containsString("Xform is valid"));
    }
}
