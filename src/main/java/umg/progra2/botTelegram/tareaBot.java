package umg.progra2.botTelegram;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class tareaBot extends TelegramLongPollingBot {
    //Cambio de Euros a Quetzales
    private static final double cambioEuroaQuetzal = 8.89;

    // Lista de chat IDs de compañeros de grupo
    private static final List<Long> chatIDSgrupal = List.of(
            //ChatId Edgar
            5792621349L,

            //ChatId Nayeli
            6699823249L,

            //ChatId Karen
            6984229154L,

            //ChatId Alejandro
            5454689659L
    );

    @Override
    public String getBotUsername() {
        return "Naye31_bot";
    }

    @Override
    public String getBotToken() {
        return "7395546105:AAHeqIOEXS07fhTYuFBaGTcw0IT6Rni6x9w";
    }



    //El método onUpdateReceived(Update update) de la clase Bot se usa para manejar todas las actualizaciones que el
    // bot recibe.
    // Dependiendo del tipo de actualización, se toman diferentes acciones.

    @Override
    public void onUpdateReceived(Update update) {

        //Obtener información de la persona que manda mensajes
        String nombre = update.getMessage().getFrom().getFirstName();
        String apellido = update.getMessage().getFrom().getLastName();
        String nickName = update.getMessage().getFrom().getUserName();

        // Obtener la fecha y hora actual
        LocalDateTime fechaHoraActual = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE d 'de' MMMM, HH:mm");
        String fechahoraSistema = fechaHoraActual.format(formatter);

        //Se verifica si la actualización contiene un mensaje y si ese mensaje tiene texto.
        //Luego se procesa el contenido del mensaje y se responde según el caso.
        if (update.hasMessage() && update.getMessage().hasText()) {
            System.out.println("Hola" +nickName+ "Tu nombre es:"+nombre+ "y tu apellido es:"+apellido);
            String message_text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();
            System.out.println("User id: " + chat_id + " Message: " + message_text);

            /*
            //manejo de mensajes
            if (message_text.toLowerCase().equals("hola")){
                sendText(chat_id, "hola "+nombre+ "gusto de saludarte");
            }  */


            //Información Personal ¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬
            if (message_text.toLowerCase().equals("/info")){
                sendText(chat_id, "CARNET: 0905-23-3945 \n NOMBRE: Manuel Monzón \n SEMESTRE: Cuarto Cilco");
            }


            //Comentario Respecto aL Curso ¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬
            if (message_text.toLowerCase().equals("/progra")){
                sendText(chat_id, "Me parece muy interesante, bastante dinamico y donde hay mucho por aprender. \n (El mundo de la tecnología esta a tus pies)");
            }


            // Saludo y hora del sistema ¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬
            if (message_text.toLowerCase().equals("/hola")) {
                sendText(chat_id, "Hola " + nombre + ",  La fecha y hora actual es: " + fechahoraSistema);
            }


            // Calculo de cambio de Euros a Quetzales ¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬
            if (message_text.toLowerCase().startsWith("/cambio")) {
                try {
                    // Extraer el valor numérico del mensaje
                    String[] parts = message_text.split(" ");
                    if (parts.length == 2) {
                        double euros = Double.parseDouble(parts[1]);
                        double quetzales = euros * cambioEuroaQuetzal;


                        String respuesta = String.format("Son %.2f quetzales.", quetzales);

                        // Enviar la respuesta
                        sendText(chat_id, respuesta);
                    } else {
                        sendText(chat_id, "Por favor, proporciona un monto en Euros después del comando. Ejemplo: /cambio 100");
                    }
                } catch (NumberFormatException e) {
                    sendText(chat_id, "El monto proporcionado no es válido. Asegúrate de ingresar un número.");
                }
            }


            // Saludo Grupal ¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬
            if (message_text.toLowerCase().startsWith("/grupal")) {
                String[] parts = message_text.split(" ", 2);
                if (parts.length == 2) {
                    String mensaje = parts[1];
                    for (Long id : chatIDSgrupal) {
                        sendText(id, mensaje);
                    }
                    sendText(chat_id, "El mensaje ha sido enviado a todos los compañeros.");
                } else {
                    sendText(chat_id, "Por favor, proporciona un mensaje después del comando. Ejemplo: /grupal Hola a todos.");
                }

            } else {
                sendText(chat_id, "Comando no reconocido. \nUtiliza: \n/info para obtener información, \n/cambio [monto] para calcular el cambio, \n/progra para saber sobre el curso, \n/grupal [mensaje] para enviar un mensaje a tus compañeros.");
            }

        }
    }


    public void sendText(Long who, String what){
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString()) //Who are we sending a message to
                .text(what).build();    //Message content
        try {
            execute(sm);                        //Actually sending the message
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);      //Any error will be printed here
        }
    }
}
