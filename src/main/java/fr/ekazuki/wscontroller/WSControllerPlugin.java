package fr.ekazuki.wscontroller;

import java.util.ArrayList;
import java.util.List;

import net.ME1312.SubServers.Bungee.SubAPI;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

public class WSControllerPlugin extends Plugin {
	
	public WSConnect con;
	public WSListener listener;
	public SubAPI subservers2;
		
	public void onEnable() {
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
		LuckPerms api = LuckPermsProvider.get();
		api.getUserManager().getUser("ekazuki").setPrimaryGroup("dev");
	}
	
	public void subDisable() {
		
	}
	
	public void startServer(String serverName) {
		this.subservers2.getSubServer(serverName).start();
	}
	
	public void stopServer(String serverName) {
		this.subservers2.getSubServer(serverName).stop();
	}
	
	public void sendCommandToServer(String serverName, String command) {
		this.subservers2.getSubServer(serverName).command(command);
	}
	
	public void kickPlayer(String playerName) {
		this.getProxy().getPlayer(playerName).disconnect(new TextComponent("You have been kicked from the server"));
	}

	public void sendPlayerToServer(String playerName, String server) {
		ServerInfo target = this.getProxy().getServerInfo(server);
		this.getProxy().getPlayer(playerName).connect(target);
		
	}

	public void sendAllPlayersToServer(String server) {
		ServerInfo target = this.getProxy().getServerInfo(server);
        for(ProxiedPlayer p: this.getProxy().getPlayers()){
        	this.getProxy().getPlayer(p.getName()).connect(target); ;
        }
	}

	public void broadcast(String message) {
		this.getProxy().broadcast(new TextComponent(message));
	}

}
