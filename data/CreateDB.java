import java.sql.*;

public class CreateDB {
    public static void main(String[] args) {
        String url = "jdbc:sqlite:data/gzhennaxia.sqlite";
        
        try {
            try (Connection conn = DriverManager.getConnection(url)) {
                if (conn != null) {
                    System.out.println("Connected to SQLite database.");
                    
                    // Create tasks table with correct structure
                    String createTableSQL = "CREATE TABLE IF NOT EXISTS tasks (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "title VARCHAR(255) NOT NULL, " +
                        "description TEXT, " +
                        "start_time DATETIME, " +
                        "end_time DATETIME, " +
                        "priority INTEGER DEFAULT 1, " +
                        "status VARCHAR(50) DEFAULT 'pending', " +
                        "category VARCHAR(100), " +
                        "tags VARCHAR(500), " +
                        "reminder_time DATETIME, " +
                        "is_all_day BOOLEAN DEFAULT FALSE, " +
                        "repeat_type VARCHAR(50), " +
                        "repeat_end_date DATETIME, " +
                        "created_time DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                        "updated_time DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                        "deleted BOOLEAN DEFAULT FALSE" +
                        ")";
                    
                    try (Statement stmt = conn.createStatement()) {
                        stmt.execute(createTableSQL);
                        System.out.println("Tasks table created successfully.");
                        
                        // Insert test data
                        String[] insertSQL = {
                            "INSERT INTO tasks (title, description, end_time, priority, status, category) VALUES ('Project Report', 'Complete quarterly project report', '2025-09-01 18:00:00', 1, 'pending', 'Work')",
                            "INSERT INTO tasks (title, description, end_time, priority, status, category) VALUES ('Shopping', 'Buy daily necessities and food', '2025-08-30 20:00:00', 2, 'pending', 'Life')",
                            "INSERT INTO tasks (title, description, end_time, priority, status, category) VALUES ('Exercise', 'Go to gym for strength training', '2025-09-01 19:00:00', 2, 'pending', 'Health')",
                            "INSERT INTO tasks (title, description, end_time, priority, status, category) VALUES ('Learning', 'Learn React Native development', '2025-09-02 22:00:00', 1, 'pending', 'Study')",
                            "INSERT INTO tasks (title, description, end_time, priority, status, category) VALUES ('Clean Room', 'Clean and organize bedroom', '2025-08-29 16:00:00', 3, 'completed', 'Life')",
                            "INSERT INTO tasks (title, description, end_time, priority, status, category) VALUES ('Team Meeting', 'Attend project progress meeting', '2025-08-28 14:00:00', 1, 'completed', 'Work')"
                        };
                        
                        for (String sql : insertSQL) {
                            stmt.execute(sql);
                        }
                        System.out.println("Test data inserted successfully.");
                        
                        // Verify data
                        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM tasks");
                        if (rs.next()) {
                            System.out.println("Total tasks: " + rs.getInt(1));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}