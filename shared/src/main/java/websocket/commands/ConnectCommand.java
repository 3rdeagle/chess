package websocket.commands;

public class ConnectCommand {

    public String commandType;
    public String authToken;
    public Integer gameID;

    public ConnectCommand() { }

    public ConnectCommand(String authToken, int gameID) {
        this.commandType = "CONNECT";
        this.authToken = authToken;
        this.gameID = gameID;
    }
}
