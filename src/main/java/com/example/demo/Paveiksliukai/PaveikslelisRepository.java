package com.example.demo.Paveiksliukai;

import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.example.demo.Constants.getConnection;

@Repository
public class PaveikslelisRepository {

    public List<Paveikslelis> findImagesToUpload() throws SQLException {
        String sql = "SELECT * FROM paveiksleliai WHERE paveikslelis IS NOT NULL";
        List<Paveikslelis> images = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Paveikslelis paveikslelis = new Paveikslelis();
                paveikslelis.setId(resultSet.getInt("id"));
                paveikslelis.setPaveikslelis(resultSet.getBytes("paveikslelis"));
                images.add(paveikslelis);
            }
        }
        return images;
    }

    public void updateImageUrl(int id, String url) throws SQLException {
        String sql = "UPDATE paveiksleliai SET url = ?, paveikslelis = NULL WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, url);
            preparedStatement.setInt(2, id);
            preparedStatement.executeUpdate();
        }
    }
}
