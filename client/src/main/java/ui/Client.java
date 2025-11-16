package ui;

import chess.ChessBoard;
import chess.ChessGame;
import model.GameList;
import server.DataAccessException;
import server.ServerFacade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Client {
    private final ServerFacade server;
    private State state = Client.State.PRELOGIN;

    public Client(int port){
        server = new ServerFacade(port);
    }

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
            System.out.print(params.length + " arguments for login. ");
            fail();
            return;
        }
        server.login(params[0], params[1]);
        state = State.POSTLOGIN;
        run();
    }

    private void fail(){
        System.out.println("That is not valid. Try typing in 'help'.");
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
        int gameNum = server.createGame(params[0]);
        System.out.println("Type 'join " + gameNum + " <WHITE|BLACK>' to join the game you created.");
        run();
    }

    private void list(String[] params) throws DataAccessException{
        if (params.length > 0){
            System.out.print("List does not take any parameters. ");
            fail();
            return;
        }
        var gameList = server.listGames();
        printGames(gameList);
        run();
    }

    private void printGames(GameList list){
        var games = new ArrayList<>(list.games());
        for(int i = 0; i < games.size(); i++){
            var game = games.get(i);
            String white = (game.whiteUsername() == null) ? "nobody": game.whiteUsername();
            String black = (game.blackUsername() == null) ? "nobody": game.blackUsername();
            System.out.println(i+1 + ". " + game.gameName() + ": with "
            + white + " playing white and " + black + " playing black");
        }
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
            System.out.print(params[0] + " is not an integer. ");
            fail();
            return;
        }
        if(!(params[1].equals("white") | params[1].equals("black"))){
            System.out.print(params[1] + " is not 'WHITE' or 'BLACK'. ");
            fail();
            return;
        }
        ChessGame.TeamColor color = Enum.valueOf(ChessGame.TeamColor.class, params[1].toUpperCase());
        server.joinGame(color, gameNum);
        var board = server.getGame(gameNum);
        printBoard(board, color);
        run();
    }

    private void observe(String[] params) throws DataAccessException{
        if (params.length != 1){
            System.out.print(params.length + " arguments for observe. ");
            fail();
            return;
        }
        int gameNum;
        try {
            gameNum = Integer.parseInt(params[0]);
        } catch (NumberFormatException e) {
            System.out.print(params[0] + " is not an integer. ");
            fail();
            return;
        }
        var board = server.getGame(gameNum);
        printBoard(board, ChessGame.TeamColor.WHITE);
        run();
    }

    private void printBoard(ChessBoard board, ChessGame.TeamColor color){
        var boardUI = new BoardUI(board);
        String[][] printable = boardUI.get();
        if (color == ChessGame.TeamColor.WHITE) {
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    System.out.print(printable[i][j]);
                }
                System.out.print(RESET_BG_COLOR + RESET_TEXT_COLOR + "\n");
            }
        } else{
            for (int i = 9; i >= 0; i--) {
                for (int j = 9; j >= 0; j--) {
                    System.out.print(printable[i][j]);
                }
                System.out.print(RESET_BG_COLOR + RESET_TEXT_COLOR + "\n");
            }
        }
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
