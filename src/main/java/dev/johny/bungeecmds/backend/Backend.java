package dev.johny.bungeecmds.backend;


import javax.sql.rowset.CachedRowSet;

;

public abstract class Backend {

  public abstract void closeConnection();

  public abstract void update(String sql, Object... vars);

  public abstract void execute(String sql, Object... vars);

  public abstract CachedRowSet query(String sql, Object... vars);
  
  private static Backend instance;
  
  public static void makeBackend() {
      instance = new MySQLBackend();
  }
  
  public static Backend getInstance() {
    return instance;
  }
}
