package Commands;

import Commands.DataAccess.Database;
import Commands.DataAccess.Exception.DataAccessException;
import Commands.DataAccess.UserDAO;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class OCMaker {
    public OCMaker(String[] args, MessageReceivedEvent event){
        if(args[0].equalsIgnoreCase("oc")) {
            Database db = new Database();
            try {
                db.openConnection();
                new UserDAO(db.getConnection()).selectAll();
                db.closeConnection(true);
            }
            catch (DataAccessException e){
                try {
                    db.closeConnection(false);
                }
                catch (DataAccessException ex){
                    ex.printStackTrace();
                }
                e.printStackTrace();
            }
        }
    }
}
