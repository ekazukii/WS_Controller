package fr.ekazuki.wscontroller.game;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import fr.ekazuki.wscontroller.WSConnect;
import fr.ekazuki.wscontroller.WSControllerPlugin;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class MurderPM implements Listener {
	
	WSControllerPlugin plugin;
	
	public MurderPM() {
		this.plugin = WSControllerPlugin.INSTANCE;
	}
	
	@EventHandler
	public void onPluginmessage(PluginMessageEvent event) throws JSONException {
		if(event.getTag().equals("murder:info")) {
			final ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
			final String sub = in.readUTF();
			
			if(sub.equals("getState")) {
				final Boolean state = in.readBoolean();
				JSONObject json = new JSONObject();
				json.put("request", "getState");
				json.put("state", state);
				this.plugin.con.send("Murder", json);
			}
		}
	}
	
	public void startGame() {
		if (this.plugin.getProxy().getPlayers().size() != 0) {
			final ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF("startGame");
			out.writeUTF("data");
			
			this.plugin.getProxy().getServerInfo("murder").sendData("murder:info", out.toByteArray());
		} 
	}
	
	public void stopGame() {
		if (this.plugin.getProxy().getPlayers().size() != 0) {
			final ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF("stopGame");
			out.writeUTF("data");
			
			this.plugin.getProxy().getServerInfo("murder").sendData("murder:info", out.toByteArray());
		}
	}
	
	public void getState() throws JSONException {
		
		// If no player the game is not started
		if (this.plugin.getProxy().getPlayers().size() == 0) {
			JSONObject json = new JSONObject();
			json.put("request", "getState");
			json.put("state", false);
			this.plugin.con.send("murder:info", json);
		}
		
		final ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("getState");
		out.writeUTF("data");
		
		this.plugin.getProxy().getServerInfo("murder").sendData("murder:info", out.toByteArray());
	}
	
	public void dispatchWS(JSONObject obj) throws JSONException {
		switch (obj.getString("sub")) {
			case "start":
				this.startGame();
				break;
			case "stop":
				this.stopGame();
				break;
			case "state":
				this.getState();
				break;
		}
	}
}
