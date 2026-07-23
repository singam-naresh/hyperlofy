package com.hyperlofy.backend.ai.recommendation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyperlofy.backend.ai.recommendation.dto.RecommendationActionRequest;
import com.hyperlofy.backend.ai.recommendation.dto.RecommendationResponse;
import com.hyperlofy.backend.ai.recommendation.repository.RecommendationRepository;
import com.hyperlofy.backend.security.jwt.JwtAuthenticationFilter;
import com.hyperlofy.backend.security.jwt.JwtTokenProvider;
import com.hyperlofy.backend.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecommendationController.class)
class RecommendationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RecommendationService recommendationService;

    @MockBean
    private RecommendationRepository recommendationRepository;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserRepository userRepository;

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void shouldReturnRecommendationsForAuthorizedCustomer() throws Exception {
        UUID customerId = UUID.randomUUID();
        RecommendationResponse response = RecommendationResponse.builder()
                .recommendationId(UUID.randomUUID())
                .customerId(customerId)
                .recommendedItem("Milk")
                .score(0.7)
                .build();

        when(recommendationService.fetchRecommendations(customerId)).thenReturn(java.util.List.of(response));

        mockMvc.perform(get("/api/v1/recommendations").param("customerId", customerId.toString()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void shouldAcceptRecommendation() throws Exception {
        RecommendationActionRequest request = RecommendationActionRequest.builder()
                .recommendationId(UUID.randomUUID())
                .build();

        RecommendationResponse response = RecommendationResponse.builder()
                .recommendationId(request.getRecommendationId())
                .customerId(UUID.randomUUID())
                .recommendedItem("Bread")
                .score(0.8)
                .build();

        when(recommendationService.acceptRecommendation(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/recommendations/accept")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void shouldDismissRecommendation() throws Exception {
        RecommendationActionRequest request = RecommendationActionRequest.builder()
                .recommendationId(UUID.randomUUID())
                .build();

        RecommendationResponse response = RecommendationResponse.builder()
                .recommendationId(request.getRecommendationId())
                .customerId(UUID.randomUUID())
                .recommendedItem("Mint")
                .score(0.4)
                .build();

        when(recommendationService.dismissRecommendation(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/recommendations/dismiss")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
