package fr.ekazuki.wscontroller;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class WSPluginListener implements Listener{
	
	private WSControllerPlugin plugin;

	public WSPluginListener(WSControllerPlugin plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onLogin(LoginEvent event) throws SQLException {
		if (plugin.whitelist) {
			if (!plugin.isWhitelisted(event.getConnection().getUniqueId())) {
				event.setCancelReason(new TextComponent("You are not whitelisted"));
				event.setCancelled(true);
			}
		}
	}
}
