package client;

import rmi.IRemoteMeeting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Class <em>Client</em> is a class provides the Client interface
 *
 * @author Sun Shuo
 */
public class Client {
    public static BufferedReader keyboard;
    private IRemoteMeeting remoteMeeting;  // 注意，是IRemoteMeeting，而不是RemoteMeeting
    private boolean remoteMeetingExist;  // 避免注册失败时多次创建remoteMeeting
    private String nowUsername;  // 记录用户名

    /**
     * Client constructor
     */
    public Client() {
        keyboard = new BufferedReader(new InputStreamReader(System.in));
        remoteMeetingExist = false;
    }

    /**
     * Main process starts the client
     */
    public static void main(String[] args) throws IOException {
        try {
            Client client = new Client();
            client.registerOrLogin();
            client.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Try to register or login
     */
    public void registerOrLogin() throws IOException {
        /*
         * get params from keyboard
         */
        System.out.println("\nRegister:\n" +
                "\tusage: java <clientName> <serverName> <portNumber> register <userName> <password>");
        System.out.println("Login:\n" +
                "\tusage: java <clientName> <serverName> <portNumber> login <userName> <password>");
        String command = keyboard.readLine().trim();
        String[] params = command.split("\\s+");
        while (params.length != 7) {
            System.out.println("Error: incorrect command, please register or login again\n");
            System.out.println("\nRegister:\n" +
                    "\tusage: java <clientName> <serverName> <portNumber> register <userName> <password>");
            System.out.println("Login:\n" +
                    "\tusage: java <clientName> <serverName> <portNumber> login <userName> <password>");
            command = keyboard.readLine().trim();
            params = command.split("\\s+");
        }

        // get the rmi object
        getRemoteObject(params[2]);

        /**
         * print results
         */
        String result;
        if (params[4].equals("register")) {
            result = remoteMeeting.register(params[5], params[6]);
        } else if (params[4].equals("login")) {
            result = remoteMeeting.login(params[5], params[6]);
        } else {
            result = "Error: incorrect param, please enter register or login";
        }
        System.out.println(result);
        if (!result.equals("Successful!")) {
            registerOrLogin();
        } else {
            nowUsername = params[5];
        }
    }

    /**
     * Run client, print the menu and handle operations
     */
    public void run() throws IOException {
        printMenu();

        while (true) {
            System.out.println("\nInput an operation:");
            String command = keyboard.readLine().trim();
            String[] params = command.split("\\s+");

            // check whether arguments is legal
            if (!checkArguments(params[0], params.length)) {
                continue;
            }

            // handle different operations
            switch (params[0]) {
                case "list":
                    System.out.println(remoteMeeting.listUsersAndMeetings());
                    break;
                case "add":
                    System.out.println(remoteMeeting.addMeeting(nowUsername, params[1], params[2], params[3], params[4]));
                    break;
                case "delete":
                    System.out.println(remoteMeeting.deleteMeeting(nowUsername, params[1]));
                    break;
                case "clear":
                    System.out.println(remoteMeeting.clearMeeting(nowUsername));
                    break;
                case "query":
                    System.out.println(remoteMeeting.queryMeeting(params[1], params[2]));
                    break;
                case "help":
                    printMenu();
                    break;
                case "quit":
                    System.out.println("quit successfully");
                    return;
                default:
                    System.out.println("Error: incorrect command. Please try again");
            }
        }
    }

    /**
     * get remote object by host
     * @param host host of RMI server
     */
    public void getRemoteObject(String host) {
        if (remoteMeetingExist) {
            return;
        }

        try {
            String bindName = "remoteMeeting";
            Registry registry = LocateRegistry.getRegistry(host);
            remoteMeeting = (IRemoteMeeting) registry.lookup(bindName);
            remoteMeetingExist = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * check whether arguments is legal
     * @param command command got from keyboard
     * @param paramLength length of parameters
     */
    public boolean checkArguments(String command, int paramLength) {
        boolean checkPassed = true;
        switch (command) {
            case "list":
            case "clear":
            case "help":
            case "quit":
                if (paramLength != 1) {
                    checkPassed = false;
                }
                break;
            case "add":
                if (paramLength != 5) {
                    checkPassed = false;
                }
                break;
            case "delete":
                if (paramLength != 2) {
                    checkPassed = false;
                }
                break;
            case "query":
                if (paramLength != 3) {
                    checkPassed = false;
                }
                break;
            default:
                System.out.println("Error: incorrect command '" + command + "', please try again");
                return false;
        }
        if (!checkPassed) {
            System.out.println("Error: Wrong number of parameters, please try again");
            return false;
        } else {
            return true;
        }
    }

    /**
     * print the client menu
     */
    public void printMenu() {
        System.out.println("RMI Menu:\n" +
                "\t1. list\n" + "\t\targuments: no args\n" +
                "\t2. add\n" + "\t\targuments: <otherUsername> <start> <end> <title>\n" +
                "\t3. delete\n" + "\t\targuments: <meetingID>\n" +
                "\t4. clear\n" + "\t\targuments: no args\n" +
                "\t5. query\n" + "\t\targuments: <start> <end>\n" +
                "\t6. help\n" + "\t\targuments: no args\n" +
                "\t7. quit\n" + "\t\targuments: no args\n"
        );
    }
}
