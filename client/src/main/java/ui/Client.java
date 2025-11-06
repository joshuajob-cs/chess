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

    private void prelogin(){
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
            case "quit" -> System.out.println("Goodbye my friend");
            default -> {
                if (command.startsWith("register ")){
                    System.out.println("Registered!");
                    try {
                        command = command.substring(9);
                        String username = command.substring(0, command.indexOf(" "));
                        command = command.substring(command.indexOf(" ") + 1);
                        String password = command.substring(0, command.indexOf(" "));
                        command = command.substring(command.indexOf(" ") + 1);
                        if (!command.contains(" ")) {
                            String email = command;
                            postlogin();
                        }
                        else{
                            System.out.print("You typed in more than was needed to register. ");
                            prelogFail();
                        }
                    } catch (StringIndexOutOfBoundsException e) {
                        System.out.print("Not enough information to register. ");
                        prelogFail();
                    }
                } else if (command.startsWith("login ")) {
                    System.out.println("Logged In");
                    postlogin();
                } else {
                    prelogFail();
                }
            }
        }
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
