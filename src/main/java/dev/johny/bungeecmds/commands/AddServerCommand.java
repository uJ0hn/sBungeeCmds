package dev.johny.bungeecmds.commands;

import dev.johny.bungeecmds.backend.Backend;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import javax.sql.rowset.CachedRowSet;

public class AddServerCommand extends Commands{
    public AddServerCommand() {
        super("addserver");
    }
    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof ProxiedPlayer) {
            if(!sender.hasPermission("sbungeecmds.admin")) {
                sender.sendMessage(new TextComponent("§cVocê não tem permissão."));
                return;
            }
        }
        if(args.length <= 3) {
            sender.sendMessage(new TextComponent("§cUso correto: addserver <nome> <ip> <porta> <senha>"));
            return;
        }
        CachedRowSet query = Backend.getInstance().query("SELECT * FROM `bungeecmds` WHERE servername='" + args[0] + "'");
        if(query == null) {
            String nome = args[0];
            String ip = args[1];
            int porta = Integer.parseInt(args[2]);
            String senha = args[3];
            if(ip.contains(":")) {
                sender.sendMessage(new TextComponent("§cO ip precisa ser valido"));
                return;
            }
            sender.sendMessage(new TextComponent("§aO servidor foi adicionado com sucesso!"));
            Backend.getInstance().execute("INSERT INTO `bungeecmds` VALUES (?, ?, ?, ?)", nome, ip, porta, senha);
        } else {
            sender.sendMessage(new TextComponent("§cJa existe um sevidor com este nome."));
        }





    }
}
