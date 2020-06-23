package fr.ekazuki.wscontroller.game;

import org.json.JSONException;

import fr.ekazuki.wscontroller.WSControllerPlugin;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class GameCommand extends Command {

	MurderPM manager;
	
	public GameCommand(String name) {
		super(name);
		this.manager = WSControllerPlugin.INSTANCE.murderManager;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (args.length >= 1) {
			if(args[0].equals("stop")) {
				
				this.manager.stopGame();
				
			} else if(args[0].equals("start")) {
				
				this.manager.startGame();
				
			} else if(args[0].equals("getstate")) {
				
				try {
					this.manager.getState();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}
		}
		
	}

}
