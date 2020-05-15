package fr.ekazuki.wscontroller;

import org.json.JSONException;
import org.json.JSONObject;
import io.socket.emitter.Emitter;

public class WSListener implements Emitter.Listener {

	private WSControllerPlugin plugin;

	public WSListener(WSControllerPlugin plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void call(Object... args) {
		JSONObject data = (JSONObject) args[0];	
		try {
			switch (data.getString("request")) {
				case "sendPlayers":
					this.plugin.getLogger().info("------- RECEIVE ----------");
					this.plugin.con.sendPlayers();
					break;
				case "sendServers":
					this.plugin.con.sendServers();
					break;
				case "sendServer":
					this.plugin.con.sendServer(data.getString("server"));
					break;
				case "startServer":
					this.plugin.startServer(data.getString("server"));
					break;
				case "stopServer":
					this.plugin.stopServer(data.getString("server"));
					break;
				case "sendCommand":
					this.plugin.sendCommandToServer(data.getString("server"), data.getString("command"));
					break;
				case "kickPlayer":
					this.plugin.kickPlayer(data.getString("player"));
					break;
				case "sendPlayerToServer":
					this.plugin.sendPlayerToServer(data.getString("player"), data.getString("server"));
					break;
				case "sendAllPlayersToServer":
					this.plugin.sendAllPlayersToServer(data.getString("server"));
					break;
				case "broadcast":
					this.plugin.broadcast(data.getString("message"));
					break;
				default:
					break;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
}
