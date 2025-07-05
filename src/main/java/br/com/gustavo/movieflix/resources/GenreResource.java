package br.com.gustavo.movieflix.resources;

import br.com.gustavo.movieflix.dto.GenreDTO;
import br.com.gustavo.movieflix.services.GenreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("genres")
public class GenreResource {

    private final GenreService genreService;

    public GenreResource(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public ResponseEntity<List<GenreDTO>> findAll() {
        List<GenreDTO> list = genreService.findAll();
        return ResponseEntity.ok(list);
    }
}
