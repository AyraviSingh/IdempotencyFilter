package com.idempotency.filter;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class IdempotencyFilter implements Filter {

    @Autowired
    private HazelcastInstance hazelcastInstance;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest; // Cast to HttpServletRequest
        HttpServletResponse response = (HttpServletResponse) servletResponse; // Cast to HttpServletResponse

        String uniqueId = request.getHeader("X-Unique-Id");

        if (StringUtils.hasText(uniqueId)) {
            // Access the Hazelcast cache
            IMap<String, String> idempotencyMap = hazelcastInstance.getMap("idempotencyMap");

            if (idempotencyMap.putIfAbsent(uniqueId, "processed") != null) {
                // If the unique ID exists in the cache, return a custom response
                response.getWriter().write("Transaction with the same unique ID already processed.");
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                return;
            }
        }

        // Continue processing the request if the unique ID is valid or not provided
        chain.doFilter(request, response);
    }

//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {
//        // Initialization code, if needed
//    }
//
//    @Override
//    public void destroy() {
//        // Cleanup code, if needed
//    }
}