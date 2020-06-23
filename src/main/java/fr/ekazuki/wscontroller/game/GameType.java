package fr.ekazuki.wscontroller.game;

public enum GameType {
	MURDER("murder", "Murder"),
	DAC("dac", "Dé à coudre"),
	SUMO("sumo", "Sumo"),
	SPLEEF("spleef", "Spleef"),
	PVP("lobby", "Arène pvp");
	
	public String server;
	public String gameName;
	
	GameType(String server, String gameName) {
		this.server = server;
		this.gameName = gameName;
	}
	
	public String getServer() {
		return this.server;
	}
	
	public String getGameName() {
		return this.gameName;
	}
}
