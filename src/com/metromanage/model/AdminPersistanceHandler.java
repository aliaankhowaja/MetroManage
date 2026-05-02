package com.metromanage.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import com.metromanage.domain.Admin;

public class AdminPersistanceHandler extends PersistanceHandler {
    public AdminPersistanceHandler() {
        this.dbConnection = DB.getConnection();
    }

    @Override
    public int save(Object obj) {
        Admin admin = (Admin) obj;
        if (admin.getAdminID() != 0) {
            String update = "Update Admin Set name = ?, email = ?, passwordHash = ?, status = ?, registrationDate = ? Where adminID = ?";
            try (PreparedStatement pstmt = dbConnection.prepareStatement(update)) {
                pstmt.setString(1, admin.getName());
                pstmt.setString(2, admin.getEmail());
                pstmt.setString(3, admin.getPasswordHash());
                pstmt.setString(4, admin.getStatus());
                pstmt.setTimestamp(5, Timestamp.valueOf(admin.getRegistrationDate()));
                pstmt.setInt(6, admin.getAdminID());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return admin.getAdminID();
        }

        String insert = "Insert Into Admin(name, email, passwordHash, status, registrationDate) Values(?,?,?,?,?)";
        try (PreparedStatement pstmt = dbConnection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, admin.getName());
            pstmt.setString(2, admin.getEmail());
            pstmt.setString(3, admin.getPasswordHash());
            pstmt.setString(4, admin.getStatus());
            pstmt.setTimestamp(5, Timestamp.valueOf(admin.getRegistrationDate()));
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return (int) generatedKeys.getLong(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public void delete(Object obj) {
        Admin admin = (Admin) obj;
        admin.setStatus("deleted");
        save(admin);
    }

    @Override
    public Object find(int id) {
        Object admin = findByIdentifier("adminID", id);
        return admin;
    }

    public Object findByEmail(String email) {
        return findByIdentifier("email", email);
    }

    private Object findByIdentifier(String key, Object value) {
        String query = "Select * From Admin Where " + key + " = ?";
        try (PreparedStatement pstmt = dbConnection.prepareStatement(query)) {
            if (value instanceof Integer) {
                pstmt.setInt(1, (Integer) value);
            } else {
                pstmt.setString(1, String.valueOf(value));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapAdmin(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Admin mapAdmin(ResultSet rs) throws SQLException {
        Admin admin = new Admin();
        admin.setAdminID(rs.getInt("adminID"));
        admin.setName(rs.getString("name"));
        admin.setEmail(rs.getString("email"));

        if (hasColumn(rs, "passwordHash")) {
            admin.setPasswordHash(rs.getString("passwordHash"));
        } else if (hasColumn(rs, "password")) {
            admin.setPasswordHash(rs.getString("password"));
        }

        if (hasColumn(rs, "status")) {
            admin.setStatus(rs.getString("status"));
        } else {
            admin.setStatus("Active");
        }

        if (hasColumn(rs, "registrationDate")) {
            Timestamp registrationTimestamp = rs.getTimestamp("registrationDate");
            if (registrationTimestamp != null) {
                admin.setRegistrationDate(registrationTimestamp.toLocalDateTime());
            } else {
                admin.setRegistrationDate(LocalDateTime.now());
            }
        } else {
            admin.setRegistrationDate(LocalDateTime.now());
        }

        return admin;
    }

    private boolean hasColumn(ResultSet rs, String columnName) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            if (columnName.equalsIgnoreCase(metaData.getColumnLabel(i))
                    || columnName.equalsIgnoreCase(metaData.getColumnName(i))) {
                return true;
            }
        }
        return false;
    }
}
