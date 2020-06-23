package fr.ekazuki.wscntroller.user;

import fr.ekazuki.wscontroller.game.GameType;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class User {
	
	private int murderWin;
	private int murderMurderWin;
	private int dacWin;
	private int dacDac;
	private int spleefWin;
	private int sumoWin;
	private int lobbyPvpWin;
	private int blWin;
	
	public ProxiedPlayer player;
	
	public User(ProxiedPlayer player) {
		this.player = player;
	}
	
	public void hydrate() {
		
	}
	
	public void addUser() {
		
	}
	
	public void removeUser() {
		// TODO 
	}
	
	public void getStats(StatType type) {
		
	}
	
	public void add(StatType type) {
		
	}

}
