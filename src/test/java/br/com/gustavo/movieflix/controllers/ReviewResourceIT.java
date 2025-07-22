package br.com.gustavo.movieflix.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.gustavo.movieflix.dto.ReviewInsertDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import br.com.gustavo.movieflix.dto.ReviewDTO;
import br.com.gustavo.movieflix.tests.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ReviewResourceIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenUtil tokenUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private String visitorUsername;
    private String visitorPassword;
    private String memberUsername;
    private String memberPassword;

    @BeforeEach
    void setUp() throws Exception {

        visitorUsername = "bob@gmail.com";
        visitorPassword = "123456";
        memberUsername = "ana@gmail.com";
        memberPassword = "123456";
    }

    @Test
    public void insertShouldReturnUnauthorizedWhenNotValidToken() throws Exception {

        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setText("Gostei do filme!");
        reviewDTO.setMovieId(1L);

        String jsonBody = objectMapper.writeValueAsString(reviewDTO);

        ResultActions result =
                mockMvc.perform(post("/reviews")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnauthorized());
    }

    @Test
    public void insertShouldReturnForbiddenWhenVisitorAuthenticated() throws Exception {

        String accessToken = tokenUtil.obtainAccessToken(mockMvc, visitorUsername, visitorPassword);

        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setText("Gostei do filme!");
        reviewDTO.setMovieId(1L);

        String jsonBody = objectMapper.writeValueAsString(reviewDTO);

        ResultActions result =
                mockMvc.perform(post("/reviews")
                        .header("Authorization", "Bearer " + accessToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isForbidden());
    }

    @Test
    public void insertShouldInsertReviewWhenMemberAuthenticatedAndValidData() throws Exception {

        String accessToken = tokenUtil.obtainAccessToken(mockMvc, memberUsername, memberPassword);

        String reviewText = "Gostei do filme!";
        long movieId = 1L;

        ReviewInsertDTO reviewInsertDTO = new ReviewInsertDTO();
        reviewInsertDTO.setText(reviewText);
        reviewInsertDTO.setMovieId(movieId);

        String jsonBody = objectMapper.writeValueAsString(reviewInsertDTO);

        ResultActions result =
                mockMvc.perform(post("/reviews")
                        .header("Authorization", "Bearer " + accessToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isCreated());

        result.andExpect(jsonPath("$.id").isNotEmpty());
        result.andExpect(jsonPath("$.text").value(reviewText));
        result.andExpect(jsonPath("$.movieId").value(1L));
        result.andExpect(jsonPath("$.userId").isNotEmpty());
        result.andExpect(jsonPath("$.userName").isNotEmpty());
        result.andExpect(jsonPath("$.userEmail").value(memberUsername));
    }

    @Test
    public void insertShouldReturnUnproccessableEntityWhenMemberAuthenticatedAndInvalidData() throws Exception {

        String accessToken = tokenUtil.obtainAccessToken(mockMvc, memberUsername, memberPassword);

        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setText("        ");
        reviewDTO.setMovieId(1L);

        String jsonBody = objectMapper.writeValueAsString(reviewDTO);

        ResultActions result =
                mockMvc.perform(post("/reviews")
                        .header("Authorization", "Bearer " + accessToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnprocessableEntity());
    }
}
