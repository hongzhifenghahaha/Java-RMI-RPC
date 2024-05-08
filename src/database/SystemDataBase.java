package database;

import java.sql.*;
import java.util.ArrayList;

/**
 * Class <em>SystemDataBase</em> is a class provides operation with database
 *
 * @author Sun Shuo
 */
public class SystemDataBase {
//    private final String JDBC_DRIVER = "org.sqlite.JDBC";
    private Connection conn;

    /**
     * SystemDataBase constructor
     */
    public SystemDataBase(){
        try {
//            Class.forName(JDBC_DRIVER);
            String sqlUrl = "jdbc:sqlite:C:\\Users\\92082\\Desktop\\wps云同步\\大三\\大三上\\课程\\网络与分布计算\\洪志峰实验\\实验三\\Experiment3\\exercise3\\SystemInformation.db";
            conn = DriverManager.getConnection(sqlUrl);
        } catch (SQLException e) {
            System.out.println("Error Message: " + e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Determine whether username and password are correct
     * @param username name of user
     * @param password password of user
     */
    public boolean isUserMatch(String username, String password){
        boolean isMatch = false;
        try {
            String sql = "SELECT * FROM user WHERE name=? AND password=?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1,username);
            preparedStatement.setString(2,password);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()){
                isMatch = true;
            }
        } catch (SQLException e) {
            System.out.println("SQL ERROR MESSAGE: " + e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
        }
        return isMatch;
    }

    /**
     * Determine whether user has exist in the database
     * @param username name of user
     */
    public boolean isUsernameExist(String username) {
        boolean isExist = false;
        try {
            String sql = "SELECT * FROM user WHERE name=?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1,username);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()){
                isExist = true;
            }
        } catch (SQLException e) {
            System.out.println("SQL ERROR MESSAGE: " + e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
        }
        return isExist;
    }

    /**
     * Insert new user into the database
     * @param username name of user
     * @param password password of user
     */
    public void insertUser(String username,String password){
        try {
            String sql = "INSERT INTO user VALUES (?,?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1,username);
            preparedStatement.setString(2,password);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("SQL ERROR MESSAGE: " + e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Get all user
     * @return user list
     */
    public ArrayList<String> getAllUser(){
        ArrayList<String> userList = new ArrayList<String>();
        try {
            String sql = "SELECT * FROM user";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                userList.add(rs.getString("name"));
            }
        } catch (SQLException e){
            System.out.println("SQL ERROR MESSAGE: " + e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
        }
        return userList;
    }

    /**
     * Determine whether user has free time in [startTime, endTime]
     * @param username name of user
     * @param startTime time to start meeting
     * @param endTime time to end meeting
     */
    public boolean userHasFreeTime(String username, String startTime, String endTime){
        boolean isFree = true;
        try {
            String sql = "SELECT * FROM meeting\n" +
                    "WHERE (creator = ? OR participant = ?)\n" +
                    "      AND\n" +
                    "      ( (start_time >= ? AND start_time < ?) \n" +
                    "             OR\n" +
                    "        (end_time > ? AND end_time <= ?)  ) ";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1,username);
            preparedStatement.setString(2,username);
            preparedStatement.setString(3,startTime);
            preparedStatement.setString(4,endTime);
            preparedStatement.setString(5,startTime);
            preparedStatement.setString(6,endTime);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()){
                isFree = false;
            }
        } catch (SQLException e) {
            System.out.println("SQL ERROR MESSAGE: " + e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
        }
        return isFree;
    }

    /**
     * Insert new meeting into the database
     */
    public void insertMeeting(String title,String creator,String participant,String startTime,String endTime){
        try {
            String sql = "INSERT INTO meeting(title, creator, participant, start_time, end_time) VALUES (?,?,?,?,?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1,title);
            preparedStatement.setString(2,creator);
            preparedStatement.setString(3,participant);
            preparedStatement.setString(4,startTime);
            preparedStatement.setString(5,endTime);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("SQL ERROR MESSAGE: " + e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Get all meeting by id in descending order
     */
    public ArrayList<String> getAllMeetingOrderByIDDesc(){
        ArrayList<String> meetingList = new ArrayList<String>();
        try {
            String sql = "SELECT * FROM meeting ORDER BY meeting_id DESC";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                meetingList.add(String.format("%-15s%-20s%-20s%-15s%-15s%-15s\n",rs.getString("meeting_id"),
                        rs.getString("start_time"),rs.getString("end_time"),rs.getString("title"),
                        rs.getString("creator"), rs.getString("participant")));
            }
        } catch (SQLException e){
            System.out.println("SQL ERROR MESSAGE: " + e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
        }
        return meetingList;
    }

    /**
     * Get all meeting in [startTime, endTime] order by startTime
     */
    public ArrayList<String> getAllMeetingOrderByStartTime(String startTime, String endTime){
        ArrayList<String> meetingList = new ArrayList<String>();
        try {
            String sql = "SELECT * FROM meeting WHERE start_time >= ? AND end_time <= ? ORDER BY start_time";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1,startTime);
            preparedStatement.setString(2,endTime);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                meetingList.add(String.format("%-15s%-20s%-20s%-15s%-15s%-15s\n",rs.getString("meeting_id"),
                        rs.getString("start_time"),rs.getString("end_time"),rs.getString("title"),
                        rs.getString("creator"), rs.getString("participant")));
            }
        } catch (SQLException e){
            System.out.println("SQL ERROR MESSAGE: " + e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
        }
        return meetingList;
    }

    /**
     * Determine whether the meeting exists with the id 'meetingID'
     * @param meetingID id of the meeting
     */
    public boolean isMeetingExist(String meetingID) {
        boolean isExist = false;
        try {
            String sql = "SELECT * FROM meeting WHERE meeting_id=?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1,meetingID);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()){
                isExist = true;
            }
        } catch (SQLException e) {
            System.out.println("SQL ERROR MESSAGE: " + e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
        }
        return isExist;
    }

    /**
     * Determine whether the meeting is created by the user
     * @param username name of the user
     * @param meetingID id of the meeting
     */
    public boolean isMeetingCreatedByUser(String username, String meetingID) {
        boolean isCreator = false;
        try {
            String sql = "SELECT * FROM meeting WHERE creator = ? AND meeting_id=?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1,username);
            preparedStatement.setString(2,meetingID);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()){
                isCreator = true;
            }
        } catch (SQLException e) {
            System.out.println("SQL ERROR MESSAGE: " + e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
        }
        return isCreator;
    }

    /**
     * Delete meeting by id created by the user
     * @param username name of the user who created the meeting
     * @param meetingID id of the meeting to be deleted
     */
    public void deleteMeetingByID(String username, String meetingID){
        try {
            String sql = "DELETE FROM meeting where creator = ? AND meeting_id = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1,username);
            preparedStatement.setString(2,meetingID);
            preparedStatement.executeUpdate();
        } catch (SQLException e){
            System.out.println("SQL ERROR MESSAGE: " + e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Delete all meeting created by the user
     * @param username name of the user
     */
    public void clearAllMeetingCreatedByUser(String username){
        try {
            String sql = "DELETE FROM meeting where creator = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1,username);
            preparedStatement.executeUpdate();
        } catch (SQLException e){
            System.out.println("SQL ERROR MESSAGE: " + e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Clean all tables of the database
     */
    public void cleanAllTable(){
        try {
            String sql = "DELETE FROM user;DELETE FROM meeting;";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.executeUpdate();
        } catch (SQLException e){
            System.out.println("SQL ERROR MESSAGE: " + e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
