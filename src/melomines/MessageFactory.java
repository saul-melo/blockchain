package melomines;

import java.util.Random;

public class MessageFactory {
    static Person[] senders = { Person.JOHN, Person.JACOB, Person.JINGLEHEIMER, Person.SCHMIDT };
    static String[] messageContent = {
            "k",
            "What time should we go?",
            "Sorry I'm all out",
            "Don't forget the stuff",
            "That's not right",
            "lol",
            "ok, boomer",
            "No.",
            "Can I borrow $20?",
            "If only it was that easy...",
            "brb",
            "What should I be for Halloween?",
            "Unemployed",
            "U up",
            "You won't believe the day I had...",
            "Dinner at 6?",
            "How about no",
            "Sure",
            "Congratulations!",
            "Check out this track:"
    };

    public static void generateMessage() {
        Random r = new Random();
        Blockchain.messages.add(new Message(senders[r.nextInt(senders.length)], messageContent[r.nextInt(messageContent.length)]));
    }

    public static void generateConvo() {
        while (Blockchain.messages.size() < 10) { // Cap each block at 10 messages
            MessageFactory.generateMessage();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

enum Person { JOHN, JACOB, JINGLEHEIMER, SCHMIDT }
