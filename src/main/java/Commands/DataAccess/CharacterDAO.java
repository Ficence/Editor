package Commands.DataAccess;

import Commands.DataAccess.Exception.DataAccessException;
import Commands.DataAccess.Model.OriginalCharacter;

import java.sql.*;

public class CharacterDAO {
    private Connection conn;
    public CharacterDAO(Connection conn){
        this.conn = conn;
    }

    public void insert(OriginalCharacter oc) throws DataAccessException {
        String sql = "INSERT INTO Character(characterId,userId,universe,name,gender,age) VALUES(?,?,?,?,?,?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, oc.getCharacterId());
            pstmt.setString(2, oc.getUserId());
            pstmt.setString(3, oc.getUniverse());
            pstmt.setString(4, oc.getName());
            pstmt.setString(5, oc.getGender());
            pstmt.setString(6, oc.getAge());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while inserting into the database");
        }
    }

    public void selectAll(){
        String sql = "SELECT userId, universe, name, gender, age FROM Characters";

        try (Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            // loop through the result set
            while (rs.next()) {
                System.out.println(rs.getString("characterId") + "\t" +
                        rs.getString("userId") + "\t" +
                        rs.getString("universe") + "\t" +
                        rs.getString("name") + "\t" +
                        rs.getString("gender") + "\t" +
                        rs.getString("age"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
