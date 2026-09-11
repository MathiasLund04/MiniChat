package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ChatClient {
    private static final String HOST = "localhost";
    private static final int PORT = 5000;

    public static void main(String[] args) {
        System.out.println("Forbinder til " + HOST + ":" + PORT + "...");

        try (Socket clientSocket = new Socket(HOST, PORT);
             BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true, StandardCharsets.UTF_8);
             Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {

            System.out.println("Forbindelsen er oprettet.");

            if (args.length > 0 && "--disconnect".equalsIgnoreCase(args[0])) {
                System.out.println("Klienten lukker uden at sende en besked.");
                return;
            }

            while (!login(reader, writer, scanner)) {
                System.out.println("Login mislykkedes. Prøv igen.");
            }

            Thread receiverThread = createReceiverThread(reader);
            receiverThread.start();

            while (true) {
                System.out.print("Skriv target (eller QUIT for at afslutte): ");
                if (!scanner.hasNextLine()) {
                    System.out.println("Input lukket. Afslutter klienten.");
                    break;
                }

                String target = scanner.nextLine().trim();
                if ("QUIT".equalsIgnoreCase(target)) {
                    System.out.println("Klienten afslutter forbindelsen.");
                    break;
                }

                System.out.print("Skriv besked: ");
                if (!scanner.hasNextLine()) {
                    System.out.println("Input lukket. Afslutter klienten.");
                    break;
                }

                String payload = scanner.nextLine();
                writer.println("TEXT|" + target + "|" + payload);
                writer.flush();
                System.out.println("Sendt: TEXT|" + target + "|" + payload);
            }
        } catch (ConnectException exception) {
            System.err.println("Kunne ikke forbinde. Er TcpServer startet på port " + PORT + "?");
        } catch (IOException exception) {
            System.err.println("Klientfejl: " + exception.getMessage());
        }
    }

    private static boolean login(BufferedReader reader, PrintWriter writer, Scanner scanner) throws IOException {
        System.out.print("Login med brugernavn: ");
        if (!scanner.hasNextLine()) {
            System.out.println("Input lukket. Afslutter klienten.");
            return false;
        }

        String username = scanner.nextLine().trim();
        String response = sendAndReadResponse(writer, reader, "LOGIN|" + username + "|");
        return response.contains("|LOGIN|");
    }

    private static String sendAndReadResponse(PrintWriter writer, BufferedReader reader, String request) throws IOException {
        writer.println(request);
        writer.flush();
        System.out.println("Sendt: " + request);

        String response = reader.readLine();
        if (response == null) {
            throw new IOException("Serveren afbrød forbindelsen uden at sende et svar.");
        }

        System.out.println("Svar: " + response);
        return response;
    }

    private static Thread createReceiverThread(BufferedReader reader) {
        Thread receiverThread = new Thread(() -> {
            try {
                String response;
                while ((response = reader.readLine()) != null) {
                    System.out.println("Modtaget: " + response);
                }
            } catch (IOException ex) {
                System.out.println("Forbindelsen til serveren blev lukket: " + ex.getMessage());
            }
        });

        receiverThread.setDaemon(true);
        receiverThread.setName("chat-client-receiver");
        return receiverThread;
    }
}
