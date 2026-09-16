package org.example.app;

import org.example.presentation.console.ClientConsoleController;
import org.example.presentation.console.ClientConsoleView;

public class ClientApplication {
    private static final String HOST = "localhost";
    private static final int PORT = 5000;

    public static void main(String[] args) {
        ClientConsoleController controller = new ClientConsoleController(new ClientConsoleView());
        controller.start(HOST, PORT, args);
    }
}
