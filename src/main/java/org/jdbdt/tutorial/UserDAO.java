package org.jdbdt.tutorial;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO for user table.
 * 
 */
public final class UserDAO {

  /** Database connection. */
  private final Connection connection;

  /** 
   * Constructor.
   * @param c Database connection.
   */
  public UserDAO(Connection c) {
    connection = c;
  }
    
  /**
   * Create table.
   * @throws SQLException if a database error occurs.
   */
  public void createTable() throws SQLException {
    try(PreparedStatement stmt = stmt(DBOp.CREATE_TABLE)) {
      stmt.execute();
    } 
  }

  /** SQL for table insertion. */
  private static final String SQL_FOR_INSERT = 
      "INSERT INTO USERS(ID,LOGIN,NAME,PASSWORD,CREATED) VALUES (?,?,?,?,?)";

  /**
   * Insert an user.
   * @param u User data for insertion.
   * @throws SQLException if a database error occurs.
   */
  public final void insertUser(User u) throws SQLException {
    try(PreparedStatement s = connection.prepareStatement(SQL_FOR_INSERT)) {
      s.setInt(1, u.getId());
      s.setString(2, u.getLogin());
      s.setString(3, u.getName());
      s.setString(4, u.getPassword());
      s.setDate(5, u.getCreated());
      s.execute();
    }
  }

  /** SQL for complete user removal. */
  private static final String SQL_FOR_DELETE_ALL = "DELETE FROM USERS";
  
  /**
   * Delete all users.
   * @return The number of deleted users. 
   * @throws SQLException If a database error occurs.
   */
  public int deleteAll() throws SQLException {
    try (PreparedStatement stmt = connection.prepareStatement(SQL_FOR_DELETE_ALL)) {
      return stmt.executeUpdate();
    }   
  }

  /** SQL for single user removal. */
  private static final String SQL_FOR_DELETE = "DELETE FROM USERS WHERE ID=?";
  
  /**
   * Delete an user.
   * @param u User data.
   * @return <code>true</code> if user was deleted (<code>false</code>
   *    if the user could not be found)
   * @throws SQLException If a database error occurs.
   */
  public final boolean deleteUser(User u) throws SQLException {
    try(PreparedStatement s = connection.prepareStatement(SQL_FOR_DELETE)) {
      s.setInt(1, u.getId());
      return s.executeUpdate() == 1;
    }
  }

  /** 
   * SQL used for table update.
   */
  private static final String SQL_FOR_UPDATE = 
      "UPDATE USERS SET LOGIN=?,NAME=?,PASSWORD=?,CREATED=? WHERE ID=?";
  
  /**
   * Update user.
   * @param u User data for update.
   * @return <code>true</code> if update was successfull, <code>false</code> otherwise
   *    (user does not exist)
   * @throws SQLException if a database error occurs.
   */
  public final boolean updateUser(User u) throws SQLException {
    try (PreparedStatement s = connection.prepareStatement(SQL_FOR_UPDATE)) {
        s.setString(1, u.getLogin());
        s.setString(2, u.getName());
        s.setString(3, u.getPassword());
        s.setDate(4, u.getCreated());
        s.setInt(5, u.getId());
        return s.executeUpdate() == 1;
    }
  }

  /**
   * Get user by id.
   * @param id User id.
   * @return User object or <code>null</code>
   *         if the user does not exist.
   * @throws SQLException if a database error occurs.
   */
  public User getUser(int id) throws SQLException {
    try(PreparedStatement s = stmt(DBOp.SELECT_BY_ID)) {
      s.setInt(1, id);
      try (ResultSet rs = s.executeQuery()) {
        return rs.next() ? 
            new User(id,
                rs.getString(1), 
                rs.getString(2), 
                rs.getString(3),
                rs.getDate(4)) 
        : null;
      } 
    }
  }
  
  /**
   * Get user by login.
   * @param login User login.
   * @return User object or <code>null</code>
   *         if the user does not exist.
   * @throws SQLException if a database error occurs.
   */
  public User getUser(String login) throws SQLException {
    try(PreparedStatement s = stmt(DBOp.SELECT_BY_LOGIN)) {
      s.setString(1, login);
      try (ResultSet rs = s.executeQuery()) {
        return rs.next() ? 
            new User(rs.getInt(1),
                login,
                rs.getString(2), 
                rs.getString(3),
                rs.getDate(4)) 
        : null;
      } 
    }
  }

  /**
   * Get user count.
   * @return The number of users in the database.
   * @throws SQLException If a data
   */
  public int count() throws SQLException {
    try(PreparedStatement s= stmt(DBOp.COUNT);
        ResultSet rs = s.executeQuery()) {
      rs.next();
      return rs.getInt(1);
    } 
  }
  
  /**
   * Prepare statement for database operation.
   * @param op Operation.
   * @return A prepared statement.
   * @throws SQLException if a database error occurs.
   */
  private PreparedStatement stmt(DBOp op) throws SQLException {
    return connection.prepareStatement(op.getSQL());
  }
  
  /**
   * Enumeration for database operations along with
   * associated SQL code.
   */
  private enum DBOp { 
    /** Create table. */
    CREATE_TABLE("CREATE TABLE IF NOT EXISTS USERS ("
        + "ID INTEGER PRIMARY KEY NOT NULL,"
        + "LOGIN VARCHAR(10) UNIQUE NOT NULL,"
        + "NAME VARCHAR(40) NOT NULL, " 
        + "PASSWORD VARCHAR(32) NOT NULL,"
        + "CREATED DATE)"),
    /** Delete all users. */
    DELETE_ALL("DELETE FROM USERS"),
    /** Delete user. */
    DELETE("DELETE FROM USERS WHERE ID = ?"),
    /** Insert user. */
    INSERT("INSERT INTO USERS(ID,LOGIN,NAME,PASSWORD,CREATED) VALUES (?,?,?,?,?)"),
    /** Select user by id. */
    SELECT_BY_ID("SELECT LOGIN, NAME, PASSWORD, CREATED FROM USERS WHERE ID = ? "),
    /** Select user by login. */
    SELECT_BY_LOGIN("SELECT ID, NAME, PASSWORD, CREATED FROM USERS WHERE LOGIN = ? "),
    /** Update user. */
    UPDATE("UPDATE USERS SET LOGIN=?,NAME=?,PASSWORD=?,CREATED=? WHERE ID=?"),
    /** Get user count. */
    COUNT("SELECT COUNT(*) FROM USERS");

    /** SQL for operation. */
    private final String sql; 

    
    /** Constructor. 
     * @param sql SQL code.
     */
    DBOp(String sql) {
      this.sql = sql;
    }
    
    /** 
     * Get SQL. 
     * @return SQL code for operation.
     */
    String getSQL() {
      return sql;
    }
  }

  
}
