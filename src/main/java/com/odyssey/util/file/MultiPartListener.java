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
    private byte[] incompletePart = new byte[1024];

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

    private void setState(State state) {
        this.state = state;
    }

    @Override
    public void onDataAvailable() {
        byte[] buffer = new byte[BUFFER_SIZE];
        try {
            do {
                input.read(buffer);
                switch (state) {
                    case BODY:
                        for (int i = 0; i <= buffer.length; i++) {
                            // looking for the boundary...
                            if (buffer[i] == boundary[0] && buffer[i + 5] == boundary[5]) {
                                // Inside boundary. Looking for the file's beginning...
                                for (int j = i; j <= buffer.length; j++) {
                                    // Looking for "<?" characters...
                                    if (buffer[j] == 60 && buffer[j + 1] == 63) {
                                        // Found the start of XML body.
                                        // Copying data into the byte array until boundary is found
                                        for (int k = j; k <= buffer.length; k++) {
                                            body[k] = buffer[k];
                                            // Looking for the closing boundary
                                            if (buffer[k] == boundary[0]
                                                    && buffer[k + 1] == boundary[1]
                                                    && buffer[k + boundary.length - 1] == boundary[boundary.length - 1]) {
                                                setState(State.BODY_END);
                                                body[k] = 0; // deleting 1 redundant byte
                                                Util.bytesToFile(body); // delete
                                                body = Util.trimBytes(body);
                                                parser.parseBytes(body);
                                                break;
                                            } else if (k == buffer.length - 1) {
                                                setState(State.BODY_WAIT);
                                                break;
                                            }
                                        }

                                    } else if (state == State.BODY_WAIT || state == State.BODY_END) {
                                        break;
                                    } else if (j == buffer.length - 1 && state != State.BODY_END) {
                                        // if the body's beginning wasn't found
                                        setState(State.BODY_WAIT);
                                    }
                                }
                            } else { // if the boundary wasn't found
                                setState(State.BODY_WAIT);
                            }

                            if (state == State.BODY_WAIT) {
                                body = Util.trimBytes(body);
                                Util.bytesToFile(body);
                                // if the body doesn't end with the closing tag ">" (byte figure 62),
                                // cutting out the incomplete part into a temporary byte array
                                makeBodyCompleted();
                                parser.parseBytes(body);
                                break;
                            } else if (state == State.BODY_END) {
                                break;
                            }
                        }
                        break;
                    case BODY_WAIT:
                        input.read(buffer);
                        Util.bytesToFile(buffer);
                        body = new byte[buffer.length];

                        for (int i = 0; i < buffer.length; i++) {
                            // Looking for the closing boundary
                            if (buffer[i] == boundary[0]
                                    && buffer[i + 1] == boundary[1]
                                    && buffer[i + boundary.length - 1] == boundary[boundary.length - 1]) {
                                body = Util.trimBytes(body);
                                // Copy incomplete part into the beginning of the current body
                                byte[] tempBody = new byte[incompletePart.length + body.length];
                                System.arraycopy(incompletePart, 0, tempBody, 0, tempBody.length);
                                System.arraycopy(body, 0, tempBody, incompletePart.length - 1, tempBody.length);
                                Util.bytesToFile(body);

                                setState(State.BODY_END);
                                Util.bytesToFile(body);
                                parser.parseBytes(Util.trimBytes(body));
                                break;
                            } else if (i == buffer.length - 1) {
                                body = Util.trimBytes(body);
                                Util.bytesToFile(body);
                                // Copy incomplete part into the beginning of the current body
                                byte[] tempBody = new byte[incompletePart.length + body.length];
                                System.arraycopy(incompletePart, 0, tempBody, 0, incompletePart.length);
                                System.arraycopy(body, 0, tempBody, incompletePart.length - 1, body.length);
                                Util.bytesToFile(tempBody);
                                tempBody = Util.trimBytes(tempBody);
                                System.arraycopy(tempBody, 0, body, 0, tempBody.length);
                                Util.bytesToFile(body);

                                makeBodyCompleted();
                                parser.parseBytes(body);
                                break;
                            }

                            body[i] = buffer[i];
                        }
                        break;
                    case BODY_END:
                        System.out.println("In BODY_END");
                        break;
                    default:
                        break;
                }
            } while (input.isReady() && !input.isFinished());
        } catch (IllegalStateException | IOException e) {
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

    private void makeBodyCompleted() {
        if (body[body.length - 1] != 62 &&
                body[body.length - 1] != 10) {
            incompletePart = new byte[body.length];

            for (int j = body.length - 1; j >= 0; j--) {
                if (body[j] == 62) {
                    // Copy incomplete part from body into the particular array
                    System.arraycopy(body, ++j, incompletePart, 0, body.length - j);
                    incompletePart = Util.trimBytes(incompletePart);
                    Util.bytesToFile(incompletePart);

                    // then save the complete part of body
                    byte[] completePart = new byte[body.length - incompletePart.length];
                    System.arraycopy(body, 0, completePart, 0, body.length - incompletePart.length);

                    Util.bytesToFile(completePart);
                    body = new byte[completePart.length];
                    System.arraycopy(completePart, 0, body, 0, completePart.length);

                    // Add closing tags to the completed body
                    byte[] tags = "</lines></order>".getBytes(Charset.forName("UTF-8"));
                    byte[] newBody = new byte[body.length + tags.length];
                    System.arraycopy(body, 0, newBody, 0, body.length);
                    System.arraycopy(tags, 0, newBody, body.length, tags.length);

                    body = new byte[newBody.length];
                    System.arraycopy(newBody, 0, body, 0, newBody.length);
                    Util.bytesToFile(body);
                    break;
                }
            }
        }
    }
}