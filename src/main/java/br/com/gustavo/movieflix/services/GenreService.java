package br.com.gustavo.movieflix.services;

import br.com.gustavo.movieflix.dto.GenreDTO;
import br.com.gustavo.movieflix.entities.Genre;
import br.com.gustavo.movieflix.repositories.GenreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GenreService {

    private final GenreRepository genreRepository;

    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Transactional(readOnly = true)
    public List<GenreDTO> findAll() {
        List<Genre> list = genreRepository.findAll();
        return list.stream().map(GenreDTO::new).collect(Collectors.toList());
    }
}
