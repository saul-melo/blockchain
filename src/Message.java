import java.io.Serializable;

public class Message implements Serializable {
    Person sender;
    String content;

    public Message(Person sender, String content) {
        this.sender = sender;
        this.content = content;
    }

    @Override
    public String toString() {
        return sender + ": " + content;
    }
}

enum Person { JOHN, JACOB, JINGLEHEIMER, SCHMIDT }
