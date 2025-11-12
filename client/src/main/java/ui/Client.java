package ui;

import dataaccess.DataAccessException;
import model.LoginData;
import model.UserData;
import server.ServerFacade;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Client {
    private final ServerFacade server = new ServerFacade("http://localhost:8080/");

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
        try {
            switch (command[0]) {
                case "help" -> helpLogin();
                case "register" -> register(parameters);
                case "login" -> login(parameters);
                case "quit" -> System.exit(0);
                default -> prelogFail();
            }
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
            prelogin();
        }
    }

    private void helpLogin() throws DataAccessException{
        System.out.println(
        """
        register <USERNAME> <PASSWORD> <EMAIL> - to create an account
        login <USERNAME> <PASSWORD> - to play chess
        quit - i'll miss you
        help - ???
        """);
        prelogin();
    }

    private void register(String[] params) throws DataAccessException{
        System.out.println("Registered!");
        if (params.length != 3){
            System.out.print(params.length + " arguments for register. ");
            prelogFail();
            return;
        }
        server.register(new UserData(params[0], params[1], params[2]));
        postlogin();
    }

    private void login(String[] params) throws DataAccessException{
        System.out.println("Logged In");
        if (params.length != 2){
            System.out.print(params.length + " arguments for register. ");
            prelogFail();
            return;
        }
        server.login(new LoginData(params[0], params[1]));
        postlogin();
    }

    private void prelogFail(){
        System.out.println("That is not valid. Try typing in 'help'");
        prelogin();
    }

    private void postlogin()  throws DataAccessException{
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
            case "quit" -> System.exit(0);
            default -> postlogFail();
        }
    }

    private void helpLogout() throws DataAccessException{
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

    private void create() throws DataAccessException{
        System.out.println("Made it");
        postlogin();
    }

    private void list() throws DataAccessException{
        System.out.println("Listed");
        postlogin();
    }

    private void join() throws DataAccessException{
        System.out.println("Joined");
        postlogin();
    }

    private void observe() throws DataAccessException{
        System.out.println("We are always watching");
        postlogin();
    }

    private void logout() throws DataAccessException{
        System.out.println("logged Out");
        server.logout();
        prelogin();
    }

    private void postlogFail() throws DataAccessException{
        System.out.println("That is not valid. Try typing in 'help'");
        postlogin();
    }
}
