package com.example.warehouse.currency;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CurrencyFilter extends OncePerRequestFilter {
    private final CurrencyProvider currencyProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String currencyHeader = request.getHeader("currency");
        if (currencyHeader != null && !currencyHeader.isEmpty()) {
            currencyProvider.setCurrency(currencyHeader);
        }

        filterChain.doFilter(request, response);
    }
}
