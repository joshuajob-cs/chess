package ui;

import chess.ChessGame;
import chess.ChessPiece;
import dataaccess.DataAccessException;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Client {
    public void run(){
        System.out.println(WHITE_KING + "Let's play chess. Yippee!" + BLACK_KING);
        prelogin();
    }

    private void prelogin(){
        Scanner scanner = new Scanner(System.in);
        System.out.print("[LOGGED OUT]  >>> ");
        String[] command = scanner.nextLine().toLowerCase().split(" ");
        String[] parameters = Arrays.copyOfRange(command, 1, command.length);
        switch (command[0]) {
            case "help" -> helpLogin();
            case "register" -> register(parameters);
            case "login" -> login();
            case "quit" -> System.out.println("Goodbye my friend");
            default -> prelogFail();
        }
    }

    private void helpLogin(){
        System.out.println(
        """
        register <USERNAME> <PASSWORD> <EMAIL> - to create an account
        login <USERNAME> <PASSWORD> - to play chess
        quit - i'll miss you
        help - ???
        """);
        prelogin();
    }

    private void register(String[] params){
        System.out.println("Registered!");
        if (params.length != 3){
            System.out.print(params.length + " arguments for register. ");
            prelogFail();
        }
        String username = params[0];
        String password = params[1];
        String email = params[2];
        postlogin();
    }

    private void login() {
        System.out.println("Logged In");
        postlogin();
    }

    private void prelogFail(){
        System.out.println("That is not valid. Try typing in 'help'");
        prelogin();
    }

    private void postlogin() {
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
            case "logout" -> {
                System.out.print("Logged out");
                prelogin();
            }
            case "quit" -> System.out.println("Goodbye my friend");
            default -> {
                if (command.startsWith("join ")){
                    System.out.println("Joined");
                    postlogin();
                } else if(command.startsWith("observe ")){
                    System.out.print("Watching");
                    postlogin();
                } else {
                    postlogFail();
                }
            }
        }
    }

    private void postlogFail(){
        System.out.println("That is not valid. Try typing in 'help'");
        postlogin();
    }
}
