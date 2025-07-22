package br.com.gustavo.movieflix.services;

import br.com.gustavo.movieflix.dto.ReviewInsertDTO;
import br.com.gustavo.movieflix.entities.Review;
import br.com.gustavo.movieflix.repositories.MovieRepository;
import br.com.gustavo.movieflix.repositories.ReviewRepository;
import br.com.gustavo.movieflix.services.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MovieRepository movieRepository;
    private final AuthService authService;

    public ReviewService(ReviewRepository reviewRepository, MovieRepository movieRepository, AuthService authService) {
        this.reviewRepository = reviewRepository;
        this.movieRepository = movieRepository;
        this.authService = authService;
    }

    @Transactional
    public ReviewInsertDTO insert(ReviewInsertDTO dto) {
        var review = new Review();
        review.setText(dto.getText());
        review.setMovie(movieRepository.getReferenceById(dto.getMovieId()));

        if (dto.getMovieId() == null) {
            throw new IllegalArgumentException("Movie ID is required");
        }

        Long movieId = dto.getMovieId();
        var movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
        review.setMovie(movie);

        var user = authService.authenticated();
        review.setUser(user);

        review = reviewRepository.save(review);
        return new ReviewInsertDTO(review);
    }
}
