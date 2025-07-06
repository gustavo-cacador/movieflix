package br.com.gustavo.movieflix.dto;

import br.com.gustavo.movieflix.entities.Review;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ReviewInsertDTO {

    private Long id;
    private String text;
    private String userName;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private MovieDTO movie;

    public ReviewInsertDTO() {
    }

    public ReviewInsertDTO(Long id, String text) {
        this.id = id;
        this.text = text;
    }

    public ReviewInsertDTO(Review entity) {
        id = entity.getId();
        text = entity.getText();

        if (entity.getUser() != null) {
            this.userName = entity.getUser().getName();
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

    public MovieDTO getMovie() {
        return movie;
    }
}
