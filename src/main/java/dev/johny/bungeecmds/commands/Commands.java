package dev.johny.bungeecmds.commands;

import dev.johny.bungeecmds.Main;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;

public abstract class Commands extends Command {

  public Commands(String name, String... aliases) {
    super(name, null, aliases);
    ProxyServer.getInstance().getPluginManager().registerCommand(Main.getInstance(), this);
  }

  public static void makeCommands() {
    new AddServerCommand();
    new CommandSendCommand();
    new RemoveServerCommand();
  }
}
