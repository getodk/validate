package org.opendatakit.validate;

import org.javarosa.core.model.actions.setgeopoint.SetGeopointAction;
import org.javarosa.core.model.instance.TreeReference;

/**
 * An odk:setgeopoint implementation that does nothing when triggered.
 */
public final class StubSetGeopointAction extends SetGeopointAction {
    public StubSetGeopointAction() {
        // empty body for serialization
    }

    StubSetGeopointAction(TreeReference targetReference) {
        super(targetReference);
    }

    @Override
    public void requestLocationUpdates() {

    }
}
