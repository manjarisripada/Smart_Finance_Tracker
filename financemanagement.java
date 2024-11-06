import java.sql.Date;
import java.util.*;
import java.sql.*;
import java.text.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


 class mainFrame extends JFrame implements ActionListener {
    JTextField usernameField;
    JPasswordField passwordField;
    JButton createProfileButton;
    JButton loginButton;
    JButton exitButton;
    Connection con=null;
    public String currentUsername;
    public mainFrame(){
        setTitle("Finance Tracker");
        setSize(300, 200);
        setLocationRelativeTo(null);
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        createProfileButton = new JButton("Create a new profile");
        loginButton = new JButton("Log in");
        exitButton = new JButton("Exit");
        
        setLayout(new FlowLayout());

        add(new JLabel("Username: "));
        add(usernameField);
        add(new JLabel("Password: "));
        add(passwordField);
        add(createProfileButton);
        add(loginButton);
        add(exitButton);

        createProfileButton.addActionListener(this);
        loginButton.addActionListener(this);
        exitButton.addActionListener(this);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
 
    public boolean doesTableExist(String tableName) throws SQLException {
        Statement statement = con.createStatement();
    try {
        String query = "select * from " + tableName ;
        ResultSet resultSet = statement.executeQuery(query);
        return resultSet.next();
    } catch (SQLException e) {
        return false;
        }
    } 
    public void actionPerformed(ActionEvent e) {
        
        if (e.getSource() == createProfileButton) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            connectToDatabase();
            try {
                if (!searchColumn("users1", username)) {
                    createUserProfile(username, password);
                    createTableForUser(username); 
                    System.out.println("Profile Created successfully!");
                } else {
                    System.out.println("Username already exists! Please choose a different username.");
                }
            } catch (Exception ex) {
                System.out.println("Error creating profile: " + ex.getMessage());
            }
        }
         else if (e.getSource() == loginButton) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            connectToDatabase();
            try {
                if (validateUser(username, password)) {
                    currentUsername = username; 
                    System.out.println("Logged in successfully!");
                    openUserMenu();
                    setVisible(false);
                } else {
                    System.out.println("Invalid username or password!");
                }
            } catch (Exception ex) {
                System.out.println("Error during login:"+ex.getMessage());
            }
        } else if (e.getSource() == exitButton) {
           System.exit(0);
        }
        
    }

    public void connectToDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String url = "jdbc:mysql://localhost:3306/finance";
            String user = "root";
            String pwd = "geetha@12";
            con = DriverManager.getConnection(url, user, pwd);
            boolean tableExists = doesTableExist("users1");
        if (!tableExists) {
            String createTableQuery = "CREATE TABLE users1 (username VARCHAR(20), password VARCHAR(20))";
            PreparedStatement createTableStmt = con.prepareStatement(createTableQuery);
            createTableStmt.executeUpdate();
        }
            System.out.println("Connection established");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public boolean searchColumn(String tableName, String userName) throws SQLException {
        PreparedStatement stmt = con.prepareStatement("SELECT * FROM users1 WHERE username=?");
        stmt.setString(1, userName);
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    }

    public void createUserProfile(String username, String password) throws SQLException {

        String insertQuery = "INSERT INTO users1 (username, password) VALUES (?, ?)";
        PreparedStatement insertStmt = con.prepareStatement(insertQuery);
        insertStmt.setString(1, username);
        insertStmt.setString(2, password);
        insertStmt.executeUpdate();
    }
    

    public void createTableForUser(String username) throws SQLException {
        String query1 = "CREATE TABLE " + username + "_income (Source VARCHAR(20), Amount INTEGER, Date_of_income DATE)";
        String query2 = "CREATE TABLE " + username + "_expenses (Expense_name VARCHAR(20), Amount INTEGER, Date_of_expense DATE)";
        PreparedStatement stmt1 = con.prepareStatement(query1);
        PreparedStatement stmt2 = con.prepareStatement(query2);
        stmt1.executeUpdate();
        stmt2.executeUpdate();
    }

    public boolean validateUser(String username, String password) throws SQLException {
        PreparedStatement stmt = con.prepareStatement("SELECT * FROM users1 WHERE username=? AND password=?");
        stmt.setString(1, username);
        stmt.setString(2, password);
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    }

    public void openUserMenu() {
        JFrame userMenuFrame = new JFrame();
        userMenuFrame.setLayout(new GridLayout(4, 2, 10, 10));

        userMenuFrame.setTitle("User Menu");
        userMenuFrame.setSize(400, 200);
        userMenuFrame.setLocationRelativeTo(null);

        JButton addExpenseButton = new JButton("Add Expense");
        JButton addIncomeButton = new JButton("Add Income");
        JButton viewIncomeTableButton = new JButton("View Income Table");
        JButton viewExpenseTableButton = new JButton("View Expense Table");
        JButton viewBalanceButton = new JButton("View Balance");
        JButton viewMonthlyExpensesButton = new JButton("View Monthly Expenses");
        JButton exitMenuButton = new JButton("Exit");

        userMenuFrame.setLayout(new FlowLayout());

        userMenuFrame.add(addExpenseButton);
        userMenuFrame.add(addIncomeButton);
        userMenuFrame.add(viewIncomeTableButton);
        userMenuFrame.add(viewExpenseTableButton);
        userMenuFrame.add(viewBalanceButton);
        userMenuFrame.add(viewMonthlyExpensesButton);
        userMenuFrame.add(exitMenuButton);

        
        addExpenseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addexpense obb=new addexpense(currentUsername);
               obb.setVisible(true);
            }
        });

        addIncomeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addincome ob=new addincome(currentUsername);
               ob.setVisible(true);
            }
        });

        
        viewIncomeTableButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            try {
            JScrollPane scrollPane = viewIncomeTable();
            JFrame tableFrame = new JFrame("Income Table");
            tableFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            tableFrame.add(scrollPane);
            tableFrame.pack();
            tableFrame.setVisible(true);
            } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error occurred while viewing income table."+ex.getMessage());
            }
            }
            });
            
        viewExpenseTableButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            try {
            JScrollPane scrollPane = viewExpenseTable();
            JFrame tableFrame = new JFrame("Expense Table");
            tableFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            tableFrame.add(scrollPane);
            tableFrame.pack();
            tableFrame.setVisible(true);
            } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error occurred while viewing expense table."+ex.getMessage());
            }
            }
            });
            
        
        viewBalanceButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
            int balance = viewBalance();
            viewbalance viewBalanceFrame = new viewbalance(balance);
            viewBalanceFrame.setVisible(true);
        } catch (Exception ex) {
            System.out.println("Error viewing balance: "+ex.getMessage());
        }
            }
        });

       
        viewMonthlyExpensesButton.addActionListener(new ActionListener() {
    public void actionPerformed(ActionEvent e) {
        try{
        MonthlyExpenses objj=new MonthlyExpenses(currentUsername);
        objj.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        objj.setVisible(true);
        } catch(Exception ex){
            System.out.println("Error viewing monthly expenses: " + ex.getMessage());
        }

      }
    });



        exitMenuButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        userMenuFrame.setVisible(true);
    }


 
    public JScrollPane viewIncomeTable() throws Exception {
    String query = "SELECT * FROM " + currentUsername + "_income";
    PreparedStatement stmt = con.prepareStatement(query);
    ResultSet rs = stmt.executeQuery();

    Vector<String> columnNames = new Vector<String>();
    Vector<Vector<Object>> data = new Vector<Vector<Object>>();

    ResultSetMetaData metaData = rs.getMetaData();
    int columnCount = metaData.getColumnCount();

    for (int i = 1; i <= columnCount; i++) {
        columnNames.add(metaData.getColumnLabel(i));
    }

    while (rs.next()) {
        Vector<Object> row = new Vector<Object>();
        for (int i = 1; i <= columnCount; i++) {
            row.add(rs.getObject(i));
        }
        data.add(row);
    }

    JTable table = new JTable(data, columnNames);
    JScrollPane scrollPane = new JScrollPane(table);
    return scrollPane;
    }


    public JScrollPane viewExpenseTable() throws Exception {
    String query = "SELECT * FROM " + currentUsername + "_expenses";
    PreparedStatement stmt = con.prepareStatement(query);
    ResultSet rs = stmt.executeQuery();

    ResultSetMetaData metaData = rs.getMetaData();
    int columnCount = metaData.getColumnCount();
    Vector<String> columnNames = new Vector<String>();
    Vector<Vector<Object>> data = new Vector<Vector<Object>>();

    for (int i = 1; i <= columnCount; i++) {
        columnNames.add(metaData.getColumnLabel(i));
    }

    while (rs.next()) {
        Vector<Object> row = new Vector<Object>();
        for (int i = 1; i <= columnCount; i++) {
            row.add(rs.getObject(i));
        }
        data.add(row);
    }

    JTable table = new JTable(data, columnNames);
    JScrollPane scrollPane = new JScrollPane(table);
    return scrollPane;
    }

    
    public int viewBalance() throws Exception {
        String incomeQuery = "SELECT SUM(Amount) AS TotalIncome FROM " + currentUsername + "_income";
        String expenseQuery = "SELECT SUM(Amount) AS TotalExpense FROM " + currentUsername + "_expenses";

        PreparedStatement stmt1 = con.prepareStatement(incomeQuery);
        PreparedStatement stmt2 = con.prepareStatement(expenseQuery);

        ResultSet rs1 = stmt1.executeQuery();
        ResultSet rs2 = stmt2.executeQuery();

        rs1.next();
        rs2.next();

        int totalIncome = rs1.getInt("TotalIncome");
        int totalExpense = rs2.getInt("TotalExpense");

        return totalIncome - totalExpense;
    }

    
}

class addexpense extends JFrame{
    private JTextField expenseNameField;
    private JTextField amountField;
    private JTextField dateField;
    public String currentUser;
    Connection con=null;
    public addexpense(String currentUsername) {
        currentUser=currentUsername;
        setTitle("Add Expense");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 2, 10, 10));

        JLabel expenseNameLabel = new JLabel("Enter the expense name:");
        JLabel amountLabel = new JLabel("Enter the amount spent:");
        JLabel dateLabel = new JLabel("Enter the date of expense (YYYY-MM-DD):");

        expenseNameField = new JTextField();
        amountField = new JTextField();
        dateField = new JTextField();

        add(expenseNameLabel);
        add(expenseNameField);
        add(amountLabel);
        add(amountField);
        add(dateLabel);
        add(dateField);
        
        JButton addButton = new JButton("Add");
        add(addButton);
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String expenseName = expenseNameField.getText();
                int amount = Integer.parseInt(amountField.getText());
                String dateInput = dateField.getText();
                connectToDatabase();
                try {
                    addExpense(expenseName, amount, dateInput);
                    System.out.println("Expense added successfully!");
                    dispose(); 
                } catch (Exception ex) {
                    System.out.println("Error adding expense: " + ex.getMessage());
                }
            }
        });
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
    }

     public void connectToDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String url = "jdbc:mysql://localhost:3306/finance";
            String user = "root";
            String pwd = "geetha";
            con = DriverManager.getConnection(url, user, pwd);
            System.out.println("Connection established");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    public void addExpense(String expenseName, int amount, String dateInput) throws Exception {
        String query = "INSERT INTO " + currentUser + "_expenses VALUES (?, ?, ?)";
        PreparedStatement stmt = con.prepareStatement(query);
        stmt.setString(1, expenseName);
        stmt.setInt(2, amount);
        stmt.setDate(3, java.sql.Date.valueOf(dateInput));
        stmt.executeUpdate();
    }

}


class addincome extends JFrame {
    public String current_User;
    Connection con=null;
    public addincome(String currentUsername) {
        current_User=currentUsername;
        setTitle("Add Income");
        setSize(400, 200);
        setLocationRelativeTo(null);

        JLabel sourceLabel = new JLabel("Enter the income source:");
        JLabel amountLabel = new JLabel("Enter the amount earned:");
        JLabel dateLabel = new JLabel("Enter the pay date (YYYY-MM-DD):");

        JTextField sourceField = new JTextField();
        JTextField amountField = new JTextField();
        JTextField dateField = new JTextField();

        JButton addButton = new JButton("Add Income");
        
        setLayout(new GridLayout(4, 2, 10, 10));

        add(sourceLabel);
        add(sourceField);
        add(amountLabel);
        add(amountField);
        add(dateLabel);
        add(dateField);
        add(addButton);

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String source = sourceField.getText();
                int amount = Integer.parseInt(amountField.getText());
                String dateInput = dateField.getText();
                connectToDatabase();
                try {
                    addIncome(source, amount, dateInput);
                    System.out.println("Income added successfully!");
                    dispose();
                } catch (Exception ex) {
                    System.out.println("Error adding income: " + ex.getMessage());
                }
            }
        });
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    public void connectToDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String url = "jdbc:mysql://localhost:3306/finance";
            String user = "root";
            String pwd = "geetha";
            con = DriverManager.getConnection(url, user, pwd);
            System.out.println("Connection established");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    public void addIncome(String source, int amount, String dateInput) throws Exception {
        String query = "INSERT INTO " + current_User + "_income VALUES (?, ?, ?)";
        PreparedStatement stmt = con.prepareStatement(query);
        stmt.setString(1, source);
        stmt.setInt(2, amount);
        stmt.setDate(3, java.sql.Date.valueOf(dateInput));
        stmt.executeUpdate();
    }
    
    
}


class viewbalance extends JFrame {
    public viewbalance(int balance) {
        setTitle("Balance");
        setSize(300, 100);
        setLocationRelativeTo(null);

        JLabel balanceLabel = new JLabel("Your total net balance is: " + balance);

        add(balanceLabel);
    }
}

 class MonthlyExpenses extends JFrame {
    private JTextField monthField;
    private JTextField yearField;
    public String user_name;
    Connection con=null;
    public MonthlyExpenses(String currentUsername) {
         setTitle("Monthly Expenses ");
        setSize(600, 300);
        user_name=currentUsername;
        setLayout(new FlowLayout());

        JLabel monthLabel = new JLabel("Enter the month (1-12):");
        monthField = new JTextField(10);
        JLabel yearLabel = new JLabel("Enter the year:");
        yearField = new JTextField(10);

        add(monthLabel);
        add(monthField);
        add(yearLabel);
        add(yearField);
        

        JButton viewButton = new JButton("View");
        add(viewButton);
        viewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int month = Integer.parseInt(monthField.getText());
                int year = Integer.parseInt(yearField.getText());
                connectToDatabase();
                try {
            JScrollPane scrollPane = viewMonthlyExpenses(month,year);
            JFrame tableFrame = new JFrame("Monthly_Expense Table");
            tableFrame.add(scrollPane);
            tableFrame.pack();
            tableFrame.setVisible(true);
            
            } catch (Exception ex) {
                System.out.println("Error occurred while viewing expense table."+ ex.getMessage());
            }
            dispose();
            }
        });

    }
    public void connectToDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String url = "jdbc:mysql://localhost:3306/finance";
            String user = "root";
            String pwd = "geetha";
            con = DriverManager.getConnection(url, user, pwd);
            System.out.println("Connection established");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    public JScrollPane viewMonthlyExpenses(int month, int year) throws Exception {
    String query = "SELECT * FROM " + user_name + "_expenses WHERE MONTH(Date_of_expense) = ? AND YEAR(Date_of_expense) = ?";
    PreparedStatement stmt = con.prepareStatement(query);
    stmt.setInt(1, month);
    stmt.setInt(2, year);
    ResultSet rs = stmt.executeQuery();

    ResultSetMetaData metaData = rs.getMetaData();
    int columnCount = metaData.getColumnCount();
    String[] columnNames = new String[columnCount];
    for (int i = 0; i < columnCount; i++) {
        columnNames[i] = metaData.getColumnLabel(i + 1);
    }

    ArrayList<Object[]> rows = new ArrayList<>();
    while (rs.next()) {
        Object[] rowData = new Object[columnCount];
        for (int i = 0; i < columnCount; i++) {
            rowData[i] = rs.getObject(i + 1);
        }
        rows.add(rowData);
    }

    Object[][] data = new Object[rows.size()][columnCount];
    rows.toArray(data);

    JTable table = new JTable(data, columnNames);

    JScrollPane scrollPane = new JScrollPane(table);
    return scrollPane;
    }
}


public class financemanagement {
    public static void main(String[] args) {
    mainFrame obj= new mainFrame();
        
    }
}