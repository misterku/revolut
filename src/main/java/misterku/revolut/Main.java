package misterku.revolut;

import misterku.revolut.web.Handlers;

import static spark.Spark.port;

public class Main {

    public static void main(final String[] args) {
        if (args.length > 0) {
            setCustomPort(args[0]);
        }

        final var handlers = new Handlers();
        handlers.init();
    }

    private static void setCustomPort(String arg) {
        try {
            port(Integer.parseInt(arg));
        } catch (NumberFormatException e) {
            System.err.println("port is invalid");
            System.exit(1);
        }
    }
}
