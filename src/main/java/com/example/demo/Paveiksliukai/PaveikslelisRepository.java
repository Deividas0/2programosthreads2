package com.example.demo.Paveiksliukai;

import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.example.demo.Constants.getConnection;

@Repository
public class PaveikslelisRepository {

    public void updateImageUrl(String url) throws SQLException {
        String sql = "INSERT INTO paveiksleliai (url) VALUES (?)";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, url);
            preparedStatement.executeUpdate();
        }
    }
}
