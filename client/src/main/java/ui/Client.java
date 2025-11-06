package ui;

import chess.ChessGame;
import chess.ChessPiece;
import dataaccess.DataAccessException;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Client {
    public void run(){
        System.out.println(WHITE_KING + "Let's play chess. Yippee!" + BLACK_KING);
        prelogin();
    }

    public void prelogin(){
        Scanner scanner = new Scanner(System.in);
        System.out.print("[LOGGED OUT]  >>> ");
        String command = scanner.nextLine();
        switch (command) {
            case "help" -> {
                System.out.println("register <USERNAME> <PASSWORD> <EMAIL> - to create an account");
                System.out.println("login <USERNAME> <PASSWORD> - to play chess");
                System.out.println("quit - i'll miss you");
                System.out.println("help - ???");
                prelogin();
            }
            case "register" -> {
                System.out.println("Registered");
                postlogin();
            }
            case "login" -> {
                System.out.println("Logged In");
                postlogin();
            }
            case "quit" -> System.out.println("Goodbye my friend");
            default -> {
                System.out.println("That is not valid. Try typing in 'help'");
                prelogin();
            }
        }
    }

    public void postlogin() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("[LOGGED IN]  >>> ");
        String command = scanner.nextLine();
        switch (command) {
            case "help" -> {
                System.out.println("create <Name> - to start a new game");
                System.out.println("list - to list games");
                System.out.println("join <ID> <WHITE|BLACK> - to join a game");
                System.out.println("observe <ID> - to watch a game");
                System.out.println("logout - if you are finished");
                System.out.println("quit - i'll miss you");
                System.out.println("help - ???");
                prelogin();
            }
            case "create" -> {
                System.out.print("Made it");
                postlogin();
            }
            case "list" -> {
                System.out.print("Listed");
                postlogin();
            }
            case "join" -> {
                System.out.println("Joined");
                postlogin();
            }
            case "observe" -> {
                System.out.print("Watching");
                postlogin();
            }
            case "logout" -> {
                System.out.print("Logged out");
                prelogin();
            }
            case "quit" -> System.out.println("Goodbye my friend");
            default -> {
                System.out.println("That is not valid. Try typing in 'help'");
                postlogin();
            }
        }
    }
}
