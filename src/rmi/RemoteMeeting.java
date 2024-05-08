package rmi;

import database.SystemDataBase;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/**
 * Class <em>RemoteMeeting</em> is a class implements the IRemoteMeeting interface and provide services
 *
 * @author Sun Shuo
 */
public class RemoteMeeting extends UnicastRemoteObject implements IRemoteMeeting {
    // 日期格式转化类
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
    // 数据库访问对象，通过jdbc与数据库交互
    private final SystemDataBase db = new SystemDataBase();

    /**
     * 构造函数
     */
    public RemoteMeeting() throws RemoteException {
        sdf.setLenient(false);
    }

    /**
     * 用户注册服务
     * @param username name of the user
     * @param password password of the user
     * @return the registration result
     */
    @Override
    public String register(String username, String password) throws RemoteException {
        if (db.isUsernameExist(username)) {
            return "Register Error: username '" + username + "' already exists, please try again";
        } else {
            db.insertUser(username, password);
            return "Successful!";
        }
    }

    /**
     * 用户登录服务
     * @param username name of the user
     * @param password password of the user
     * @return the login result
     */
    @Override
    public String login(String username, String password) throws RemoteException {
        if (!db.isUserMatch(username, password)) {
            return "Login Error: incorrect username or password, please try again";
        } else {
            return "Successful!";
        }
    }

    /**
     * 列出所有用户和所有会议
     * @return user list and meeting list
     */
    @Override
    public String listUsersAndMeetings() throws RemoteException {
        StringBuilder listString = new StringBuilder();

        /*
         * 把用户添加到 listString 中
         */
        listString.append("User List:\n");
        listString.append("No.  userName\n");

        ArrayList<String> usernameList = db.getAllUser();
        Iterator<String> userIterator = usernameList.iterator();
        int count = 1;
        // 通过迭代器一行一行打印用户信息
        while (userIterator.hasNext()) {
            listString.append(String.format("%-3d:  %s\n", count, userIterator.next()));
            count++;
        }

        /*
         * 把会议添加到 listString 中
         */
        listString.append("\nMeeting List:\n");
        listString.append(String.format("%-15s%-20s%-20s%-15s%-15s%-15s\n",
                "meeting_id", "startTime","endTime","title","creator", "participant"));

        ArrayList<String> meetingList = db.getAllMeetingOrderByIDDesc();
        Iterator<String> meetingIterator = meetingList.iterator();
        // 通过迭代器一行一行打印会议信息
        while (meetingIterator.hasNext()){
            listString.append(meetingIterator.next());
        }

        /*
         * return 返回list结果
         */
        return listString.toString();
    }

    /**
     * 尝试与另一个用户在 [startTime, endTime] 时间范围内开会
     * @param username name of meeting creator
     * @param otherUsername name of meeting participant
     * @param startTime start time of the meeting
     * @param endTime end time of the meeting
     * @param title title of the meeting
     * @return operation result
     */
    @Override
    public String addMeeting(String username, String otherUsername, String startTime, String endTime, String title) throws RemoteException {
        if (!db.isUsernameExist(otherUsername)) {
            return "Error: user'" + otherUsername + "' is not exist";
        }

        if (username.equals(otherUsername)){
            return "Error: you could not add a meeting with yourself!";
        }

        try {
            Date startDate = sdf.parse(startTime);
            Date endDate = sdf.parse(endTime);

            if (startDate.after(endDate)) {
                return "Error: startTime after endTime";
            }

            startTime = startTime.replace('T',' ');
            endTime = endTime.replace('T',' ');
 
            if (!db.userHasFreeTime(username, startTime, endTime)) {
                return "Failed: user '" + username + "' has no free time!";
            }

            if (!db.userHasFreeTime(otherUsername, startTime, endTime)) {
                return "Failed: user '" + otherUsername + "' has no free time!";
            }

            /*
             * 插入会议
             */
            db.insertMeeting(title, username, otherUsername, startTime, endTime);
            return "Successfully add a meeting with '" + otherUsername + "'\n";

        } catch (ParseException pe) {
            return "Error: time format error. Please try again";
        }
    }

    /**
     * 删除被该用户创建的，会议id为 'meetingID' 的会议
     * @param username name of user
     * @param meetingID meeting of the meeting to be deleted
     * @return operation result
     */
    @Override
    public String deleteMeeting(String username, String meetingID) throws RemoteException {
        if (!db.isMeetingExist(meetingID)){
            return "Failed: the meeting with ID '" + meetingID + "' does not exist";
        }

        if (!db.isMeetingCreatedByUser(username,meetingID)){
            return "Failed: the meeting with ID '" + meetingID + "' is not created by user '" + username + "'";
        }

        // 删除会议
        db.deleteMeetingByID(username, meetingID);
        return "Successful: the meeting with ID '" + meetingID + "' has been deleted";
    }

    /**
     * 清除该用户创建的会议
     * @param username name of the user
     */
    @Override
    public String clearMeeting(String username) throws RemoteException {
        db.clearAllMeetingCreatedByUser(username);
        return "Done.";
    }

    /**
     * 查询 [startTime, endTime] 返回内的会议
     * @param startTime start time of the interval
     * @param endTime end time of the interval
     * @return operation result
     */
    @Override
    public String queryMeeting(String startTime, String endTime) throws RemoteException {
        try {
            Date startDate = sdf.parse(startTime);
            Date endDate = sdf.parse(endTime);

            if (startDate.after(endDate)) {
                return "Error: startTime after endTime";
            }

            startTime = startTime.replace('T',' ');
            endTime = endTime.replace('T',' ');

            StringBuilder meetingString = new StringBuilder();
            /**
             * append meetings to listString
             */
            meetingString.append("\n");
            meetingString.append("Meeting List:\n");
            meetingString.append(String.format("%-15s%-20s%-20s%-15s%-15s%-15s\n",
                    "meeting_id", "startTime","endTime","title","creator", "participant"));

            ArrayList<String> meetingList = db.getAllMeetingOrderByStartTime(startTime,endTime);
            Iterator<String> meetingIterator = meetingList.iterator();
            while (meetingIterator.hasNext()){
                meetingString.append(meetingIterator.next());
            }
            return meetingString.toString();

        } catch (ParseException pe) {
            return "Error: time format error. Please input yyyy-MM-ddTHH:mm";
        }
    }

}
