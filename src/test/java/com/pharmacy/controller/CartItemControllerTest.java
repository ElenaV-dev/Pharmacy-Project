package com.pharmacy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharmacy.constants.ApiEndpointConstants;
import com.pharmacy.dto.cart_item_dto.CartItemRequestDto;
import com.pharmacy.dto.cart_item_dto.CartItemResponseDto;
import com.pharmacy.dto.cart_item_dto.CartItemUpdateDto;
import com.pharmacy.security.JwtService;
import com.pharmacy.service.CartItemService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartItemController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CartItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CartItemService cartItemService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void createCartItem_shouldReturnCartItem_whenRequestIsValid() throws Exception {
        CartItemRequestDto requestDto = CartItemRequestDto.builder()
                .productId(10L)
                .quantity(2)
                .build();

        CartItemResponseDto responseDto = CartItemResponseDto.builder()
                .id(1L)
                .productId(10L)
                .productName("Vitamin C")
                .price(new BigDecimal("1500.00"))
                .quantity(2)
                .stock(20)
                .build();

        when(cartItemService.createCartItem(any(CartItemRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post(ApiEndpointConstants.CART_ITEM)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.productId").value(responseDto.getProductId()))
                .andExpect(jsonPath("$.productName").value(responseDto.getProductName()))
                .andExpect(jsonPath("$.price").value(responseDto.getPrice().doubleValue()))
                .andExpect(jsonPath("$.quantity").value(responseDto.getQuantity()))
                .andExpect(jsonPath("$.stock").value(responseDto.getStock()));

        ArgumentCaptor<CartItemRequestDto> requestCaptor = ArgumentCaptor.forClass(CartItemRequestDto.class);

        verify(cartItemService).createCartItem(requestCaptor.capture());

        CartItemRequestDto result = requestCaptor.getValue();

        assertEquals(requestDto.getProductId(), result.getProductId());
        assertEquals(requestDto.getQuantity(), result.getQuantity());
    }

    @Test
    void createCartItem_shouldReturnBadRequest_whenProductIdIsNull() throws Exception {
        CartItemRequestDto requestDto = CartItemRequestDto.builder()
                .productId(null)
                .quantity(2)
                .build();

        mockMvc.perform(post(ApiEndpointConstants.CART_ITEM)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(cartItemService);
    }

    @Test
    void createCartItem_shouldReturnBadRequest_whenQuantityIsNull() throws Exception {
        CartItemRequestDto requestDto = CartItemRequestDto.builder()
                .productId(10L)
                .quantity(null)
                .build();

        mockMvc.perform(post(ApiEndpointConstants.CART_ITEM)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(cartItemService);
    }

    @Test
    void createCartItem_shouldReturnBadRequest_whenQuantityIsZero() throws Exception {
        CartItemRequestDto requestDto = CartItemRequestDto.builder()
                .productId(10L)
                .quantity(0)
                .build();

        mockMvc.perform(post(ApiEndpointConstants.CART_ITEM)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(cartItemService);
    }

    @Test
    void updateQuantity_shouldUpdateQuantity_whenRequestIsValid() throws Exception {
        CartItemUpdateDto requestDto = CartItemUpdateDto.builder()
                .quantity(5)
                .build();

        CartItemResponseDto responseDto = CartItemResponseDto.builder()
                .id(1L)
                .productId(10L)
                .productName("Vitamin C")
                .price(new BigDecimal("1500.00"))
                .quantity(5)
                .stock(20)
                .build();

        when(cartItemService.updateQuantity(anyLong(), any(CartItemUpdateDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(put(ApiEndpointConstants.CART_ITEM + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.productId").value(responseDto.getProductId()))
                .andExpect(jsonPath("$.productName").value(responseDto.getProductName()))
                .andExpect(jsonPath("$.price").value(responseDto.getPrice().doubleValue()))
                .andExpect(jsonPath("$.quantity").value(responseDto.getQuantity()))
                .andExpect(jsonPath("$.stock").value(responseDto.getStock()));

        ArgumentCaptor<CartItemUpdateDto> requestCaptor = ArgumentCaptor.forClass(CartItemUpdateDto.class);

        verify(cartItemService).updateQuantity(eq(1L), requestCaptor.capture());

        CartItemUpdateDto result = requestCaptor.getValue();

        assertEquals(requestDto.getQuantity(), result.getQuantity());
    }

    @Test
    void updateQuantity_shouldReturnBadRequest_whenQuantityIsNull() throws Exception {
        CartItemUpdateDto requestDto = CartItemUpdateDto.builder()
                .quantity(null)
                .build();

        mockMvc.perform(put(ApiEndpointConstants.CART_ITEM + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(cartItemService);
    }

    @Test
    void updateQuantity_shouldReturnBadRequest_whenQuantityIsZero() throws Exception {
        CartItemUpdateDto requestDto = CartItemUpdateDto.builder()
                .quantity(0)
                .build();

        mockMvc.perform(put(ApiEndpointConstants.CART_ITEM + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(cartItemService);
    }

    @Test
    void deleteById_shouldReturnOk_whenCartItemExists() throws Exception {
        mockMvc.perform(delete(ApiEndpointConstants.CART_ITEM + "/1"))
                .andExpect(status().isOk());

        verify(cartItemService).deleteById(1L);
    }
}
