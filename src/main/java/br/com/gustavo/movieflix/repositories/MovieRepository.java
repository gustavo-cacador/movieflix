package br.com.gustavo.movieflix.repositories;

import br.com.gustavo.movieflix.entities.Movie;
import br.com.gustavo.movieflix.projections.MovieProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Query("SELECT m.id AS id, m.title AS title FROM Movie m JOIN m.genre g " +
            "WHERE (:genreIds IS NULL OR g.id IN :genreIds)" +
            "ORDER BY m.title ASC")
    Page<MovieProjection> searchMoviesByGenre(@Param("genreIds") List<Long> genreIds, Pageable pageable);


    @Query("SELECT obj FROM Movie obj JOIN FETCH obj.genre "
            + "WHERE obj.id IN :movieIds")
    List<Movie> searchMoviesWithReviews(List<Long> movieIds);
}
