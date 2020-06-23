package fr.ekazuki.wscntroller.user;

import fr.ekazuki.wscontroller.WSControllerPlugin;
import fr.ekazuki.wscontroller.game.GameType;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import java.sql.Statement;
import java.util.ArrayList;

public class UserManager {
	
	public static UserManager INSTANCE; 
	public ArrayList<User> users;
	
	public UserManager() {
		this.users = new ArrayList<User>();
		INSTANCE = this;
		WSControllerPlugin.INSTANCE.getProxy().getPluginManager().registerListener(WSControllerPlugin.INSTANCE, new UserListener());
	}
	
	public User registerUser(ProxiedPlayer player) {
		User user = new User(player);
		users.add(user);
		return user;
	}
	
	public void getStat(ProxiedPlayer player, StatType stat) {
		User user = this.getUser(player);
		user.getStats(stat);
	}
	
	public void add(ProxiedPlayer player, StatType stat) {
		User user = this.getUser(player);
		user.add(stat);
	}
	
	public User getUser(ProxiedPlayer player) {
		for(User user : this.users) {
			if(user.player.equals(player)) {
				return user;
			}
		}
		
		return this.registerUser(player);
	}
}
