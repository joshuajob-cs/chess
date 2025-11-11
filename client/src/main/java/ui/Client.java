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
        String[] command = scanner.nextLine().toLowerCase().split("\\s+");
        if (command.length == 0){
            prelogFail();
            return;
        }
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
            return;
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
        String[] command = scanner.nextLine().toLowerCase().split("\\s+");
        if (command.length == 0){
            postlogFail();
            return;
        }
        switch (command[0]) {
            case "help" -> helpLogout();
            case "create" -> create();
            case "list" -> list();
            case "join" -> join();
            case "observe" -> observe();
            case "logout" -> logout();
            case "quit" -> System.out.println("Goodbye my friend");
            default -> postlogFail();
        }
    }

    private void helpLogout(){
        System.out.println(
                """
                create <Name> - to start a new game
                list - to list games
                join <ID> <WHITE|BLACK> - to join a game
                observe <ID> - to watch a game
                logout - if you are finished
                quit - i'll miss you
                help - ???
                """);
        postlogin();
    }

    private void create(){
        System.out.println("Made it");
        postlogin();
    }

    private void list(){
        System.out.println("Listed");
        postlogin();
    }

    private void join(){
        System.out.println("Joined");
        postlogin();
    }

    private void observe(){
        System.out.println("We are always watching");
        postlogin();
    }

    private void logout(){
        System.out.println("logged Out");
        prelogin();
    }

    private void postlogFail(){
        System.out.println("That is not valid. Try typing in 'help'");
        postlogin();
    }
}
