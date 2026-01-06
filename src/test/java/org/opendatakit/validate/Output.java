package org.opendatakit.validate;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

class Output {
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

    public String getStd() {
        return std;
    }

    public String getErr() {
        return err;
    }
}