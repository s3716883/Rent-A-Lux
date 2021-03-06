package dao;

import controller.util.Utils;
import dao.util.DatabaseUtils;
import model.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

public class UserDAO {
    public static User createUser(String email, String password,String first_name,String last_name, int account_type) {
        int user_id = 0;
        String hashedPass = Utils.generateHashPassword(password);
        String update_sql;
        update_sql = "INSERT INTO `rentalux`.`Users` ( `email`, `password`, `first_name`,`last_name`,`account_type`) " +
                "VALUES('" + email + "' ,'" + hashedPass + "' ,'" + first_name + "','" +
                last_name + "'," + account_type + ");";

        try {
            // Execute the query
            Connection connection = DatabaseUtils.connectToDatabase();
            Statement statement = connection.createStatement();
            statement.executeUpdate(update_sql, Statement.RETURN_GENERATED_KEYS);
            // Extract user_id
            ResultSet result = statement.getGeneratedKeys();
            result.next();
            user_id = result.getInt(1);
            // Close it
            DatabaseUtils.closeConnection(connection);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        // Create the user object
        User user = new User(user_id, email, hashedPass, first_name, last_name, account_type);
        return user;
    }

    public static boolean editUser(int user_id, HashMap<String, String> props) {
        if (props.size()>0) {
            String sql;
            sql = "UPDATE `rentalux`.`users` SET ";
            Iterator propIterator = props.entrySet().iterator();
            while (propIterator.hasNext()) {
                Map.Entry prop = (Map.Entry) propIterator.next();
                sql += "`" + prop.getKey() + "` = '" + prop.getValue() + "',";
            }
            sql = sql.substring(0, sql.length() - 1);
            sql += " WHERE `user_id` = " + user_id + ";";
            System.out.println(sql);
            try {
                // Execute the query
                Connection connection = DatabaseUtils.connectToDatabase();
                Statement statement = connection.createStatement();
                statement.executeUpdate(sql);
                // Close it
                DatabaseUtils.closeConnection(connection);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        else{
            System.out.println("UpdateUser DAO method was called, but there were no props provided");
        }
        return true;
    }

    public static User checkLogin(String email, String password) {
        //Returns 2 if employee, 3 if admin, 0 if none (in future 1 will be customer)
        try {
            // Here you prepare your sql statement
            String sql = "SELECT  `email`, `password` FROM `rentalux`.Users WHERE `email` = '" + email + "';";
            // Execute the query
            Connection connection = DatabaseUtils.connectToDatabase();
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql);

            // If there is a result, that means that the email matches.
            if(result.next()) {
                // 2) Check if the password matches
                if (Utils.passwordIsValid(password, result.getString("password"))){
                    return getPersonByEmail(result.getString("email"));
                }
                else{
                    System.err.println("Wrong password");
                }
            }
            else{
                System.err.println("no result");
            }

            // Close it
            DatabaseUtils.closeConnection(connection);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static User getPersonByEmail(String email) {
        List<User> users = new ArrayList<>();

        try {
            // Here you prepare your sql statement
            String sql = "SELECT * FROM `rentalux`.Users WHERE email = '" + email + "';";
            // Execute the query
            Connection connection = DatabaseUtils.connectToDatabase();
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql);

            // If you have multiple results, you do a while
            if(result.next()) {
                // 2) Add it to the list we have prepared
                users.add(new User(result.getInt("user_id"),result.getString("email"),result.getString("password"),
                                    result.getString("first_name"),result.getString("last_name"), result.getInt("account_type")));
            }

            // Close it
            DatabaseUtils.closeConnection(connection);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if(!users.isEmpty()) {
            return users.get(0);
        }
        // If we are here, something bad happened
        return null;
    }
    public static boolean emailInUse(String email) {
        try {
            // Here you prepare your sql statement
            String sql = "SELECT `email` FROM `rentalux`.Users WHERE `email` = '" + email + "';";

            // Execute the query
            Connection connection = DatabaseUtils.connectToDatabase();
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql);

            // If there is a result, that means that the email matches.
            if(result.next()) {
                return true;
            }

            // Close it
            DatabaseUtils.closeConnection(connection);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static ArrayList<User> getUsers() {
        ArrayList<User> users = new ArrayList<User>();
        try {
            // Here you prepare your sql statement
            String sql = "SELECT * FROM `rentalux`.Users";

            // Execute the query
            Connection connection = DatabaseUtils.connectToDatabase();
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql);

            // If there is a result, that means that the email matches.
            while(result.next()) {
                users.add(new User(result.getInt("user_id"),result.getString("email"),result.getString("password"),result.getString("first_name"),result.getString("last_name"),result.getInt("account_type")));
            }

            // Close it
            DatabaseUtils.closeConnection(connection);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (users.size()>0){
            return users;
        }
        else{
            return null;
        }
    }

    public static User getUser(int user_id) {
        ArrayList<User> users = new ArrayList<User>();
        try {
            // Here you prepare your sql statement
            String sql = "SELECT * FROM `rentalux`.Users WHERE `user_id` = " + user_id;

            // Execute the query
            Connection connection = DatabaseUtils.connectToDatabase();
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql);

            // If there is a result, that means that the email matches.
            while(result.next()) {
                users.add(new User(result.getInt("user_id"),result.getString("email"),result.getString("password"),result.getString("first_name"),result.getString("last_name"),result.getInt("account_type")));
            }

            // Close it
            DatabaseUtils.closeConnection(connection);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (users.size()>0){
            return users.get(0);
        }
        else{
            return null;
        }
    }

    public static boolean removeUser(int user_id) {
        String update_sql;
        update_sql = "DELETE FROM `rentalux`.`users` WHERE `user_id` = '" + user_id + "';";
        try {
            // Execute the query
            Connection connection = DatabaseUtils.connectToDatabase();
            Statement statement = connection.createStatement();
            statement.executeUpdate(update_sql);
            // Close it
            DatabaseUtils.closeConnection(connection);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
