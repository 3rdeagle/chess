package websocket.commands;

public class LeaveCommand {
    public String commandType;
    public String authToken;
    public Integer gameID;

    public LeaveCommand() {

    }

    public LeaveCommand(String authToken, int gameID) {
        this.commandType = "LEAVE";
        this.authToken = authToken;
        this.gameID = gameID;
    }
}
