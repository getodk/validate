package org.opendatakit.validate;

public interface ErrorListener {

    void error(Object err);

    void error(Object err, Throwable t);

    void info(Object msg);

    ErrorListener DEFAULT_ERROR_LISTENER = new ErrorListener() {
        public void error(Object err) {
            System.err.println(err);
        }

        public void error(Object err, Throwable t) {
            System.err.println(err);
            t.printStackTrace();
        }

        public void info(Object msg) {
            System.out.println(msg);
        }

    };
}
