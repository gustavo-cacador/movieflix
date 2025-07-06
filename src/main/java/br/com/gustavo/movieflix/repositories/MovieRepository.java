package br.com.gustavo.movieflix.repositories;

import br.com.gustavo.movieflix.entities.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Query("""
    SELECT DISTINCT m
    FROM Movie m
    LEFT JOIN FETCH m.reviews
    """)
    List<Movie> searchMoviesWithReviews(List<Long> movieIds);

    @Query("SELECT m FROM Movie m JOIN m.genre g WHERE (:genreIds IS NULL OR g.id IN :genreIds)")
    Page<Movie> searchMoviesByGenre(List<Long> genreIds, Pageable pageable);

}
