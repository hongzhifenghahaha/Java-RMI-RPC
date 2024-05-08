package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface <em>IRemoteMeeting</em> is a interface provides the access to the RMI object
 *
 * @author Sun Shuo
 */
public interface IRemoteMeeting extends Remote {
    /**
     * Registration service for the user
     * @param username name of the user
     * @param password password of the user
     * @return the registration result
     */
    String register(String username, String password) throws RemoteException;

    /**
     * Login service for the user
     * @param username name of the user
     * @param password password of the user
     * @return the login result
     */
    String login(String username, String password) throws RemoteException;

    /**
     * List all users and meetings in the database
     * @return user list and meeting list
     */
    String listUsersAndMeetings() throws RemoteException;

    /**
     * Try to add a new meeting with otherUsername in [startTime, endTime]
     * @param username name of meeting creator
     * @param otherUsername name of meeting participant
     * @param startTime start time of the meeting
     * @param endTime end time of the meeting
     * @param title title of the meeting
     * @return operation result
     */
    String addMeeting(String username, String otherUsername, String startTime, String endTime, String title) throws RemoteException;

    /**
     * Delete meeting created by the user with id 'meetingID'
     * @param username name of user
     * @param meetingID meeting of the meeting to be deleted
     * @return operation result
     */
    String deleteMeeting(String username, String meetingID) throws RemoteException;

    /**
     * Delete all meeting created by user
     * @param username name of the user
     */
    String clearMeeting(String username) throws RemoteException;

    /**
     * Query all meeting in [startTime, endTime]
     * @param startTime start time of the interval
     * @param endTime end time of the interval
     * @return operation result
     */
    String queryMeeting(String startTime, String endTime) throws RemoteException;
}
