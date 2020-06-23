package fr.ekazuki.wscntroller.user;

public enum StatType {
	MURDERWIN("murder_win"),
	MURDERMURDERWIN("murder_murder_win"),
	DACWIN("dac_win"),
	DACDAC("dac_dac"),
	SPLEEFWIN("spleef_win"),
	SUMOWIN("sumo_win"),
	LOBBYPVPWIN("lobby_pvp_win"),
	BLWIN("bl_win");
	
	public String sqlClm;
	
	StatType(String sqlClm) {
		this.sqlClm = sqlClm;
	}
}
