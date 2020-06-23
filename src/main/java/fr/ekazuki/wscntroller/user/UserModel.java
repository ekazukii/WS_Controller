package fr.ekazuki.wscntroller.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import fr.ekazuki.wscontroller.WSControllerPlugin;

public class UserModel {

	public Statement statement;
	
	public UserModel() {
		statement = WSControllerPlugin.STATEMENT;
	}
	
	public void addUser(UUID uuid, String username) {
		try {
			statement.executeUpdate("INSERT INTO mc_users (uuid, username) VALUES ('"+uuid.toString()+"', '"+username+"')");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int getStat(UUID uuid, StatType stat) {
		try {
			
			ResultSet result = statement.executeQuery("SELECT '"+stat.sqlClm+"' FROM mc_users");

			while (result.next()) {
			    return result.getInt(stat.sqlClm);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return 0;
	}
	
	public void addStat(UUID uuid, StatType stat) {
		try {
			statement.executeUpdate("UPDATE mc_users SEMT "+stat.sqlClm+" = "+stat.sqlClm+" + 1 WHERE uuid = '"+uuid.toString()+"'");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
