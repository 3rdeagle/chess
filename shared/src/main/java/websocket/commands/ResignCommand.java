package websocket.commands;

public class ResignCommand {
    public String commandType;
    public String authToken;
    public Integer gameID;

    public ResignCommand() {

    }

    public ResignCommand(String authToken, int gameID) {
        this.commandType = "RESIGN";
        this.authToken = authToken;
        this.gameID = gameID;
    }
}
