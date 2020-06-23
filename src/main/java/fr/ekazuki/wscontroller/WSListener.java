package fr.ekazuki.wscontroller;

import java.sql.SQLException;
import java.util.UUID;

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
		System.out.println("CALLLLLLLLLL");
		JSONObject data = (JSONObject) args[0];	
		try {
			switch (data.getString("request")) {
				case "sendPlayers":
					this.plugin.con.sendPlayers();
					break;
				case "sendPlayer":
					this.plugin.con.sendPlayer(data.getString("player"));
					break;
				case "sendServers":
					this.plugin.con.sendServers();
					break;
				case "sendServer":
					this.plugin.con.sendServer(data.getString("server"));
					break;
				case "sendWhitelist":
					this.plugin.con.sendWhitelist();
					break;
				case "startServer":
					this.plugin.startServer(data.getString("server"));
					break;
				case "stopServer":
					this.plugin.stopServer(data.getString("server"));
					break;
				case "stopAllServers":
					this.plugin.stopAllServers();
				case "sendCommand":
					this.plugin.sendCommandToServer(data.getString("server"), data.getString("command"));
					break;
				case "kickPlayer":
					this.plugin.kickPlayer(data.getString("player"));
					break;
				case "banipPlayer":
					this.plugin.banIpPlayer(data.getString("player"));
					break;
				case "banPlayer":
					this.plugin.banPlayer(data.getString("player"));
					break;
				case "mutePlayer":
					this.plugin.mutePlayer(data.getString("player"));
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
				case "startWhitelist":
					this.plugin.whitelist = true;
					break;
				case "stopWhitelist":
					this.plugin.whitelist = false;
					break;
				case "setGroup":
					this.plugin.setGroupOfPlayer(data.getString("player"), data.getString("group"));
					break;
				case "sendGroup":
					this.plugin.con.sendGroup(data.getString("player"));
					break;
				case "setPrefix":
					this.plugin.setPrefixOfPlayer(data.getString("player"), data.getString("prefix"));
					break;
				case "sendPrefix":
					this.plugin.con.sendPrefix(data.getString("player"));
					break;
				case "murder":
					this.plugin.murderManager.dispatchWS(data);
					break;
				default:
					break;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
}
