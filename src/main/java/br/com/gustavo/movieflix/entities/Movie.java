package br.com.gustavo.movieflix.entities;

import br.com.gustavo.movieflix.projections.IdProjection;
import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "tb_movie")
public class Movie implements IdProjection<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String subTitle;
    private Integer movieYear;
    private String imgUrl;

    @Column(columnDefinition = "TEXT")
    private String synopsis;

    @ManyToOne
    @JoinColumn(name = "genre_id")
    private Genre genre;

    // aqui usamos List ao invés de Set pois n importa se o comentário sobre o filme for igual, o List mantém a ordem inserida (por data por exemplo) e o List eh melhor num caso de paginação (se tiver muitos comentários)
    @OneToMany(mappedBy = "movie")
    private List<Review> reviews = new ArrayList<>();

    public Movie() {}

    public Movie(Long id, String title, String subTitle, Integer movieYear, String imgUrl, String synopsis) {
        this.id = id;
        this.title = title;
        this.subTitle = subTitle;
        this.movieYear = movieYear;
        this.imgUrl = imgUrl;
        this.synopsis = synopsis;
    }

    @Override
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

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return Objects.equals(id, movie.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
