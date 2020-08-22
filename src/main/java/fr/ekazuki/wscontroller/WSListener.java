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
					if(this.plugin.getProxy().getPluginManager().getPlugin("LuckPerms") != null) {
						this.plugin.con.sendGroup(data.getString("player"));
					} else {
						JSONObject res = new JSONObject();
						res.put("success", false);
						res.put("request", "sendGroup");
						this.plugin.con.send("sendGroup", res);
					}
					break;
				case "sendGroups":
					if(this.plugin.getProxy().getPluginManager().getPlugin("LuckPerms") != null) {
						this.plugin.con.sendGroups();
					} else {
						JSONObject res = new JSONObject();
						res.put("success", false);
						res.put("request", "sendGroup");
						this.plugin.con.send("sendGroup", res);
					}
					break;
				case "sendPrefix":
					if(this.plugin.getProxy().getPluginManager().getPlugin("LuckPerms") != null) {
						this.plugin.con.sendPrefix(data.getString("player"));
					} else {
						JSONObject res = new JSONObject();
						res.put("success", false);
						res.put("request", "sendPrefix");
						this.plugin.con.send("sendPrefix", res);
					}
					break;
				case "murder":
					this.plugin.murderManager.dispatchWS(data);
					break;
				case "sendModeration":
					if(this.plugin.getProxy().getPluginManager().getPlugin("BanManager") != null) {
						this.plugin.con.sendModeration(data.getString("player"));
					} else {
						JSONObject res = new JSONObject();
						res.put("success", false);
						res.put("request", "sendModeration");
						this.plugin.con.send("sendModeration", res);
					}
				default:
					break;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
}
