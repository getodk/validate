package org.opendatakit.validate;

import org.javarosa.core.model.actions.setgeopoint.SetGeopointAction;
import org.javarosa.core.model.actions.setgeopoint.SetGeopointActionHandler;

/**
 * Set an implementation that does nothing.
 */
public final class StubSetGeopointActionHandler extends SetGeopointActionHandler {
    @Override
    public SetGeopointAction getSetGeopointAction() {
        // We'd like to use the default constructor but then the name field defined in Action wouldn't be set. 
        // This is because the default constructor has to have an empty body for serialization. Instead, we've
        // defined a constructor in StubSetGeopointAction that takes in a TreeReference (and sets the name field). 
        // We can pass in null since we don't know the target node at this point and SetGeopointActionHandler's 
        // handle() method will set the target.
        return new StubSetGeopointAction(null);
    }
}
