package com.chaykin.paymentservice.interceptor;

import com.chaykin.common.exception.ServiceException;
import com.chaykin.paymentservice.persistence.model.IdempotencyKey;
import com.chaykin.paymentservice.service.IdempotencyService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static com.chaykin.common.model.payment.PaymentHeaders.KEY_HEADER;
import static com.chaykin.paymentservice.constant.WebConstants.WRAPPED_RESPONSE_ATTRIBUTE_NAME;
import static com.chaykin.paymentservice.persistence.model.IdempotencyStatus.COMPLETED;
import static com.chaykin.paymentservice.persistence.model.IdempotencyStatus.PENDING;
import static org.springframework.http.HttpMethod.POST;

@Component
@AllArgsConstructor
public class IdempotencyInterceptor implements HandlerInterceptor {

    private final IdempotencyService service;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        HttpMethod httpMethod = HttpMethod.valueOf(request.getMethod());

        if (httpMethod.equals(POST)) {
            UUID idempotencyKey = (UUID) request.getAttribute(KEY_HEADER);

            if (null == idempotencyKey) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println(KEY_HEADER + " header is not presented");
                return false;
            }

            processIdempotency(idempotencyKey, response);
        }

        return true;

    }

    private boolean processIdempotency(UUID key, HttpServletResponse response) throws IOException {
        Optional<IdempotencyKey> existingKey = service.findByKeyLocked(key);

        if (existingKey.isPresent()) {
            return processExistingKey(existingKey.get(), response);
        } else {
            return createNewKey(key, response);
        }

    }

    private boolean processExistingKey(IdempotencyKey idempotencyKey,
                                       HttpServletResponse response) throws IOException {
        var status = idempotencyKey.getIdempotencyStatus();

        if (status == PENDING) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            response.getWriter().println("Same request is already in progress...");
        } else if (status == COMPLETED) {
            response.setStatus(idempotencyKey.getStatusCode());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().println(idempotencyKey.getResponse());
        } else {
            throw new IllegalArgumentException("Invalid status of idempotency key");
        }

        return false;
    }

    private boolean createNewKey(UUID idempotencyKey,
                                 HttpServletResponse response) throws IOException {
        try {
            service.createPendingKey(idempotencyKey);
            return true;
        } catch (ServiceException e) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            response.getWriter().println("Same request is already in progress...");
            return false;
        }

    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request,
                                @NonNull HttpServletResponse response,
                                @NonNull Object handler,
                                Exception ex) throws Exception {
        var method = HttpMethod.valueOf(request.getMethod());

        if (method.equals(HttpMethod.POST) || method.equals(HttpMethod.PATCH)) {
            ContentCachingResponseWrapper wrappedResponse =
                    (ContentCachingResponseWrapper) request.getAttribute(WRAPPED_RESPONSE_ATTRIBUTE_NAME);
            String responseBody = new String(wrappedResponse.getContentAsByteArray(),
                                             wrappedResponse.getCharacterEncoding());

            UUID idempotencyKey = UUID.fromString(request.getHeader(KEY_HEADER));
            service.markAsCompleted(idempotencyKey, responseBody, response.getStatus());
        }
    }

}
