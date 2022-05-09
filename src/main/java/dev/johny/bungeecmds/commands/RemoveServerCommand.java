package dev.johny.bungeecmds.commands;

import dev.johny.bungeecmds.backend.Backend;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import javax.sql.rowset.CachedRowSet;
import java.io.IOException;

public class RemoveServerCommand extends Commands{
    public RemoveServerCommand() {
        super("removeserver");
    }
    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof ProxiedPlayer) {
            if(!sender.hasPermission("sbungeecmds.admin")) {
                sender.sendMessage(new TextComponent("§cVocê não tem permissão."));
                return;
            }
        }

        if(args.length <= 0) {
            sender.sendMessage(new TextComponent("§cUso correto: removeserver <nome> "));
            return;
        }
        CachedRowSet query = Backend.getInstance().query("SELECT * FROM `bungeecmds` WHERE servername='" + args[0] + "'");
        if(query != null) {
            sender.sendMessage(new TextComponent("§aO servidor foi removido com sucesso!"));
            Backend.getInstance().execute("DELETE FROM `bungeecmds` WHERE servername='" + args[0] + "'");
        } else {
            sender.sendMessage(new TextComponent("§cNão existe um sevidor com este nome."));
        }
        if (CommandSendCommand.rcon.getSocket().isConnected()) {
            try {
                CommandSendCommand.rcon.disconnect();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }





    }
}
