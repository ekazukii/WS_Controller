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
				case "sendServer":
					this.plugin.con.sendServer(data.getString("server"));
					break;
				case "sendWhitelist":
					this.plugin.con.sendWhitelist();
					break;
				case "startWhitelist":
					this.plugin.whitelist = true;
					break;
				case "stopWhitelist":
					this.plugin.whitelist = false;
					break;
				case "sendGroup":
					this.plugin.con.sendGroup(data.getString("player"));
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
