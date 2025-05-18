// Card.java
import java.io.File;
import java.io.Serializable;
import javax.swing.ImageIcon;
import java.util.ArrayList;
import java.util.List;

public class Card implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum AnswerType {
        TEXT,
        IMAGE
    }

    private String question;
    private String textAnswer;
    private File imageAnswerFile;
    private transient ImageIcon imageAnswer;
    private AnswerType answerType;
    private String topic;

    // Constructor for text answer
    public Card(String question, String textAnswer) {
        this.question = question;
        this.textAnswer = textAnswer;
        this.answerType = AnswerType.TEXT;
        this.topic = "General";
    }

    // Constructor for text answer with topic
    public Card(String question, String textAnswer, String topic) {
        this.question = question;
        this.textAnswer = textAnswer;
        this.answerType = AnswerType.TEXT;
        this.topic = topic;
    }

    // Constructor for image answer
    public Card(String question, File imageFile) {
        this.question = question;
        this.imageAnswerFile = imageFile;
        this.loadImage();
        this.answerType = AnswerType.IMAGE;
        this.topic = "General"; // Default topic
    }

    // Constructor for image answer with topic
    public Card(String question, File imageFile, String topic) {
        this.question = question;
        this.imageAnswerFile = imageFile;
        this.loadImage();
        this.answerType = AnswerType.IMAGE;
        this.topic = topic;
    }

    private void loadImage() {
        if (imageAnswerFile != null && imageAnswerFile.exists()) {
            imageAnswer = new ImageIcon(imageAnswerFile.getAbsolutePath());
        }
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();

        // Initialize topic if it's null (for backward compatibility with old saved cards)
        if (topic == null) {
            topic = "General";
        }

        // Also reload the image if it's an image card
        if (answerType == AnswerType.IMAGE && imageAnswerFile != null) {
            loadImage();
        }
    }

    public String getQuestion() {
        return question;
    }

    public AnswerType getAnswerType() {
        return answerType;
    }

    public String getTextAnswer() {
        return textAnswer;
    }

    public ImageIcon getImageAnswer() {
        return imageAnswer;
    }

    public File getImageAnswerFile() {
        return imageAnswerFile;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setTextAnswer(String textAnswer) {
        this.textAnswer = textAnswer;
        this.answerType = AnswerType.TEXT;
    }

    public void setImageAnswer(File imageFile) {
        this.imageAnswerFile = imageFile;
        this.loadImage();
        this.answerType = AnswerType.IMAGE;
    }

    // Add getter and setter for topic
    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}