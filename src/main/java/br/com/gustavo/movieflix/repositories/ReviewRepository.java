package br.com.gustavo.movieflix.repositories;

import br.com.gustavo.movieflix.entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
