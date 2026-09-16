package org.example.presentation.console;

public class ClientConsoleView {
    public void showConnectionAttempt(String host, int port) {
        System.out.println("Forbinder til " + host + ":" + port + "...");
    }

    public void showConnected() {
        System.out.println("Forbindelsen er oprettet.");
    }

    public void showDisconnectedWithoutMessage() {
        System.out.println("Klienten lukker uden at sende en besked.");
    }

    public void showLoginRetry() {
        System.out.println("Login mislykkedes. Prøv igen.");
    }

    public void showCommands() {
        System.out.println("\nKommandoer: ROOMS, TEXT, JOIN_ROOM, PRIVATE, QUIT");
    }

    public void promptCommand() {
        System.out.print("Vælg kommando: ");
    }

    public void showInputClosed() {
        System.out.println("Input lukket. Afslutter klienten.");
    }

    public void promptUsername() {
        System.out.print("Login med brugernavn: ");
    }

    public void promptRoomMessage(String currentRoom) {
        System.out.print("Besked (nuværende Rum: " + currentRoom + "): ");
    }

    public void promptRoomName() {
        System.out.print("Rum navn: ");
    }

    public void showMissingRoomName() {
        System.out.println("Rumnavn mangler.");
    }

    public void promptRecipient() {
        System.out.print("Modtager: ");
    }

    public void promptMessage() {
        System.out.print("Besked: ");
    }

    public void showUnknownCommand() {
        System.out.println("Ukendt kommando. Prøv: ROOMS, TEXT, JOIN_ROOM, PRIVATE, QUIT");
    }

    public void showClientClosing() {
        System.out.println("Klienten afslutter forbindelsen.");
    }

    public void showInterruptedShutdown() {
        System.err.println("Klienten blev afbrudt under nedlukning.");
    }

    public void showConnectionError(int port) {
        System.err.println("Kunne ikke forbinde. Er TcpServer startet på port " + port + "?");
    }

    public void showClientError(String message) {
        System.err.println("Klientfejl: " + message);
    }

    public void showReceived(String response) {
        System.out.println();
        System.out.println("Modtaget: " + response);
    }

    public void showServerClosed(String message) {
        System.out.println("Forbindelsen til serveren blev lukket: " + message);
    }
}
