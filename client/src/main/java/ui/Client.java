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
    private State state = Client.State.PRELOGIN;

    private enum State{
        PRELOGIN,
        POSTLOGIN
    }

    public void run(){
        Scanner scanner = new Scanner(System.in);
        if (state == State.PRELOGIN) {
            System.out.print("[LOGGED OUT]  >>> ");
        }
        else if (state == State.POSTLOGIN){
            System.out.print("[LOGGED IN]  >>> ");
        }
        String[] command = scanner.nextLine().toLowerCase().split("\\s+");
        if (command.length == 0){
            fail();
            return;
        }
        String[] parameters = Arrays.copyOfRange(command, 1, command.length);
        try {
            if (state == State.PRELOGIN) {
                prelogin(command[0], parameters);
            }
            else if (state == State.POSTLOGIN){
                postlogin(command[0], parameters);
            }
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
            run();
        }
    }

    private void prelogin(String command, String[] parameters) throws DataAccessException{
        switch (command) {
            case "help" -> helpLogin();
            case "register" -> register(parameters);
            case "login" -> login(parameters);
            case "quit" -> System.exit(0);
            default -> fail();
        }
    }

    private void helpLogin(){
        System.out.print(
        """
        register <USERNAME> <PASSWORD> <EMAIL> - to create an account
        login <USERNAME> <PASSWORD> - to play chess
        quit - i'll miss you
        help - ???
        """);
        run();
    }

    private void register(String[] params) throws DataAccessException{
        System.out.println("Registered!");
        if (params.length != 3){
            System.out.print(params.length + " arguments for register. ");
            fail();
            return;
        }
        server.register(new UserData(params[0], params[1], params[2]));
        state = State.POSTLOGIN;
        run();
    }

    private void login(String[] params) throws DataAccessException{
        System.out.println("Logged In");
        if (params.length != 2){
            System.out.print(params.length + " arguments for register. ");
            fail();
            return;
        }
        server.login(new LoginData(params[0], params[1]));
        state = State.POSTLOGIN;
        run();
    }

    private void fail(){
        System.out.println("That is not valid. Try typing in 'help'");
        run();
    }

    private void postlogin(String command, String[] parameters) throws DataAccessException{
        switch (command) {
            case "help" -> helpPostlog();
            case "create" -> create();
            case "list" -> list();
            case "join" -> join();
            case "observe" -> observe();
            case "logout" -> logout();
            case "quit" -> System.exit(0);
            default -> fail();
        }
    }

    private void helpPostlog() throws DataAccessException{
        System.out.print(
                """
                create <Name> - to start a new game
                list - to list games
                join <ID> <WHITE|BLACK> - to join a game
                observe <ID> - to watch a game
                logout - if you are finished
                quit - i'll miss you
                help - ???
                """);
        run();
    }

    private void create() throws DataAccessException{
        System.out.println("Made it");
        run();
    }

    private void list() throws DataAccessException{
        System.out.println("Listed");
        run();
    }

    private void join() throws DataAccessException{
        System.out.println("Joined");
        run();
    }

    private void observe() throws DataAccessException{
        System.out.println("We are always watching");
        run();
    }

    private void logout() throws DataAccessException{
        System.out.println("logged Out");
        server.logout();
        state = State.PRELOGIN;
        run();
    }
}
