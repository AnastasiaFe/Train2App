package ua.nure.fedorenko.kidstim.firebase;


public class NotificationData {
    public static final String TEXT = "TEXT";


    private String title;
    private String textMessage;


    public NotificationData() {
    }

    public NotificationData(String title, String textMessage) {

        this.title = title;
        this.textMessage = textMessage;

    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

}
