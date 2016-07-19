package org.jdbdt.tutorial;

import java.io.DataInputStream;
import java.io.IOException;
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
    
  /** SQL script for table creation. */
  private static final String SQL_TABLE_SCRIPT = "/tableCreation.sql";

  /**
   * Create table.
   * @throws SQLException if a database error occurs.
   */
  public void createTable() throws SQLException {
    DataInputStream in = new DataInputStream(getClass().getResourceAsStream(SQL_TABLE_SCRIPT));
    try {
      byte[] fileContents = new byte[in.available()];
      in.readFully(fileContents);
      try(PreparedStatement stmt = connection.prepareStatement(new String(fileContents))) {
        stmt.execute();
      } 
    } 
    catch (IOException e) {
      throw new Error("Error reading SQL for table creation", e);
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
    try(PreparedStatement stmt = connection.prepareStatement(SQL_FOR_INSERT)) {
      stmt.setInt(1, u.getId());
      stmt.setString(2, u.getLogin());
      stmt.setString(3, u.getName());
      stmt.setString(4, u.getPassword());
      stmt.setDate(5, u.getCreated());
      stmt.execute();
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
    try(PreparedStatement stmt = connection.prepareStatement(SQL_FOR_DELETE)) {
      stmt.setInt(1, u.getId());
      return stmt.executeUpdate() == 1;
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
    try (PreparedStatement stmt = connection.prepareStatement(SQL_FOR_UPDATE)) {
        stmt.setString(1, u.getLogin());
        stmt.setString(2, u.getName());
        stmt.setString(3, u.getPassword());
        stmt.setDate(4, u.getCreated());
        stmt.setInt(5, u.getId());
        return stmt.executeUpdate() == 1;
    }
  }
  
  /** SQL for user queries by id. */
  private static final String 
  SQL_FOR_SELECT_BY_ID = "SELECT LOGIN, NAME, PASSWORD, CREATED FROM USERS WHERE ID = ? ";
  
  /**
   * Get user by id.
   * @param id User id.
   * @return User object or <code>null</code>
   *         if the user does not exist.
   * @throws SQLException if a database error occurs.
   */
  public User getUser(int id) throws SQLException {
    try(PreparedStatement stmt = connection.prepareStatement(SQL_FOR_SELECT_BY_ID)) {
      stmt.setInt(1, id);
      try (ResultSet rs = stmt.executeQuery()) {
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
  
  /** SQL for user queries by login. */
  private static final String 
  SQL_FOR_SELECT_BY_LOGIN = "SELECT ID, NAME, PASSWORD, CREATED FROM USERS WHERE LOGIN = ? ";

  /**
   * Get user by login.
   * @param login User login.
   * @return User object or <code>null</code>
   *         if the user does not exist.
   * @throws SQLException if a database error occurs.
   */
  public User getUser(String login) throws SQLException {
    try(PreparedStatement stmt = connection.prepareStatement(SQL_FOR_SELECT_BY_LOGIN)) {
      stmt.setString(1, login);
      try (ResultSet rs = stmt.executeQuery()) {
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
}
