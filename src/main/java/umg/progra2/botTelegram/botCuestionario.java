package umg.progra2.botTelegram;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import umg.progra2.TareaCuestionario.CuesUser;
import umg.progra2.TareaCuestionario.CuesUserServie;
import umg.progra2.model.User;
import umg.progra2.service.UserService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class botCuestionario extends TelegramLongPollingBot {

    private Map<Long, String> estadoConversacion = new HashMap<>();
    private Map<Long, Integer> indicePregunta = new HashMap<>();
    private Map<Long, String> seccionActiva = new HashMap<>();
    private Map<String, String[]> preguntas = new HashMap<>();
    User usuarioConectado = null;
    UserService userService = new UserService();

    public botCuestionario() {
        // Inicializa los cuestionarios con las preguntas.
        preguntas.put("SECCION_1", new String[]{
                "1.1- Si te sientes estancado, ¿qué haces para salir de la rutina?",
                "1.2- ¿Cuál es la última serie que viste y te gustó?",
                "1.3- Si pudieras tener un superpoder, ¿cuál elegirías?",
                "1.4- ¿Cuál es tu forma favorita de pasar el tiempo cuando estás solo/a?",
                "1.5- ¿Tienes algún pasatiempo o actividad que te relaje?"
        });

        preguntas.put("SECCION_2", new String[]{
                "2.1- ¿Tienes algún hobby que te permita desconectar del mundo?",
                "2.2- ¿Cuántos años tienes?",
                "2.3- ¿Qué país te gustaría conocer?",
                "2.4- ¿Qué tipo de historias te gusta que cuenten las películas o series?",
                "2.5- ¿Tienes mascota?"
        });

        preguntas.put("SECCION_3", new String[]{
                "3.1- ¿Laboras o emprendes algún negocio?",
                "3.2- ¿Te has caído de la moto?",
                "3.3- ¿Estás de acuerdo en que aprueben la Pena de Muerte?",
                "3.4- ¿Cuál es tu lugar favorito para relajarte o pasar el tiempo libre?",
                "3.5- ¿Qué película o serie volverías a ver una y otra vez?"
        });

        preguntas.put("SECCION_4", new String[]{
                "4.1- ¿Qué te inspira a seguir adelante en los momentos difíciles?",
                "4.2- ¿Qué edad tienes?",
                "4.3- ¿Cómo te ves en cinco años?",
                "4.4- ¿Cuál es tu mayor logro hasta ahora y qué aprendiste de él?",
                "4.5- ¿Qué metas te gustaría alcanzar en el próximo año?"
        });
    }

    @Override
    public String getBotUsername() {
        return "Naye31_bot";
    }

    @Override
    public String getBotToken() {
        return "7395546105:AAHeqIOEXS07fhTYuFBaGTcw0IT6Rni6x9w";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            String mensajeTexto = update.getMessage().getText();

            try {
                String state = estadoConversacion.getOrDefault(chatId, "");
                usuarioConectado = userService.getUserByTelegramId(chatId);

                if (mensajeTexto.equals("/menu")) {
                    sendMenu(chatId);
                    return;
                }

                if (state.equals("ESPERANDO_CORREO")) {
                    processEmailInput(chatId, mensajeTexto);
                } else if (seccionActiva.containsKey(chatId)) {
                    manejaCuestionario(chatId, mensajeTexto);
                } else {
                    sendText(chatId, "Hola, envía /menu para iniciar el cuestionario.");
                }
            } catch (Exception e) {
                sendText(chatId, "Ocurrió un error al procesar tu mensaje. Por favor intenta de nuevo.");
                e.printStackTrace();
            }
        } else if (update.hasCallbackQuery()) {
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            String data = update.getCallbackQuery().getData();
            if (data.startsWith("SECCION_")) {
                inicioCuestionario(chatId, data);
            }
        }
    }

    private void processEmailInput(long chatId, String email) {
        sendText(chatId, "Recibo su Correo: " + email);
        estadoConversacion.remove(chatId);
        try {
            usuarioConectado = userService.getUserByEmail(email);
            if (usuarioConectado == null) {
                sendText(chatId, "El correo no se encuentra registrado en el sistema, por favor contacte al administrador.");
            } else {
                usuarioConectado.setTelegramid(chatId);
                userService.updateUser(usuarioConectado);
                sendText(chatId, "Usuario actualizado con éxito!");
            }
        } catch (Exception e) {
            sendText(chatId, "Error al obtener o actualizar el usuario.");
            e.printStackTrace();
        }
    }

    private void inicioCuestionario(long chatId, String section) {
        seccionActiva.put(chatId, section);
        indicePregunta.put(chatId, 0);
        enviarPregunta(chatId);
    }

    private void enviarPregunta(long chatId) {
        String seccion = seccionActiva.get(chatId);
        if (seccion == null) {
            sendText(chatId, "No hay una sección activa para el chat.");
            return;
        }

        String[] questions = preguntas.get(seccion);
        if (questions == null) {
            sendText(chatId, "No hay preguntas disponibles para la sección seleccionada.");
            seccionActiva.remove(chatId);
            indicePregunta.remove(chatId);
            sendMenu(chatId);
            return;
        }

        int index = indicePregunta.get(chatId);

        if (index < questions.length) {
            sendText(chatId, questions[index]);
        } else {
            if (seccion.equals("SECCION_4")) {
                sendText(chatId, "¡Has completado el cuestionario, Felicidades!");
                seccionActiva.remove(chatId);
                indicePregunta.remove(chatId);
            } else {
                sendText(chatId, "Sección completada. Por favor, selecciona la siguiente sección del cuestionario.");
                seccionActiva.remove(chatId);
                indicePregunta.remove(chatId);
                sendMenu(chatId);
            }
        }
    }

    private void manejaCuestionario(long chatId, String response) {
        String section = seccionActiva.get(chatId);
        int index = indicePregunta.get(chatId);

        // Validar la edad si estamos en la segunda pregunta de la sección 4
        if (section.equals("SECCION_4") && index == 1) {
            try {
                int edad = Integer.parseInt(response);
                if (edad < 0 || edad > 120) {
                    sendText(chatId, "Por favor, ingresa una edad válida.");
                    return;
                }
            } catch (NumberFormatException e) {
                sendText(chatId, "Por favor, ingresa un número válido para la edad.");
                return;
            }
        }

        CuesUser cuesUser = new CuesUser();
        cuesUser.setSeccion(section);
        cuesUser.setTelegramId(chatId);
        cuesUser.setPreguntaId(index);
        cuesUser.setRespuestaTexto(response);

        try {
            CuesUserServie respuestaService = new CuesUserServie();
            respuestaService.guardarRespuesta(cuesUser);
            sendText(chatId, "Tu respuesta fue: " + response);
        } catch (SQLException e) {
            sendText(chatId, "Hubo un error al guardar tu respuesta. Inténtalo de nuevo.");
            e.printStackTrace();
        }

        indicePregunta.put(chatId, index + 1);
        enviarPregunta(chatId);
    }

    private String formatUserInfo(String firstName, String lastName, String userName) {
        return firstName + " " + lastName + " (" + userName + ")";
    }

    private void sendText(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendMenu(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId); // Corrección para asegurar el formato adecuado
        message.setText("Selecciona una SECCIÓN:");

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Crea las filas de botones del menú
        rows.add(createButtonRow("Sección 1", "SECCION_1"));
        rows.add(createButtonRow("Sección 2", "SECCION_2"));
        rows.add(createButtonRow("Sección 3", "SECCION_3"));
        rows.add(createButtonRow("Sección 4", "SECCION_4"));

        markup.setKeyboard(rows);
        message.setReplyMarkup(markup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private List<InlineKeyboardButton> createButtonRow(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(button);
        return row;
    }
}



