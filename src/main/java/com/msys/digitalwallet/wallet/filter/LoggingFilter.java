package com.msys.digitalwallet.wallet.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Component
public class LoggingFilter /*extends OncePerRequestFilter*/ {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingFilter.class);


    private String getStringValue(byte[] contentAsByteArray, String characterEncoding) {
        try {
            return new String(contentAsByteArray, 0, contentAsByteArray.length, characterEncoding);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

//    @Override
//    protected void doFilterInternal(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, jakarta.servlet.FilterChain filterChain) throws jakarta.servlet.ServletException, IOException {
//        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
//        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
//
//        //filterChain.doFilter(requestWrapper, responseWrapper);
//
//        String requestBody = getStringValue(requestWrapper.getContentAsByteArray(),
//                request.getCharacterEncoding());
//        String responseBody = getStringValue(responseWrapper.getContentAsByteArray(),
//                response.getCharacterEncoding());
//
//        LOGGER.info(
//                "METHOD={}; REQUEST URI={}; REQUEST PAYLOAD={}; RESPONSE CODE={}; RESPONSE={};",
//                request.getMethod(), request.getRequestURI(), requestBody, response.getStatus(), responseBody);
//        responseWrapper.copyBodyToResponse();
//    }
}
