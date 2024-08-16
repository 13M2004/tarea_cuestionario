package umg.progra2.botTelegram;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Bot extends TelegramLongPollingBot {

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



        //Se verifica si la actualización contiene un mensaje y si ese mensaje tiene texto.
        //Luego se procesa el contenido del mensaje y se responde según el caso.
        if (update.hasMessage() && update.getMessage().hasText()) {
            System.out.println("Hola" +nickName+ "Tu nombre es:"+nombre+ "y tu apellido es:"+apellido);
            String message_text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();
            System.out.println("User id: " + chat_id + " Message: " + message_text);

            //manejo de mensajes
            if (message_text.toLowerCase().equals("hola")){
                sendText(chat_id, "hola "+nombre+ "gusto de saludarte");
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

