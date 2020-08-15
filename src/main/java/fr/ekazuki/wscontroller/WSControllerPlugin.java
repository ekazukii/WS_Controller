package fr.ekazuki.wscontroller;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import fr.ekazuki.wscontroller.game.GameCommand;
import fr.ekazuki.wscontroller.game.MurderPM;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class WSControllerPlugin extends Plugin {
	
	public WSConnect con;
	public WSListener listener;
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
		System.out.println("Enable");
		
		System.out.println("subEnable");
		this.con = new WSConnect(this, this.listener);
		createFile("config");
		this.whitelist = getConfig("config").getBoolean("whitelist");
		getProxy().getPluginManager().registerListener(this, new WSPluginListener(this));
		
		this.murderManager = new MurderPM();
		this.getProxy().registerChannel("murder:info");
		this.getProxy().getPluginManager().registerListener(this, murderManager);
		
		this.getProxy().getPluginManager().registerCommand(this, new GameCommand("game"));
		
        host = getConfig("config").getString("host");
        port = getConfig("config").getInt("port");
        database = "website";
        username = getConfig("config").getString("username");
        password = getConfig("config").getString("password");   
        
        try {    
            openConnection();
            STATEMENT = connection.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}
	
	public void onDisable() {
	    try { 
	        if (connection!=null && !connection.isClosed()){ 
	            connection.close(); 
	        }
	    } catch(Exception e) {
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
					config.set("host", "localhost");
					config.set("port", 3306);
					config.set("username", "root");
					config.set("password", "");
					config.set("socketUrl", "http://localhost");
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
	
}
