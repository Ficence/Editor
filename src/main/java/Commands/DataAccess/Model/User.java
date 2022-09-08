package Commands.DataAccess.Model;

public record User(String userId) {

    public String getUserId() {
        return userId;
    }
}
