package ui;

import chess.ChessGame;
import dataaccess.DataAccessException;
import server.ServerFacade;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Client {
    private final ServerFacade server = new ServerFacade();
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
            case "help" -> helpLogin(parameters);
            case "register" -> register(parameters);
            case "login" -> login(parameters);
            case "quit" -> System.exit(0);
            default -> fail();
        }
    }

    private void helpLogin(String[] params){
        if (params.length > 0){
            System.out.print("Help does not take any parameters. ");
            fail();
            return;
        }
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
        if (params.length != 3){
            System.out.print(params.length + " arguments for register. ");
            fail();
            return;
        }
        server.register(params[0], params[1], params[2]);
        System.out.println("Registered!");
        state = State.POSTLOGIN;
        run();
    }

    private void login(String[] params) throws DataAccessException{
        if (params.length != 2){
            System.out.print(params.length + " arguments for register. ");
            fail();
            return;
        }
        server.login(params[0], params[1]);
        System.out.println("Logged In!");
        state = State.POSTLOGIN;
        run();
    }

    private void fail(){
        System.out.println("That is not valid. Try typing in 'help'");
        run();
    }

    private void postlogin(String command, String[] parameters) throws DataAccessException{
        switch (command) {
            case "help" -> helpPostlog(parameters);
            case "create" -> create(parameters);
            case "list" -> list(parameters);
            case "join" -> join(parameters);
            case "observe" -> observe(parameters);
            case "logout" -> logout(parameters);
            case "quit" -> System.exit(0);
            default -> fail();
        }
    }

    private void helpPostlog(String[] params){
        if (params.length > 0){
            System.out.print("Help does not take any parameters. ");
            fail();
            return;
        }
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

    private void create(String[] params) throws DataAccessException{
        if (params.length != 1){
            System.out.print(params.length + " arguments for create. ");
            fail();
            return;
        }
        server.createGame(params[0]);
        System.out.println("New game created!");
        run();
    }

    private void list(String[] params) throws DataAccessException{
        if (params.length > 0){
            System.out.print("List does not take any parameters. ");
            fail();
            return;
        }
        var gameList = server.listGames().games();
        System.out.println(gameList);
        run();
    }

    private void join(String[] params) throws DataAccessException{
        int gameNum;
        if (params.length != 2){
            System.out.print(params.length + " arguments for join. ");
            fail();
            return;
        }
        try {
            gameNum = Integer.parseInt(params[0]);
        } catch (NumberFormatException e) {
            System.out.print(params[0] + " is not a number. ");
            fail();
            return;
        }
        if(!(params[1].equals("white") | params[1].equals("black"))){
            System.out.print(params[1] + " is not WHITE or BLACK. ");
            fail();
            return;
        }
        server.joinGame(Enum.valueOf(ChessGame.TeamColor.class, params[1].toUpperCase()), gameNum);
        System.out.println("Joined game!");
        run();
    }

    private void observe(String[] params) throws DataAccessException{
        if (params.length != 1){
            System.out.print(params.length + " arguments for observe. ");
            fail();
            return;
        }
        try {
            int gameNum = Integer.parseInt(params[0]);
        } catch (NumberFormatException e) {
            System.out.print(params[0] + " is not a number. ");
            fail();
            return;
        }
        System.out.println("We are always watching");
        run();
    }

    private void logout(String[] params) throws DataAccessException{
        if (params.length > 0){
            System.out.print("Logout does not take any parameters. ");
            fail();
            return;
        }
        server.logout();
        state = State.PRELOGIN;
        run();
    }
}
