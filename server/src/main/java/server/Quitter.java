package server;

import java.util.Scanner;

public class Quitter {
    Server server;
    public void start() {
        server = new Server();
        server.run(8080);
        System.out.println("♕ 240 Chess Server");
        run();
    }

    private void run(){
        System.out.println("Type in 'quit' when you are finished");
        Scanner scanner = new Scanner(System.in);
        String command = scanner.nextLine();
        if (command.equals("quit")){
            server.stop();
        }
        else{
            run();
        }
    }
}
