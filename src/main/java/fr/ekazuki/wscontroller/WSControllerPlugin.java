package fr.ekazuki.wscontroller;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;

import fr.ekazuki.wscontroller.game.GameCommand;
import fr.ekazuki.wscontroller.game.MurderPM;
import net.ME1312.SubServers.Bungee.SubAPI;
import net.ME1312.SubServers.Bungee.Host.SubServer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.data.DataType;
import net.luckperms.api.node.Node;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class WSControllerPlugin extends Plugin {
	
	public WSConnect con;
	public WSListener listener;
	public SubAPI subservers2;
	static Connection connection;
	private String host, database, username, password;
	private int port;
	public boolean whitelist;
	
	public static Statement STATEMENT;
	public static WSControllerPlugin INSTANCE;
	
	public MurderPM murderManager;
		
	public void onEnable() {
		
		INSTANCE = this;
		
		System.out.println("Enable");
		listener = new WSListener(this);
		this.subservers2 = net.ME1312.SubServers.Bungee.SubAPI.getInstance();
		this.subservers2.addListener(this::subEnable, this::subDisable);
		System.out.println("Enable");
	}
	
	public void onDisable() {
		
	}
	
	public void subEnable()  {
		System.out.println("subEnable");
		this.con = new WSConnect(this, this.listener);
		createFile("config");
		this.whitelist = getConfig("config").getBoolean("whitelist");
		getProxy().getPluginManager().registerListener(this, new WSPluginListener(this));
		
		this.murderManager = new MurderPM();
		this.getProxy().registerChannel("murder:info");
		this.getProxy().getPluginManager().registerListener(this, murderManager);
		
		this.getProxy().getPluginManager().registerCommand(this, new GameCommand("game"));
		
        host = "localhost";
        port = 3306;
        database = "website";
        username = "plugins";
        password = "pikachu3";   
        
        try {    
            openConnection();
            STATEMENT = connection.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}
	
	public void openConnection() throws SQLException, ClassNotFoundException {
	    if (connection != null && !connection.isClosed()) {
	        return;
	    }
	 
	    synchronized (this) {
	        if (connection != null && !connection.isClosed()) {
	            return;
	        }
	        Class.forName("com.mysql.jdbc.Driver");
	        connection = DriverManager.getConnection("jdbc:mysql://" + this.host+ ":" + this.port + "/" + this.database, this.username, this.password);
	    }
	}
	
	public void subDisable() {
	    try { 
	        if (connection!=null && !connection.isClosed()){ 
	            connection.close(); 
	        }
	    } catch(Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public void startServer(String serverName) {
		this.subservers2.getSubServer(serverName).start();
	}
	
	public void stopServer(String serverName) {
		this.subservers2.getSubServer(serverName).stop();
	}
	
	public void sendCommandToServer(String serverName, String command) {
		SubServer server = this.subservers2.getSubServer(serverName);
		if (server != null) {
			server.command(command);
		}
	}

	public void sendPlayerToServer(String playerName, String server) {
		ServerInfo target = this.getProxy().getServerInfo(server);
		ProxiedPlayer player = this.getProxy().getPlayer(playerName);
		if (target != null && player != null) {
			player.connect(target);
		}
		
	}

	public void sendAllPlayersToServer(String server) {
		ServerInfo target = this.getProxy().getServerInfo(server);
		if (target != null) {
	        for(ProxiedPlayer p: this.getProxy().getPlayers()){
	        	p.connect(target); 
	        }
		}
	}

	public void broadcast(String message) {
		this.getProxy().broadcast(new TextComponent(message));
	}
	
	public void setGroupOfPlayer(String player, String group) {
		this.getProxy().getPluginManager().dispatchCommand(this.getProxy().getConsole(), "lpb user "+player+" parent set "+group);
	}
	
	public void setPrefixOfPlayer(String player, String prefix) {
		this.getProxy().getPluginManager().dispatchCommand(this.getProxy().getConsole(), "lpb user "+player+" meta setprefix "+prefix);
	}
	
	public void kickPlayer(String player) {
		ProxiedPlayer pl = this.getProxy().getPlayer(player);
		if (pl != null) {
			pl.disconnect(new TextComponent("You have been kicked from the server"));
		}
	}
	
	public void banPlayer(String player) {
		this.getProxy().getPluginManager().dispatchCommand(this.getProxy().getConsole(), "gtempban "+player+" 12h");
	}
	
	public void banIpPlayer(String player) {
		this.getProxy().getPluginManager().dispatchCommand(this.getProxy().getConsole(), "gtempbanip "+player+" 12h");
	}
	
	public void mutePlayer(String player) {
		this.getProxy().getPluginManager().dispatchCommand(this.getProxy().getConsole(), "vmute "+player+" global 720");
	}
	
	public void createFile(String fileName) {
		if(!getDataFolder().exists()) {
			getDataFolder().mkdir();
		}
		
		File file = new File(getDataFolder(), fileName + ".yml");
		if(!file.exists()) {
			try {
				file.createNewFile();
				if (fileName.contentEquals("config")) {
					Configuration config = getConfig(fileName);
					config.set("whitelist", false);
					config.set("auth", "FAKE_TEMP_PASSWORD");
					saveConfig(config, fileName);
					getProxy().getConsole().sendMessage(new TextComponent("New config is created, PLUGIN WILL STOP BECAUSE NO PRIVATE KEY"));
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public Configuration getConfig(String fileName) {
		try {
			return ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), fileName + ".yml"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void saveConfig(Configuration config, String fileName) {
		try {
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, new File(getDataFolder(), fileName + ".yml"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public List<UUID> listWhitelisted() throws SQLException {
		ResultSet result = STATEMENT.executeQuery("SELECT * FROM uuids;");
		List<UUID> listUUIDs = new ArrayList<UUID>();
		while (result.next()) {
		    String uuid = result.getString("uuid");
		    listUUIDs.add(UUID.fromString(uuid));
		}
		return listUUIDs;
	}
	
	public void addWhitelisted(UUID uuid) throws SQLException {
		STATEMENT.executeUpdate("INSERT INTO uuids (uuid) VALUES ('"+uuid.toString()+"');");
	}
	
	public void removeWhitelisted(UUID uuid) throws SQLException {
		STATEMENT.executeUpdate("DELETE FROM uuids WHERE uuid = '"+uuid.toString()+"';");
	}
	
	public boolean isWhitelisted(UUID uuid) throws SQLException {
		List<UUID> uuids = this.listWhitelisted();
		return uuids.contains(uuid);
	}

	public void stopAllServers() {
		Map<String, SubServer> subservers = this.subservers2.getSubServers();
		for (String key : subservers.keySet()) {
			SubServer server = subservers.get(key);
			server.stop();
		}
		
	}
	
}
