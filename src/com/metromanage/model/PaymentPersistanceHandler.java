package com.metromanage.model;

import java.sql.*;

import com.metromanage.domain.CardPayment;
import com.metromanage.domain.Payment;

public class PaymentPersistanceHandler extends PersistanceHandler {

    public PaymentPersistanceHandler() {
        this.dbConnection = DB.getConnection();
    }

    @Override
    public int save(Object obj) {
        return 0;
    }

    public int savePayment(Object obj, String paymentType) {
        Payment payment = (Payment) obj;
        CardPayment card = null;
        if(paymentType.equals("Card")) {
            card = (CardPayment) obj;
        }

        String saveQuery = "Insert Into Payment(PassengerID, Amount, PaymentDate, CardNumber, CardHolderName, CardExpiryDate, PaymentType) Values(?,?,?,?,?,?,?)";
        try (PreparedStatement pstmt = dbConnection.prepareStatement(saveQuery, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, payment.getPassengerID());
            pstmt.setFloat(2, payment.getAmount());
            pstmt.setTimestamp(3, Timestamp.valueOf(payment.getPaymentDate().replace("T", " ")));
            if(paymentType.equals("Card")) {
                pstmt.setString(4, card.getCardNumber());
                pstmt.setString(5, card.getCardHolderName());
                pstmt.setString(6, card.getExpiryDate());
            } else {
                pstmt.setNull(4, Types.VARCHAR);
                pstmt.setNull(5, Types.VARCHAR);
                pstmt.setNull(6, Types.VARCHAR);
            }
            pstmt.setString(7, paymentType);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating Payment failed, no rows affected.");
            }
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating payment failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void delete(Object obj) {
    }

    @Override
    public Object find(int id) {
        return null;
    }

}
