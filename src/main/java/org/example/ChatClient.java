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

            System.out.print("Login med brugernavn: ");
            if (!scanner.hasNextLine()) {
                System.out.println("Input lukket. Afslutter klienten.");
                return;
            }

            String username = scanner.nextLine().trim();
            sendAndPrintResponse(writer, reader, "LOGIN|" + username + "|");

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
                sendAndPrintResponse(writer, reader, "TEXT|" + target + "|" + payload);
            }
        } catch (ConnectException exception) {
            System.err.println("Kunne ikke forbinde. Er TcpServer startet på port " + PORT + "?");
        } catch (IOException exception) {
            System.err.println("Klientfejl: " + exception.getMessage());
        }
    }

    private static void sendAndPrintResponse(PrintWriter writer, BufferedReader reader, String request) throws IOException {
        writer.println(request);
        System.out.println("Sendt: " + request);

        String response = reader.readLine();
        if (response == null) {
            throw new IOException("Serveren afbrød forbindelsen uden at sende et svar.");
        }

        System.out.println("Svar: " + response);
    }
}
