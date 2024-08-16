import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.Scanner;

class VotingSystem {
    static Scanner scanner = new Scanner(System.in);
    static boolean approval = false;
    static String ANSI_RESET = "\u001B[0m";
    static String ANSI_RED = "\u001B[31m";
    static String ANSI_GREEN = "\u001B[32m";
    static String ANSI_YELLOW = "\u001B[33m";
    static String ANSI_BLUE = "\u001B[34m";
    static String ANSI_PURPLE = "\u001B[35m";
    static String ANSI_CYAN = "\u001B[36m";
    static String ANSI_BLINK = "\u001B[5m";
    static LinkedList link = new LinkedList();
    static CustomLinkedList adminList = new CustomLinkedList();
    static Users1 loggedInUser;

    public static void main(String[] args) throws Exception {
        System.out.println(ANSI_RED + ANSI_BLINK + "\t\t\tWelcome to the Online Voting System" + ANSI_BLINK + ANSI_RESET);
        while (true) {
            printMenu();
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    registerUser();
                    break;
                case 2:
                    loginUser();
                    break;
                case 3:
                    adminPanel();
                    break;
                case 4:
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    static void printMenu() {
        System.out.println(ANSI_GREEN + "\t\t\t1. Register" + ANSI_RESET);
        System.out.println(ANSI_CYAN + "\t\t\t2. Login" + ANSI_RESET);
        System.out.println(ANSI_PURPLE + "\t\t\t3. Admin Panel" + ANSI_RESET);
        System.out.println(ANSI_YELLOW + "\t\t\t4. Exit" + ANSI_RESET);
    }

    static void registerUser() throws SQLException, Exception {
        System.out.println("Enter Your Name : ");
        String name = scanner.nextLine();
        int age = 0;
        String user_email;
        String user_password;
        while (true) {
            System.out.println("Enter Your Age : ");
            age = scanner.nextInt();
            scanner.nextLine();
            if (age <= 18) {
                System.out.println("You Not Eligible For Voting Age is greater than 18 !");
            } else {
                while (true) {
                    System.out.println("Enter Your email ID (Must Be End With @gmail.com): ");
                    user_email = scanner.nextLine();
                    if (user_email.endsWith("@gmail.com")) {
                        while (true) {
                            System.out.println("Enter Your PassWord (Must Be less then 8):");
                            user_password = scanner.nextLine();
                            if (user_password.length() <= 8) {
                                break;
                            } else {
                                System.out.println("Enter PassWord less Then 8 !");
                            }
                        }
                        break;
                    } else {
                        System.out.println("Must BE Enter email End With @gmail.com");
                    }
                }
                break;
            }
        }
        LocalTime registrationTime = LocalTime.now();
    Users1 users = new Users1(age, name, user_email, user_password, registrationTime);
    if (users.registers()) {
        link.add(users);
        System.out.println(ANSI_YELLOW + "Registration Successful at " + registrationTime + ". Waiting for admin approval." + ANSI_RESET);
    } else {
        System.out.println(ANSI_RED + "Registration Failed!" + ANSI_RESET);
    }
    }

    static void loginUser() throws Exception {
        String email, password;
        while (true) {
            System.out.println("Enter Your Email_ID (must be EndWith @gmail.com): ");
            email = scanner.nextLine();
            if (email.endsWith("@gmail.com")) {
                break;
            } else {
                System.out.println("\tPlease Enter Valid Email !\n");
            }
        }
        while (true) {
            System.out.println("Enter PassWord : ");
            password = scanner.nextLine();
            if (password.length() <= 8) {
                break;
            } else {
                System.out.println("\tMust be Enter less Than 8 !\n");
            }
        }
        
        Users1 user = new Users1(email, password);
        if (user.authenticate()) {
            // String otp = OTPService.generateOTP(6);
            // OTPService.sendOTP(email, otp);
            System.out.println("Login SucessFul ! ");
            // System.out.println("Enter the OTP sent to your email: ");
            // String enteredOTP = scanner.nextLine();
            
            // if (otp.equals(enteredOTP)) {
            //     LocalTime loginTime = LocalTime.now();
            //     System.out.println("Login Successful at " + loginTime + "!\n");
            //     loggedInUser = user;
            //     startVoting();
            // } else {
            //     System.out.println("Invalid OTP. Login failed.");
            // }
        } else {
            System.out.println("\tPlease Enter Valid data before Registration ! \n");
            System.out.println("For Registration Click 1 ");
            System.out.println("For Re-Login Click 2 ");
            System.out.println("For Back Menu 3  ");
            System.out.println("Enter Your Choice : ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            if (choice == 1) {
                registerUser();
            } else if (choice == 2) {
                loginUser();
            } else {
                return;
            }
        }
    }
    

    static void startVoting() throws Exception {
        if (loggedInUser.hasVoted()) {
            System.out.println("You have already voted.");
            return;
        }

        System.out.println("Available Elections:");
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM elections";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.println("Election ID: " + rs.getInt("election_id") + ", Name: " + rs.getString("election_name"));
            }
        }

        System.out.println("Enter Election ID to vote in: ");
        int electionId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        System.out.println("Candidates:");
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM candidates WHERE election_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, electionId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.println("Candidate ID: " + rs.getInt("candidate_id") + ", Name: " + rs.getString("candidate_name"));
            }
        }

        System.out.println("Enter Candidate ID to vote for: ");
        int candidateId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO votes (election_id, user_email, candidate_id) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, electionId);
            stmt.setString(2, loggedInUser.getUser_email());
            stmt.setInt(3, candidateId);
            stmt.executeUpdate();

            String updateQuery = "UPDATE users SET has_voted = TRUE WHERE user_email = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
            updateStmt.setString(1, loggedInUser.getUser_email());
            updateStmt.executeUpdate();

            System.out.println("Vote cast successfully!");

            logOutUser();
        }
    }

    static void logOutUser() throws Exception {
        LocalTime logoutTime = LocalTime.now();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "UPDATE login SET logout_time = ? WHERE user_email = ? AND logout_time IS NULL";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setTime(1, java.sql.Time.valueOf(logoutTime));
            stmt.setString(2, loggedInUser.getUser_email());
            stmt.executeUpdate();
        }
        System.out.println("Logout successful at " + logoutTime + "!");
        loggedInUser = null;
    }

    static void adminPanel() throws Exception {
        while (true) {
            System.out.println("\tADMIN PANEL");
            System.out.println("1. Add Election");
            System.out.println("2. Add Candidate");
            System.out.println("3. Pending Approval");
            System.out.println("4. View Results");
            System.out.println("5. Back to Menu \n");
            System.out.println("Enter Your Choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            switch (choice) {
                case 1:
                    addElection();
                    break;
                case 2:
                    addCandidate();
                    break;
                case 3:
                    pendingApproval();
                    break;
                case 4:
                    viewResults();
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    static void addElection() throws Exception {
        System.out.println("Enter Election Name: ");
        String electionName = scanner.nextLine();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO elections (election_name) VALUES (?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, electionName);
            stmt.executeUpdate();
            System.out.println("Election added successfully!");
            adminList.add(new AdminData("Election", electionName));
        }
    }

    static void addCandidate() throws Exception {
        System.out.println("Enter Election ID: ");
        int electionId = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        System.out.println("Enter Candidate Name: ");
        String candidateName = scanner.nextLine();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO candidates (election_id, candidate_name) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, electionId);
            stmt.setString(2, candidateName);
            stmt.executeUpdate();
            System.out.println("Candidate added successfully!");
            adminList.add(new AdminData("Candidate", candidateName));
        }
    }

    static void pendingApproval() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM users WHERE is_approved = FALSE";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            System.out.println(ANSI_PURPLE + ANSI_BLINK + "\t\tPending Approvals" + ANSI_RESET);
            while (rs.next()) {
                System.out.println("User: " + rs.getString("name") + " | Email: " + rs.getString("user_email"));
                System.out.println("1. Approve");
                System.out.println("2. Skip");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
    
                if (choice == 1) {
                    String updateQuery = "UPDATE users SET is_approved = TRUE WHERE user_email = ?";
                    PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                    updateStmt.setString(1, rs.getString("user_email"));
                    updateStmt.executeUpdate();
                    System.out.println(ANSI_GREEN + "User approved!" + ANSI_RESET);
                }
            }
        }
    }
    

    static void viewResults() throws Exception {
        System.out.println("Available Elections:");
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM elections";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int electionId = rs.getInt("election_id");
                String electionName = rs.getString("election_name");
                System.out.println("Election ID: " + electionId + ", Name: " + electionName);
                System.out.println("Results:");
                String resultQuery = "SELECT candidate_name, COUNT(*) as vote_count FROM votes v " +
                        "JOIN candidates c ON v.candidate_id = c.candidate_id WHERE election_id = ? GROUP BY candidate_name";
                PreparedStatement resultStmt = conn.prepareStatement(resultQuery);
                resultStmt.setInt(1, electionId);
                ResultSet resultRs = resultStmt.executeQuery();
                while (resultRs.next()) {
                    System.out.println("Candidate: " + resultRs.getString("candidate_name") + ", Votes: " + resultRs.getInt("vote_count"));
                }
            }
        }
    }

}

class Users1 {
    String name;
    int age;
    String user_email;
    String user_password;
    LocalTime registrationTime;

    public Users1(int age, String name, String user_email, String user_password, LocalTime registrationTime) {
        this.age = age;
        this.name = name;
        this.user_email = user_email;
        this.user_password = user_password;
        this.registrationTime = registrationTime;
    }

    public Users1(String user_email, String user_password) {
        this.user_email = user_email;
        this.user_password = user_password;
    }

    public String getUser_email() {
        return user_email;
    }

    boolean registers() throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO users (user_email, user_password, name, age, registration_time) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, user_email);
            stmt.setString(2, user_password);
            stmt.setString(3, name);
            stmt.setInt(4, age);
            stmt.setTime(5, java.sql.Time.valueOf(registrationTime));
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean authenticate() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM users WHERE user_email = ? AND user_password = ? AND is_approved = TRUE";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, user_email);
            stmt.setString(2, user_password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return true;
            } else {
                System.out.println("Approval Pending or Invalid Credentials!");
                return false;
            }
        }
    }
    
    public boolean hasVoted() throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT has_voted FROM users WHERE user_email = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, user_email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("has_voted");
            } else {
                return false;
            }
        }
    }
}

class LinkedList {
    private Node head;

    private class Node {
        Users1 user;
        Node next;

        Node(Users1 user) {
            this.user = user;
        }
    }

    public void add(Users1 user) {
        Node newNode = new Node(user);
        if (head == null) {
            head = newNode;
        } else {
            Node current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
    }

    public void remove(Users1 user) {
        if (head == null) return;

        if (head.user.equals(user)) {
            head = head.next;
            return;
        }

        Node current = head;
        while (current.next != null && !current.next.user.equals(user)) {
            current = current.next;
        }

        if (current.next != null) {
            current.next = current.next.next;
        }
    }

    public boolean contains(Users1 user) {
        Node current = head;
        while (current != null) {
            if (current.user.equals(user)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    public void printList() {
        Node current = head;
        while (current != null) {
            System.out.println(current.user);
            current = current.next;
        }
    }
}

class CustomLinkedList {
    private AdminNode head;

    private class AdminNode {
        AdminData adminData;
        AdminNode next;

        AdminNode(AdminData adminData) {
            this.adminData = adminData;
        }
    }

    public void add(AdminData adminData) {
        AdminNode newNode = new AdminNode(adminData);
        if (head == null) {
            head = newNode;
        } else {
            AdminNode current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
    }

    public void remove(AdminData adminData) {
        if (head == null) return;

        if (head.adminData.equals(adminData)) {
            head = head.next;
            return;
        }

        AdminNode current = head;
        while (current.next != null && !current.next.adminData.equals(adminData)) {
            current = current.next;
        }

        if (current.next != null) {
            current.next = current.next.next;
        }
    }

    public boolean contains(AdminData adminData) {
        AdminNode current = head;
        while (current != null) {
            if (current.adminData.equals(adminData)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    public void printList() {
        AdminNode current = head;
        while (current != null) {
            System.out.println(current.adminData);
            current = current.next;
        }
    }
}

class AdminData {
    String type;
    String name;

    public AdminData(String type, String name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public String toString() {
        return "AdminData{" +
                "type='" + type + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
class DatabaseConnection {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/voting_system";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}
