package umg.progra2.TareaCuestionario;

import umg.progra2.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class CuesUserDao {

    public void insertarRespuesta(CuesUser cuesUser) throws SQLException {
        String query = "INSERT INTO tb_respuestas (seccion, telegram_id, pregunta_id, respuesta_texto, fecha_respuesta) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setString(1, cuesUser.getSeccion());
            statement.setLong(2, cuesUser.getTelegramId());
            statement.setInt(3, cuesUser.getPreguntaId());
            statement.setString(4, cuesUser.getRespuestaTexto());
            statement.setTimestamp(5, cuesUser.getFechaRespuesta()); // Asegúrate de que la fecha esté bien asignada
            statement.executeUpdate();
        }
    }

    public List<CuesUser> obtenerRespuestasPorTelegramId(long telegramId) throws SQLException {
        String query = "SELECT * FROM tb_respuestas WHERE telegram_id = ?";
        List<CuesUser> cuesUsers = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query))
        {
            statement.setLong(1, telegramId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    CuesUser cuesUser = new CuesUser();
                    cuesUser.setId(resultSet.getInt("id"));
                    cuesUser.setSeccion(resultSet.getString("seccion"));
                    cuesUser.setTelegramId(resultSet.getLong("telegram_id"));
                    cuesUser.setPreguntaId(resultSet.getInt("pregunta_id"));
                    cuesUser.setRespuestaTexto(resultSet.getString("respuesta_texto"));
                    cuesUser.setFechaRespuesta(resultSet.getTimestamp("fecha_respuesta"));
                    cuesUsers.add(cuesUser);
                }
            }
        }
        return cuesUsers;
    }

    public List<CuesUser> obtenerTodasRespuestas() throws SQLException {
        String query = "SELECT * FROM tb_respuestas";
        List<CuesUser> cuesUsers = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query))
        {
            while (resultSet.next()) {
                CuesUser cuesUser = new CuesUser();
                cuesUser.setId(resultSet.getInt("id"));
                cuesUser.setSeccion(resultSet.getString("seccion"));
                cuesUser.setTelegramId(resultSet.getLong("telegram_id"));
                cuesUser.setPreguntaId(resultSet.getInt("pregunta_id"));
                cuesUser.setRespuestaTexto(resultSet.getString("respuesta_texto"));
                cuesUser.setFechaRespuesta(resultSet.getTimestamp("fecha_respuesta"));
                cuesUsers.add(cuesUser);
            }
        }
        return cuesUsers;
    }
}
