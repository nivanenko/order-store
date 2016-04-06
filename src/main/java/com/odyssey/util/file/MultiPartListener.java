package com.odyssey.util.file;

import com.odyssey.model.Order;
import com.odyssey.service.OrderService;
import com.odyssey.util.Util;
import com.odyssey.util.xml.XMLParser;

import javax.servlet.AsyncContext;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;

public class MultiPartListener implements ReadListener {
    private enum State {
        BODY, BODY_WAIT, BODY_END
    }

    private ServletInputStream input;
    private AsyncContext context;
    private HttpServletResponse resp;
    private OrderService orderService;
    private State state;
    private Order order;
    private XMLParser parser;
    private byte[] boundary;

    private int BUFFER_SIZE = 1024 * 1024;
    private byte[] body = new byte[BUFFER_SIZE];

    private void setState(State state) {
        this.state = state;
    }

    public MultiPartListener(ServletInputStream in, AsyncContext ac, HttpServletResponse resp,
                             OrderService orderService, String boundaryStr) {
        this.input = in;
        this.context = ac;
        this.resp = resp;
        this.orderService = orderService;
        boundary = boundaryStr.getBytes(Charset.forName("UTF-8"));
        order = new Order();
        parser = new XMLParser(order);

        state = State.BODY;
    }

    @Override
    public void onDataAvailable() {
        byte[] buffer = new byte[BUFFER_SIZE];
        try {
            do {
                input.read(buffer);
                buffer = Util.trimBytes(buffer);
                switch (state) {
                    case BODY:
                        for (int i = 0; i < buffer.length; i++) {
                            // looking for the boundary...
                            if (buffer[i] == boundary[0] && buffer[i + 5] == boundary[5]) {
                                // Inside boundary. Looking for the file's start...
                                for (int j = i; j < buffer.length; j++) {
                                    // Looking for "<?" characters...
                                    if (buffer[j] == 60 && buffer[j + 1] == 63) {
                                        // Found the start of XML body. Copying data into byte array until boundary found
                                        for (int k = j; k < buffer.length; k++) {
                                            // Looking for the closing boundary
                                            if (buffer[k] == boundary[0]
                                                    && buffer[k + 1] == boundary[1]
                                                    && buffer[k + boundary.length - 1] == boundary[boundary.length - 1]) {
                                                setState(State.BODY_END);
                                                Util.bytesToFile(body);
                                                parser.parseBytes(Util.trimBytes(body));
                                                break;
                                            } else if (k == buffer.length - 1) {
                                                setState(State.BODY_WAIT);
                                                break;
                                            }

                                            body[k] = buffer[k];
                                        }

                                    } else if (state == State.BODY_WAIT) {
                                        break;
                                    } else if (j == buffer.length - 1 && state != State.BODY_END) {
                                        // if we didn't found the body start
                                        setState(State.BODY_WAIT);
                                    }
                                }
                            } else { // if the boundary didn't found
                                setState(State.BODY_WAIT);
                            }

                            if (state == State.BODY_WAIT) {
                                body = Util.trimBytes(body);
                                // Send part of byte into XML parser
                                Util.bytesToFile(body);
                                parser.parseBytes(body);
                                break;
                            }
                        }
                        break;
                    case BODY_WAIT:
                        input.read(buffer);
                        buffer = Util.trimBytes(buffer);
                        body = new byte[BUFFER_SIZE];

                        for (int i = 0; i < buffer.length; i++) {
                            // Looking for the closing boundary
                            if (buffer[i] == boundary[0]
                                    && buffer[i + 1] == boundary[1]
                                    && buffer[i + boundary.length - 1] == boundary[boundary.length - 1]) {
                                setState(State.BODY_END);
                                Util.bytesToFile(body);
                                parser.parseBytes(Util.trimBytes(body));
                                break;
                            } else if (i == buffer.length - 1) {
                                parser.parseBytes(Util.trimBytes(body));
                                break;
                            }

                            body[i] = buffer[i];
                        }
                        break;
                    case BODY_END:
                        break;
                    default:
                        break;
                }
            } while (input.isReady() && !input.isFinished());
        } catch (IllegalStateException e) {
            System.err.println("Illegal state exception: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAllDataRead() {
        int orderID = orderService.addOrder(order);

        try (PrintWriter out = resp.getWriter()) {
            out.print(orderID);
        } catch (IOException e) {
            System.err.println("IO error: " + e.getMessage());
            e.printStackTrace();
        }
        context.complete();
    }

    @Override
    public void onError(Throwable t) {
        System.err.println("Error: " + t.getMessage());
        t.printStackTrace();
        context.complete();
    }
}