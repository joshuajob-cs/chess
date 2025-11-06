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
        if (command.equals("help")){
            System.out.println("register <USERNAME> <PASSWORD> <EMAIL> - to create an account");
            System.out.println("login <USERNAME> <PASSWORD> - to play chess");
            System.out.println("quit - i'll miss you");
            System.out.println("help - ???");
            prelogin();
        }
        else if(command.equals("register")){
            System.out.print("Registered");
            prelogin();
        }
        else if(command.equals("login")){
            System.out.print("Logged In");
            prelogin();
        }
        else if(command.equals("quit")){
            System.out.println("Goodbye my friend");
        }
        else{
            System.out.println("That is not valid. Try typing in 'help'");
            prelogin();
        }
    }
}
