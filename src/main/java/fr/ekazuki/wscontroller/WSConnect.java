package fr.ekazuki.wscontroller;

import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Strings;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import net.ME1312.SubServers.Bungee.Host.SubServer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.node.Node;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;

public class WSConnect {
	Socket socket;
	boolean connected = false;
	private WSControllerPlugin plugin;
	
	public WSConnect(WSControllerPlugin plugin, Emitter.Listener listener) {
		WSConnect self = this;
		this.plugin = plugin;
		try {
			IO.Options opts = new IO.Options();
			opts.forceNew = true;
			//opts.reconnection = false;
			socket = IO.socket("http://localhost", opts);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
			public void call(Object... arg0) {
				self.connected = true;
				JSONObject obj = new JSONObject();
				try {
					obj.put("key", plugin.getConfig("config").getString("auth"));
					socket.emit("auth", obj);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
		socket.on("request", listener);
		socket.connect();
	}
	
	public void send(String type, JSONObject data) {
		if (this.connected == true) {
			socket.emit(type, data);
		}
	}
	
	public void sendPlayers() {
		JSONObject obj = new JSONObject();
		JSONArray players = new JSONArray();
        for(ProxiedPlayer p: this.plugin.getProxy().getPlayers()){
        	JSONObject player = new JSONObject();
        	try {
            	player.put("name", p.getName());
				player.put("server", p.getServer().getInfo().getName());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	players.put(player);
        }
        
		try {
			obj.put("players", players);
			obj.put("request", "sendPlayers");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	
		this.send("sendPlayers", obj);
	}
	
	public void sendPlayer(String username) throws JSONException {
		Server server = this.plugin.getProxy().getPlayer(username).getServer();
		if (server == null) {
			JSONObject errorJSON = new JSONObject();
			errorJSON.put("error", "Player not found");
			this.send("sendPlayer", errorJSON);
		} else {
			JSONObject serverJSON = new JSONObject();
			
	        serverJSON.put("server", server.getInfo().getName());

			// Send the main JSON object to server
			this.send("sendPlayer", serverJSON);
		}

	}
	
	public void sendServers() throws JSONException {
		JSONObject json = new JSONObject();
		json.put("request", "sendServers");
		
		JSONArray servers = new JSONArray();
		
		Map<String, SubServer> subservers = this.plugin.subservers2.getSubServers();
		for (String key : subservers.keySet()) {
			SubServer server = subservers.get(key);
			JSONObject serverJSON = new JSONObject();
			
			serverJSON.put("name", key);
			
			serverJSON.put("displayName", server.getDisplayName());
	        
	        // Add online status to the JSON
	        serverJSON.put("online", server.isRunning());
	    
	        // Save in the main JSON object
			servers.put(serverJSON);
		}
		
		json.put("servers", servers);
		// Send the main JSON object to server
		this.send("sendServers", json);
	}
	
	public void sendServer(String serverName) throws JSONException {
		SubServer server = this.plugin.subservers2.getSubServer(serverName);
		if (server == null) {
			JSONObject errorJSON = new JSONObject();
			errorJSON.put("error", "Server not found");
			this.send("sendServer", errorJSON);
		} else {
			server.getWhitelist();
			JSONObject serverJSON = new JSONObject();
			
			// Add list of players connected on this server to the JSON
			List<String> playernames = new ArrayList<String>();
	        for(ProxiedPlayer p: server.getPlayers()){
	            playernames.add(p.getName());
	        }
	        serverJSON.put("players", playernames);
	        
	        // Add online status to the JSON
	        serverJSON.put("online", server.isRunning());
	        
	        serverJSON.put("displayName", serverName);
	        
	        serverJSON.put("name", server.getDisplayName());

			// Send the main JSON object to server
			this.send("sendServer", serverJSON);
		}

	}
	
	public void sendGroup(String player) throws JSONException {
		LuckPerms api = LuckPermsProvider.get();
		JSONObject serverJSON = new JSONObject();
		serverJSON.put("request", "sendGroup");
		serverJSON.put("group", api.getUserManager().getUser(player).getPrimaryGroup());
		this.send("sendGroup", serverJSON);
	}
	
	public void sendPrefix(String player) throws JSONException {
		LuckPerms api = LuckPermsProvider.get();
		JSONObject serverJSON = new JSONObject();
		String prefix = "";
		serverJSON.put("request", "sendGroup");
		Iterator<Node> it = api.getUserManager().getUser(player).getNodes().iterator();
		while(it.hasNext()) {
			String key = it.next().getKey();
			System.out.println(key);
			String[] strings = key.split("\\.");
			System.out.println(key.length());
			if(strings.length > 2) {
				System.out.println("Premier test");
				if(strings[0].equals("prefix")) {
					System.out.println("Deuxieme test");
					prefix = strings[2];
				}	
			}
		}
		serverJSON.put("request", "sendPrefix");
		serverJSON.put("prefix", prefix);
		this.send("sendPrefix", serverJSON);
	}

	public void sendWhitelist() throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put("whitelist", this.plugin.whitelist);
		obj.put("request", "sendWhitelist");
		this.send("sendWhitelist", obj);
	}
}
