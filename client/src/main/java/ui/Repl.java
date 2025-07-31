package ui;

import dataaccess.DataAccessException;

import java.util.Scanner;
import static ui.EscapeSequences.*;

public class Repl {
    private final ChessClient client;
    Scanner scanner = new Scanner(System.in);

    public Repl(String serverURL) {
        this.client = new ChessClient(serverURL);
    }

    public void Run() {
        System.out.println("Welcome to Chess, \n sign in or register to start");
        while (true) {
            switch (client.getState()) {
                case Prelogin:
                    preLogin();
                    break;
                case Postlogin:
                    postLogin();
                    break;
                case GamePlay:
                    gamePlay();


            }
        }
    }


    private void preLogin() {
        while (true) {
            System.out.println(client.help());
            String line = scanner.nextLine();
            if (line.toLowerCase().contains("quit") ) {
                break;
            }

            try {
                var result = client.eval(line);
                System.out.println(result);
                if (client.getState() != ChessClient.State.Prelogin) {
                    return;
                }
            } catch (DataAccessException e) {
                System.out.println("Error: " + e.getMessage());
            }

        }
    }

    private void postLogin() {
        while (true) {
            System.out.println(client.help());
            String line = scanner.nextLine();
            if (line.equalsIgnoreCase("quit") ) {
                System.exit(0);
            }

            try {
                var result = client.eval(line);
                System.out.println(result);
                if (client.getState() != ChessClient.State.Postlogin) {
                    return;
                }

            } catch (DataAccessException e) {
                System.out.println("Error: " + e.getMessage());
            }

        }
    }
}
