package Classes;

import java.time.LocalDateTime;

public class Rating {
    String username;
    int rating;
    String feedback;
    LocalDateTime date;

    public Rating(String username, int rating, String feedback, LocalDateTime date) {
        this.username = username;
        this.rating = rating;
        this.feedback = feedback;
        this.date = date;
    }

    public String getRatingUsername(){return username;}
    public int getRating(){return rating;}
    public String getFeedback(){return feedback;}
    public LocalDateTime getDate(){return date;}
    public String toRatingString(){
        return "Username: " + username + "\n" +
                "Rating: " + rating + "\n" +
                "Feedback: " + feedback + "\n" +
                "Date: " + date + "\n---";
    }
}