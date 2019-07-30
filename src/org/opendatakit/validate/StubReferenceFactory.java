package org.opendatakit.validate;

import org.javarosa.core.reference.Reference;
import org.javarosa.core.reference.ReferenceFactory;

/**
 * Always provides a reference to the same simple XML or CSV document. This allows forms with external secondary instances to
 * pass validation.
 */
public class StubReferenceFactory implements ReferenceFactory {

    @Override
    public boolean derives(String URI) {
        return true;
    }

    @Override
    public Reference derive(String URI) {
        return new StubReference(URI);
    }

    @Override
    public Reference derive(String URI, String context) {
        return new StubReference(URI);
    }
}
