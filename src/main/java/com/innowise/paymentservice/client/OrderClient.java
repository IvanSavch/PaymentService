package com.innowise.paymentservice.client;

import com.innowise.paymentservice.model.dto.OrderDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class OrderClient {
    private static final String USER_ID_HEADER = "UserId";
    private static final String USER_ROLES_HEADER = "UserRoles";
    @Value("${order.service.url}")
    private String orderServiceUrl;
    private final RestTemplate restTemplate;

    public OrderClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<OrderDto> findOrderByUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();
        List<String> collect = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        HttpHeaders headers = new HttpHeaders();
        headers.set(USER_ID_HEADER,userId.toString());
        headers.set(USER_ROLES_HEADER,collect.getFirst());
        HttpEntity<?> httpEntity = new HttpEntity<>(headers);
        return restTemplate.exchange(orderServiceUrl + "/orders/users/{id}/orders",
                HttpMethod.GET,
                httpEntity,
                new ParameterizedTypeReference<List<OrderDto>>() {},
                userId).getBody();
    }
}
