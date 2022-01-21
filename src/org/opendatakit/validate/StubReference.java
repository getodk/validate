package org.opendatakit.validate;

import org.javarosa.core.reference.Reference;

import java.io.InputStream;
import java.io.OutputStream;

public class StubReference implements Reference {

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
        return "Can't be found";
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
}
