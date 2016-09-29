package com.odyssey.service;

import com.odyssey.controller.FileProcessController;
import com.odyssey.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.DeferredResultProcessingInterceptorAdapter;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

/**
 * Responsible for attaching a {@link MultipartReadListener} to the controller {@link FileProcessController}.
 */
public class DeferredResultInterceptor extends DeferredResultProcessingInterceptorAdapter {
    private final OrderService orderService;
    private final HttpServletRequest _request;

    @Autowired
    public DeferredResultInterceptor(OrderService orderService, HttpServletRequest request) {
        this.orderService = orderService;
        _request = request;
    }

    @Override
    public <T> void preProcess(NativeWebRequest request, DeferredResult<T> deferredResult) throws Exception {
        ServletInputStream input = _request.getInputStream();
        String boundary = Util.extractBoundary(_request.getHeader("Content-Type"));
        ReadListener listener = new MultipartReadListener(input, orderService, (DeferredResult<String>) deferredResult, boundary);
        input.setReadListener(listener);
    }
}