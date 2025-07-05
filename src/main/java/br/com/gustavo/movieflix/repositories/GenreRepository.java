package br.com.gustavo.movieflix.repositories;

import br.com.gustavo.movieflix.entities.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Long> {
}
