package ui;

import chess.ChessBoard;
import shared.DataAccessException;
import java.util.Scanner;

public class Repl {
    private final ChessClient client;
    Scanner scanner = new Scanner(System.in);

    public Repl(String serverURL) {
        this.client = new ChessClient(serverURL);
    }



    public void run() {
        System.out.println("Welcome to Chess, \n Log in or Register to start");
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
            System.out.print("Status: Logged Out >>> ");
            String line = scanner.nextLine();
            if (line.equalsIgnoreCase("quit")) {
                System.exit(0);
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
            System.out.print("Status: Logged In >>> ");
            String line = scanner.nextLine();
            if (line.equalsIgnoreCase("quit")) {
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

    private void gamePlay() {
        ChessBoard board = client.getBoard();
        String playerColor = client.getPlayerColor();
        ChessBoardPrinter.print(board, playerColor);

        while (true) {
            System.out.print("Status: Game >>> ");
            String line = scanner.nextLine();
            if (line.equalsIgnoreCase("quit")) {
                System.exit(0);
            }
            if (client.getState() != ChessClient.State.GamePlay) {
                return;
            }
            if (line.equalsIgnoreCase("exit")) {
                client.setState(ChessClient.State.Postlogin);
                return;
            }
        }
    }
}
