package br.com.gustavo.movieflix.services;

import br.com.gustavo.movieflix.dto.MovieDTO;
import br.com.gustavo.movieflix.entities.Movie;
import br.com.gustavo.movieflix.projections.MovieProjection;
import br.com.gustavo.movieflix.repositories.MovieRepository;
import br.com.gustavo.movieflix.services.exceptions.ResourceNotFoundException;
import br.com.gustavo.movieflix.util.Utils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
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

        List<Long> genreIds = null;
        if (!"0".equals(genreId)) {
            genreIds = Arrays.asList(genreId.split(",")).stream().map(Long::parseLong).toList();
        }

        Page<MovieProjection> page = movieRepository.searchMoviesByGenre(genreIds, pageable);
        List<Long> movieIds = page.map(x -> x.getId()).toList();

        List<Movie> entities = movieRepository.searchMoviesWithReviews(movieIds);

        entities = (List<Movie>) Utils.replace(page.getContent(), entities);

        List<MovieDTO> dtos = entities.stream().map(MovieDTO::new).toList();

        return new PageImpl<>(dtos, page.getPageable(), page.getTotalElements());
    }
}
