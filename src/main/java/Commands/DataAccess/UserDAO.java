package Commands.DataAccess;

import Commands.DataAccess.Exception.DataAccessException;
import Commands.DataAccess.Model.User;

import java.sql.*;

public class UserDAO {
    private final Connection conn;

    public UserDAO(Connection conn){
        this.conn = conn;
    }

    public void insert(User user) throws DataAccessException {
        String sql = "INSERT INTO User(userId) VALUES(?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUserId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while inserting into the database");
        }
    }

    public void delete(String userId) throws SQLException {
        String sql = "DELETE FROM User WHERE userId = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1,userId);
            // execute the delete statement
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
    }

    public void selectAll(){
        String sql = "SELECT userId FROM User";

        try (Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            // loop through the result set
            while (rs.next()) {
                System.out.println(rs.getString("userId") + "\t");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
