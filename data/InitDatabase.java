import java.sql.*;

public class InitDatabase {
    public static void main(String[] args) {
        String url = "jdbc:sqlite:data/gzhennaxia.sqlite";
        
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                System.out.println("Connected to SQLite database.");
                
                // Create table
                String createTableSQL = """
                    CREATE TABLE IF NOT EXISTS tasks (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        title VARCHAR(255) NOT NULL,
                        description TEXT,
                        start_time DATETIME,
                        end_time DATETIME,
                        priority INTEGER DEFAULT 1,
                        status VARCHAR(50) DEFAULT 'pending',
                        category VARCHAR(100),
                        tags VARCHAR(500),
                        reminder_time DATETIME,
                        is_all_day BOOLEAN DEFAULT FALSE,
                        repeat_type VARCHAR(50),
                        repeat_end_date DATETIME,
                        created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                        updated_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                        deleted BOOLEAN DEFAULT FALSE
                    )
                    """;
                
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(createTableSQL);
                    System.out.println("Table created successfully.");
                    
                    // Insert test data
                    String[] insertSQL = {
                        "INSERT INTO tasks (title, description, end_time, priority, status, category) VALUES ('完成项目报告', '需要在今天完成季度项目报告', '2025-09-01 18:00:00', 1, 'pending', '工作')",
                        "INSERT INTO tasks (title, description, end_time, priority, status, category) VALUES ('购买生活用品', '去超市购买日用品和食材', '2025-08-30 20:00:00', 2, 'pending', '生活')",
                        "INSERT INTO tasks (title, description, end_time, priority, status, category) VALUES ('健身锻炼', '去健身房进行力量训练', '2025-09-01 19:00:00', 2, 'pending', '健康')",
                        "INSERT INTO tasks (title, description, end_time, priority, status, category) VALUES ('学习新技术', '学习React Native开发', '2025-09-02 22:00:00', 1, 'pending', '学习')",
                        "INSERT INTO tasks (title, description, end_time, priority, status, category) VALUES ('整理房间', '清理和整理卧室', '2025-08-29 16:00:00', 3, 'completed', '生活')",
                        "INSERT INTO tasks (title, description, end_time, priority, status, category) VALUES ('团队会议', '参加项目进度讨论会议', '2025-08-28 14:00:00', 1, 'completed', '工作')"
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
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}