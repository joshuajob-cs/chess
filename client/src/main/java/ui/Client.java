package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameList;
import server.DataAccessException;
import server.ServerFacade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


public class Client {
    private final ServerFacade server;
    private State state = Client.State.PRELOGIN;
    private final ClientObserver observer = new ClientObserver();

    public Client(int port){
        server = new ServerFacade(port, observer);
    }

    private enum State{
        PRELOGIN,
        POSTLOGIN,
        GAME
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
            else{
                gamePhase(command[0], parameters);
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
        System.out.println("1");
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
        observer.setColor(color);
        server.joinGame(color, gameNum);
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
        BoardUI.printBoard(board, ChessGame.TeamColor.WHITE);
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

    private void gamePhase(String command, String[] parameters) throws DataAccessException{
        switch (command) {
            case "help" -> helpGame(parameters);
            case "redraw" -> redraw(parameters);
            case "leave" -> leave(parameters);
            case "move" -> move(parameters);
            case "resign" -> resign(parameters);
            case "highlight_moves" -> highlight(parameters);
            default -> fail();
        }
    }

    private void helpGame(String[] params){
        if (params.length > 0){
            System.out.print("Help does not take any parameters. ");
            fail();
            return;
        }
        System.out.print(
                """
                redraw - to see the game board
                leave - to leave the game
                move <FROM> <TO> - to make a move
                move <FROM> <TO> <ROOK|BISHOP|KNIGHT|QUEEN> - promotion
                resign - to give up
                highlight_moves - to see all possible moves
                help - ???
                """);
        run();
    }

    private void redraw(String[] params){
        //Similar to observe, calls server.getGame()
    }

    private void leave(String[] params){
        server.leave();
        // Calls Server Facade
    }

    private void move(String[] params){
        if (!(params.length == 2 || params.length == 3)){
            System.out.print(params.length + " arguments for move. ");
            fail();
            return;
        }
        ChessPosition startPos = ChessPosition.parse(params[0]);
        if (startPos == null){
            System.out.print("Incorrect format for <FROM>. a1, b2, c3 are correct formats");
            fail();
            return;
        }
        ChessPosition endPos = ChessPosition.parse(params[1]);
        if (endPos == null){
            System.out.print("Incorrect format for <TO>. a1, b2, c3 are correct formats");
            fail();
            return;
        }
        ChessPiece.PieceType promotion = null;
        if (params.length == 3){
            if(!(params[2].equals("rook") | params[2].equals("bishop") | params[2].equals("knight") | params[2].equals("queen"))){
                System.out.print(params[2] + " is not 'ROOK' or 'BISHOP' or 'KNIGHT' or 'QUEEN'. ");
                fail();
                return;
            }
            promotion = Enum.valueOf(ChessPiece.PieceType.class, params[2].toUpperCase());
        }
        server.move(new ChessMove(startPos, endPos, promotion));
    }

    private void resign(String[] params){
        server.resign();
        // Calls Server Facade
    }

    private void highlight(String[] params){
        // Local Operation
        // Utilise Board UI, give board UI the board and a list of highlighted squares
    }
}
