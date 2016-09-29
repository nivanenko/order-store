package com.odyssey.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
@RequestMapping("/upload")
public class FileProcessController {
    /**
     * See {@link com.odyssey.service.DeferredResultInterceptor} for the details.
     *
     * @return the deferred result
     */
    @RequestMapping(method = RequestMethod.POST)
    public DeferredResult<String> processFile() {
        return new DeferredResult<>();
    }
}