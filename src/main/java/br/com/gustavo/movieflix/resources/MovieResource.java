package br.com.gustavo.movieflix.resources;

import br.com.gustavo.movieflix.dto.MovieDTO;
import br.com.gustavo.movieflix.services.MovieService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("movies")
public class MovieResource {

    private final MovieService movieService;

    public MovieResource(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public ResponseEntity<Page<MovieDTO>> findAll(
            @RequestParam(value = "genreId", defaultValue = "") String genreId,
            Pageable pageable) {
        Page<MovieDTO> list = movieService.findAllPaged(genreId, pageable);
        return ResponseEntity.ok().body(list);
    }
}
