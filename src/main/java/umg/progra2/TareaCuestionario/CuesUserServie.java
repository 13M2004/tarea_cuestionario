package umg.progra2.TareaCuestionario;

import java.sql.SQLException;
import java.util.List;

public class CuesUserServie {

    private CuesUserDao cuesUserDao = new CuesUserDao(); // Corrige el nombre de la variable

    public void guardarRespuesta(CuesUser cuesUser) throws SQLException {
        cuesUserDao.insertarRespuesta(cuesUser); // Usa el nombre de variable corregido
    }

    public List<CuesUser> obtenerRespuestasPorTelegramId(long telegramId) throws SQLException {
        return cuesUserDao.obtenerRespuestasPorTelegramId(telegramId); // Usa el nombre de variable corregido
    }

    public List<CuesUser> obtenerTodasRespuestas() throws SQLException {
        return cuesUserDao.obtenerTodasRespuestas(); // Usa el nombre de variable corregido
    }
}
