package br.com.gustavo.movieflix.repositories;

import br.com.gustavo.movieflix.entities.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Query("SELECT m FROM Movie m WHERE (:genreId = 0 OR m.genre.id = :genreId)")
    Page<Movie> searchMoviesByGenre(@Param("genreId") String genreId, Pageable pageable);
}
