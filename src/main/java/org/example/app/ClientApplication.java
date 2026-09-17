package org.example.app;

import org.example.presentation.console.ClientConsoleController;
import org.example.presentation.console.ClientConsoleView;
/**
 Starter klientapplikationen og initialiserer controlleren og viewet
    samt setter standardværdier for host og port.
 */
public class ClientApplication {
    private static final String HOST = "localhost";
    private static final int PORT = 5000;

    // starter klienten
    public static void main(String[] args) {
        ClientConsoleController controller = new ClientConsoleController(new ClientConsoleView());
        controller.start(HOST, PORT, args);
    }
}
