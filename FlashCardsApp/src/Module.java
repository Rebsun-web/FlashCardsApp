// Module.java
import java.io.Serializable;
import java.util.*;

public class Module implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private List<Card> cards;

    public List<String> getAllTopics() {
        Set<String> uniqueTopics = new HashSet<>();
        for (Card card : cards) {
            // Get the topic and use "General" if it's null
            String topic = card.getTopic();
            if (topic == null) {
                topic = "General";
                // You might want to update the card to have a topic
                card.setTopic(topic);
            }
            uniqueTopics.add(topic);
        }

        // Convert to list for sorting
        List<String> topics = new ArrayList<>(uniqueTopics);

        // Remove null entries before sorting
        topics.removeIf(t -> t == null);

        // Only sort if there are items
        if (!topics.isEmpty()) {
            Collections.sort(topics); // Sort alphabetically
        }

        return topics;
    }

    // Get cards filtered by topic
    public List<Card> getCardsByTopic(String topic) {
        if (topic == null || topic.equals("All Topics")) {
            return new ArrayList<>(cards);
        }

        List<Card> filteredCards = new ArrayList<>();
        for (Card card : cards) {
            if (card.getTopic().equals(topic)) {
                filteredCards.add(card);
            }
        }
        return filteredCards;
    }

    public Module(String name) {
        this.name = name;
        this.cards = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public void removeCard(int index) {
        if (index >= 0 && index < cards.size()) {
            cards.remove(index);
        }
    }

    public int getCardCount() {
        return cards.size();
    }

    public void shuffleCards() {
        Collections.shuffle(cards);
    }
}