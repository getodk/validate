package org.opendatakit.validate;

import org.javarosa.core.model.actions.setgeopoint.SetGeopointAction;
import org.javarosa.core.model.actions.setgeopoint.SetGeopointActionHandler;

/**
 * Set an implementation that does nothing.
 */
public final class StubSetGeopointActionHandler extends SetGeopointActionHandler {
    @Override
    public SetGeopointAction getSetGeopointAction() {
        // We'd like to use the default constructor but then the name wouldn't be set because the default constructor
        // has to have an empty body for serialization. Instead, set a null reference and let handle set the target.
        return new StubSetGeopointAction(null);
    }
}