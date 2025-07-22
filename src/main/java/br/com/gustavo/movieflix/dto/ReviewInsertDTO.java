package br.com.gustavo.movieflix.dto;

import br.com.gustavo.movieflix.entities.Review;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public class ReviewInsertDTO {

    private Long id;

    @NotBlank(message = "O texto é obrigatório")
    private String text;
    private String userName;
    private String userEmail;
    private Long movieId;
    private Long userId;

    public ReviewInsertDTO() {
    }

    public ReviewInsertDTO(Long id, String text) {
        this.id = id;
        this.text = text;
    }

    public ReviewInsertDTO(Review entity) {
        id = entity.getId();
        text = entity.getText();
        movieId = entity.getMovie().getId();

        if (entity.getUser() != null) {
            this.userId = entity.getUser().getId();
            this.userName = entity.getUser().getName();
            this.userEmail = entity.getUser().getEmail();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
