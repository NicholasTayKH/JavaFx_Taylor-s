package Classes;
import java.time.LocalDateTime;

public class Notification {
    private int id;
    private String subject;
    private String content;
    private LocalDateTime timestamp;
    private boolean read;

    public Notification(int id, String subject, String content, LocalDateTime timestamp, boolean read) {
        this.id = id;
        this.subject = subject;
        this.content = content;
        this.timestamp = timestamp;
        this.read = read;
    }
    public Notification(int id, String subject, String content, LocalDateTime timestamp) {
        this.id = id;
        this.subject = subject;
        this.content = content;
        this.timestamp = timestamp;
        this.read = false;
    }


    public int getId() { return id; }
    public String getSubject() { return subject; }
    public String getContent() { return content; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public boolean isRead() {return read;}
    public void setRead(boolean read) {this.read = read;}
}
