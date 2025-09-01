import java.sql.*;
import java.io.File;

public class FixInit {
    public static void main(String[] args) {
        String url = "jdbc:sqlite:data/gzhennaxia.sqlite";
        
        try {
            // Load SQLite driver
            Class.forName("org.sqlite.JDBC");
            
            try (Connection conn = DriverManager.getConnection(url)) {
                if (conn != null) {
                    System.out.println("Connected to SQLite database.");
                    
                    // Create task table (matching schema.sql)
                    String createTableSQL = "CREATE TABLE IF NOT EXISTS task (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "title VARCHAR(255) NOT NULL, " +
                        "description TEXT, " +
                        "status VARCHAR(50) DEFAULT 'PENDING', " +
                        "priority VARCHAR(50) DEFAULT 'MEDIUM', " +
                        "due_date DATETIME, " +
                        "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                        "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP" +
                        ")";
                    
                    try (Statement stmt = conn.createStatement()) {
                        stmt.execute(createTableSQL);
                        System.out.println("Task table created successfully.");
                        
                        // Insert test data
                        String[] insertSQL = {
                            "INSERT OR IGNORE INTO task (id, title, description, status, priority, due_date) VALUES (1, 'Complete Project Report', 'Write technical documentation and user manual', 'IN_PROGRESS', 'HIGH', '2025-09-01 18:00:00')",
                            "INSERT OR IGNORE INTO task (id, title, description, status, priority, due_date) VALUES (2, 'Code Review', 'Review code submitted by team members', 'PENDING', 'MEDIUM', '2025-08-31 12:00:00')",
                            "INSERT OR IGNORE INTO task (id, title, description, status, priority, due_date) VALUES (3, 'Prepare Demo', 'Prepare PPT and demo environment for client', 'COMPLETED', 'HIGH', '2025-08-30 15:00:00')",
                            "INSERT OR IGNORE INTO task (id, title, description, status, priority, due_date) VALUES (4, 'Shopping', 'Buy daily necessities', 'PENDING', 'LOW', '2025-08-30 20:00:00')",
                            "INSERT OR IGNORE INTO task (id, title, description, status, priority, due_date) VALUES (5, 'Exercise', 'Go to gym', 'PENDING', 'MEDIUM', '2025-09-01 19:00:00')",
                            "INSERT OR IGNORE INTO task (id, title, description, status, priority, due_date) VALUES (6, 'Learning', 'Learn React Native', 'PENDING', 'HIGH', '2025-09-02 22:00:00')"
                        };
                        
                        for (String sql : insertSQL) {
                            stmt.execute(sql);
                        }
                        System.out.println("Test data inserted successfully.");
                        
                        // Verify data
                        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM task");
                        if (rs.next()) {
                            System.out.println("Total tasks: " + rs.getInt(1));
                        }
                        
                        // Show all tasks
                        rs = stmt.executeQuery("SELECT id, title, status FROM task");
                        System.out.println("Tasks in database:");
                        while (rs.next()) {
                            System.out.println("ID: " + rs.getInt("id") + ", Title: " + rs.getString("title") + ", Status: " + rs.getString("status"));
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}