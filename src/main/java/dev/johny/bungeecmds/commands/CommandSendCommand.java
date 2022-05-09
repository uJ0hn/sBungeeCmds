package dev.johny.bungeecmds.commands;

import com.google.common.base.Joiner;
import dev.j0hny.rconglobal.Rcon;
import dev.j0hny.rconglobal.exception.AuthenticationException;
import dev.johny.bungeecmds.backend.Backend;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import javax.sql.rowset.CachedRowSet;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;

public class CommandSendCommand extends Commands{
    public static Rcon rcon;
    public CommandSendCommand() {
        super("sendcommandbungee");
    }
    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof ProxiedPlayer) {
            if(!sender.hasPermission("sbungeecmds.admin")) {
                sender.sendMessage(new TextComponent("§cVocê não tem permissão."));
                return;
            }
        }
        if(args.length <= 1) {
            sender.sendMessage(new TextComponent("§cUso correto: sendcommandbungee <servidor> <comando>"));
            return;
        }
        CachedRowSet query = Backend.getInstance().query("SELECT * FROM `bungeecmds` WHERE servername='" + args[0] + "'");
        if(query != null) {
            String argsresult = Joiner.on(' ').join(Arrays.copyOfRange(args, 1, args.length));
            String ip;
            int port;
            String passwd;
            try {
                ip = query.getString("host");
                port = Integer.parseInt(query.getString("port"));
                passwd = query.getString("passwd");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            try {
                rcon = new Rcon(ip, port, passwd.getBytes());
            } catch (IOException | AuthenticationException e) {
                throw new RuntimeException(e);
            }
            String result;
            try {
                result=rcon.comando(argsresult);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            sender.sendMessage(new TextComponent(argsresult));
            sender.sendMessage(new TextComponent(result));


        } else  {
            sender.sendMessage(new TextComponent("§cEsse servidor não existe."));
        }



    }
}
