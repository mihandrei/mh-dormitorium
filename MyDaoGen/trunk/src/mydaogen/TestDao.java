package mydaogen;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import mh.dao.Users;
import mh.dao.UsersDB;

public class TestDao {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Connection con = DB.getDbConnection();
        UsersDB usrdb= new UsersDB();
        List<Users> usrs = usrdb.query(con, "where user_name=?","dog");
        Users usr = usrdb.getbypk(con, "dog");
        System.out.println(usrs);
        System.out.println(usr);
    }
}
