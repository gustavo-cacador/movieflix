package br.com.gustavo.movieflix.dto;

import br.com.gustavo.movieflix.entities.Genre;
import br.com.gustavo.movieflix.entities.Movie;
import br.com.gustavo.movieflix.entities.Review;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MovieDTO {

    private Long id;
    private String title;
    private String subTitle;
    private Integer movieYear;
    private String imgUrl;
    private String synopsis;

    private GenreDTO genre;

    private List<ReviewDTO> reviews = new ArrayList<>();

    public MovieDTO() {
    }

    public MovieDTO(Long id, String title, String subTitle, Integer movieYear, String imgUrl, String synopsis) {
        this.id = id;
        this.title = title;
        this.subTitle = subTitle;
        this.movieYear = movieYear;
        this.imgUrl = imgUrl;
        this.synopsis = synopsis;
    }

    public MovieDTO(Movie entity) {
        id = entity.getId();
        title = entity.getTitle();
        subTitle = entity.getSubTitle();
        movieYear = entity.getMovieYear();
        imgUrl = entity.getImgUrl();
        synopsis = entity.getSynopsis();

        entity.getReviews().forEach(review -> this.reviews.add(new ReviewDTO(review)));
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public Integer getMovieYear() {
        return movieYear;
    }

    public void setMovieYear(Integer movieYear) {
        this.movieYear = movieYear;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public List<ReviewDTO> getReviews() {
        return reviews;
    }

    public void setReviews(List<ReviewDTO> reviews) {
        this.reviews = reviews;
    }
}
