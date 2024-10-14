package com.example.demo.Laiskai;

import com.example.demo.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import static com.example.demo.Constants.*;

@Repository
public class LaiskasRepository {

    public List<Laiskas> neissiustiLaiskai() throws SQLException {
        String sql = "SELECT * FROM laiskai WHERE issiusta IS NULL";
        List<Laiskas> emails = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Laiskas laiskas = new Laiskas();
                laiskas.setId(resultSet.getInt("id"));
                laiskas.setGavejas(resultSet.getString("gavejas"));
                laiskas.setTurinys(resultSet.getString("turinys"));
                emails.add(laiskas);
            }
        }
        return emails;
    }

    public void updateIssiusta(String gavejas, String turinys) throws SQLException {
        String sql = "INSERT INTO laiskai (gavejas, turinys, issiusta) VALUES (?, ?, CURRENT_TIMESTAMP)";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, gavejas);
            preparedStatement.setString(2, turinys);
            preparedStatement.executeUpdate();
        }
    }

}
