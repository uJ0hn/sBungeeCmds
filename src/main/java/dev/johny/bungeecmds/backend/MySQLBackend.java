package dev.johny.bungeecmds.backend;


import dev.johny.bungeecmds.Main;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.YamlConfiguration;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.sql.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MySQLBackend extends Backend {

  private Connection connection;
  private final ExecutorService executor;
  private final String host;
  private final String port;
  private final String database;
  private final String username;
  private final String password;

  public MySQLBackend() {
    File file = new File("plugins/sBungeeCmds/config.yml");
    Configuration config;
    try {
      config = YamlConfiguration.getProvider(YamlConfiguration.class).load(new InputStreamReader(Files.newInputStream(file.toPath()), "UTF-8"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    this.host = config.getString("mysql.host");
    this.port = config.getString("mysql.porta");
    this.database = config.getString("mysql.db");
    this.username = config.getString("mysql.user");
    this.password = config.getString("mysql.senha");

    this.executor = Executors.newCachedThreadPool();
    openConnection();

    update("CREATE TABLE IF NOT EXISTS bungeecmds ("
            + "servername VARCHAR(36),"
            + "host VARCHAR(36),"
            + "port VARCHAR(36),"
            + "passwd VARCHAR(36),"
            + "PRIMARY KEY(servername)) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE utf8_bin;");

  }

  public Connection getConnection() {
    if (!isConnected()) {
      openConnection();

    }

    return connection;
  }

  public void closeConnection() {
    if (isConnected()) {
      try {
        connection.close();
      } catch (SQLException e) {
        System.out.println("Cannot close MySQL connection: " + e);
      }
    }
  }

  public boolean isConnected() {
    try {
      return connection != null && !connection.isClosed() && connection.isValid(5);
    } catch (SQLException e) {
      System.out.println("MySQL error: " + e);
    }

    return false;
  }

  public void openConnection() {
    if (!isConnected()) {
      try {
        boolean bol = connection == null;
        connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database
                        + "?verifyServerCertificate=false&useSSL=false&useUnicode=yes&characterEncoding=UTF-8",
                username, password);
        if (bol) {
          BungeeCord.getInstance().getConsole().sendMessage(new TextComponent("§6[sBungeeCmds] Conectado ao mysql sucesso"));
          return;
        }

        BungeeCord.getInstance().getConsole().sendMessage(new TextComponent("§6[sBungeeCmds] Reconectado com o MySQL!"));
      } catch (SQLException e) {
        System.out.println("Cannot open MySQL connection: " + e);
      }
    }
  }

  public void update(String sql, Object... vars) {
    try {
      PreparedStatement ps = prepareStatement(sql, vars);
      ps.execute();
      ps.close();
    } catch (SQLException e) {
      System.out.println("Cannot execute SQL: " + e);
    }
  }

  public void execute(String sql, Object... vars) {
    executor.execute(() -> update(sql, vars));
  }

  public PreparedStatement prepareStatement(String query, Object... vars) {
    try {
      PreparedStatement ps = getConnection().prepareStatement(query);
      for (int i = 0; i < vars.length; i++) {
        ps.setObject(i + 1, vars[i]);
      }
      return ps;
    } catch (SQLException e) {
      System.out.println("Cannot Prepare Statement: " +e);
    }

    return null;
  }

  public CachedRowSet query(String query, Object... vars) {
    CachedRowSet rowSet = null;
    try {
      Future<CachedRowSet> future = executor.submit(() -> {
        try {
          PreparedStatement ps = prepareStatement(query, vars);

          ResultSet rs = ps.executeQuery();
          CachedRowSet crs = RowSetProvider.newFactory().createCachedRowSet();
          crs.populate(rs);
          rs.close();
          ps.close();

          if (crs.next()) {
            return crs;
          }
        } catch (Exception e) {
          System.out.println("Cannot execute Query: " + e);
        }

        return null;
      });

      if (future.get() != null) {
        rowSet = future.get();
      }
    } catch (Exception e) {
      System.out.println("Cannot call FutureTask: " + e);
    }

    return rowSet;
  }
}
