package Commands.DataAccess.Model;

public record OriginalCharacter(String characterId, String userId, String universe, String name,
                                String gender, String age) {

    public String getCharacterId(){
        return characterId;
    }

    public String getUserId() {
        return userId;
    }

    public String getUniverse() {
        return universe;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public String getAge() {
        return age;
    }
}
