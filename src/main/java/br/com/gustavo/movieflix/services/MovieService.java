package br.com.gustavo.movieflix.services;

import br.com.gustavo.movieflix.dto.MovieDTO;
import br.com.gustavo.movieflix.entities.Movie;
import br.com.gustavo.movieflix.repositories.MovieRepository;
import br.com.gustavo.movieflix.services.exceptions.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Transactional(readOnly = true)
    public MovieDTO findById(Long id) {
        Optional<Movie> obj = movieRepository.findById(id);
        var movie = obj.orElseThrow(
                () -> new ResourceNotFoundException("Filme com id: " + id + ", não encontrado"));
        return new MovieDTO(movie);
    }

    @Transactional(readOnly = true)
    public Page<MovieDTO> findAllPaged(String genreId, Pageable pageable) {
        Page<Movie> page = movieRepository.searchMoviesByGenre(genreId, pageable);
        return page.map(MovieDTO::new);
    }
}
