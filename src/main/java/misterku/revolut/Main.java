package misterku.revolut;

import misterku.revolut.web.Handlers;

public class Main {

    public static void main(final String[] args) {
        int port = 8080;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        final var handlers = new Handlers(port);
    }

}
