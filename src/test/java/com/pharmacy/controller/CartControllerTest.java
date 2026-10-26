package com.pharmacy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharmacy.constants.ApiEndpointConstants;
import com.pharmacy.dto.cart_dto.CartRequestDto;
import com.pharmacy.dto.cart_dto.CartResponseDto;
import com.pharmacy.dto.cart_item_dto.CartItemRequestDto;
import com.pharmacy.dto.cart_item_dto.CartItemResponseDto;
import com.pharmacy.security.JwtService;
import com.pharmacy.service.CartService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CartService cartService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void merge_shouldReturnCart_whenRequestIsValid() throws Exception {
        CartItemRequestDto requestItem = CartItemRequestDto.builder()
                .productId(10L)
                .quantity(2)
                .build();

        CartRequestDto requestDto = CartRequestDto.builder()
                .items(List.of(requestItem))
                .build();

        CartItemResponseDto responseItem = CartItemResponseDto.builder()
                .id(1L)
                .productId(10L)
                .productName("Vitamin C")
                .price(new BigDecimal("1500.00"))
                .quantity(2)
                .stock(20)
                .build();

        CartResponseDto responseDto = CartResponseDto.builder()
                .items(List.of(responseItem))
                .build();

        when(cartService.merge(any(CartRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post(ApiEndpointConstants.CART + ApiEndpointConstants.CART_MERGE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()").value(responseDto.getItems().size()))
                .andExpect(jsonPath("$.items[0].id").value(responseItem.getId()))
                .andExpect(jsonPath("$.items[0].productId").value(responseItem.getProductId()))
                .andExpect(jsonPath("$.items[0].productName").value(responseItem.getProductName()))
                .andExpect(jsonPath("$.items[0].price").value(responseItem.getPrice().doubleValue()))
                .andExpect(jsonPath("$.items[0].quantity").value(responseItem.getQuantity()))
                .andExpect(jsonPath("$.items[0].stock").value(responseItem.getStock()));

        ArgumentCaptor<CartRequestDto> requestCaptor = ArgumentCaptor.forClass(CartRequestDto.class);

        verify(cartService).merge(requestCaptor.capture());

        CartRequestDto result = requestCaptor.getValue();

        assertEquals(requestDto.getItems().size(), result.getItems().size());
        assertEquals(requestItem.getProductId(), result.getItems().get(0).getProductId());
        assertEquals(requestItem.getQuantity(), result.getItems().get(0).getQuantity());
    }

    @Test
    void merge_shouldReturnBadRequest_whenItemsIsEmpty() throws Exception {
        CartRequestDto requestDto = CartRequestDto.builder()
                .items(List.of())
                .build();

        mockMvc.perform(post(ApiEndpointConstants.CART + ApiEndpointConstants.CART_MERGE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(cartService);
    }

    @Test
    void merge_shouldReturnBadRequest_whenItemsIsNull() throws Exception {
        CartRequestDto requestDto = CartRequestDto.builder()
                .items(null)
                .build();

        mockMvc.perform(post(ApiEndpointConstants.CART + ApiEndpointConstants.CART_MERGE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(cartService);
    }

    @Test
    void merge_shouldReturnBadRequest_whenProductIdIsNull() throws Exception {
        CartItemRequestDto requestItem = CartItemRequestDto.builder()
                .productId(null)
                .quantity(2)
                .build();

        CartRequestDto requestDto = CartRequestDto.builder()
                .items(List.of(requestItem))
                .build();

        mockMvc.perform(post(ApiEndpointConstants.CART + ApiEndpointConstants.CART_MERGE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(cartService);
    }

    @Test
    void merge_shouldReturnBadRequest_whenQuantityIsNull() throws Exception{
        CartItemRequestDto requestItem = CartItemRequestDto.builder()
                .productId(10L)
                .quantity(null)
                .build();

        CartRequestDto requestDto = CartRequestDto.builder()
                .items(List.of(requestItem))
                .build();

        mockMvc.perform(post(ApiEndpointConstants.CART + ApiEndpointConstants.CART_MERGE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(cartService);
    }

    @Test
    void merge_shouldReturnBadRequest_whenQuantityIsZero() throws Exception{
        CartItemRequestDto requestItem = CartItemRequestDto.builder()
                .productId(10L)
                .quantity(0)
                .build();

        CartRequestDto requestDto = CartRequestDto.builder()
                .items(List.of(requestItem))
                .build();

        mockMvc.perform(post(ApiEndpointConstants.CART + ApiEndpointConstants.CART_MERGE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(cartService);
    }

    @Test
    void getCart_shouldReturnCart_whenCartExists() throws Exception {
        CartResponseDto responseDto = CartResponseDto.builder()
                .items(new ArrayList<>())
                .build();

        when(cartService.getCart()).thenReturn(responseDto);

        mockMvc.perform(get(ApiEndpointConstants.CART))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items").isEmpty());

        verify(cartService).getCart();
    }

    @Test
    void getCart_shouldReturnCartWithItems_whenCartExists() throws Exception {
        CartItemResponseDto responseItem = CartItemResponseDto.builder()
                .id(1L)
                .productId(10L)
                .productName("Vitamin C")
                .price(new BigDecimal("1500.00"))
                .quantity(2)
                .stock(20)
                .build();

        CartResponseDto responseDto = CartResponseDto.builder()
                .items(List.of(responseItem))
                .build();

        when(cartService.getCart()).thenReturn(responseDto);

        mockMvc.perform(get(ApiEndpointConstants.CART))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()").value(responseDto.getItems().size()))
                .andExpect(jsonPath("$.items[0].id").value(responseItem.getId()))
                .andExpect(jsonPath("$.items[0].productId").value(responseItem.getProductId()))
                .andExpect(jsonPath("$.items[0].productName").value(responseItem.getProductName()))
                .andExpect(jsonPath("$.items[0].price").value(responseItem.getPrice().doubleValue()))
                .andExpect(jsonPath("$.items[0].quantity").value(responseItem.getQuantity()))
                .andExpect(jsonPath("$.items[0].stock").value(responseItem.getStock()));

        verify(cartService).getCart();
    }

}
