package com.odyssey.util.file;

import com.odyssey.model.Order;
import com.odyssey.service.OrderService;
import com.odyssey.util.Util;
import com.odyssey.util.xml.XMLParser;

import javax.servlet.AsyncContext;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;

public class MultiPartReadListener implements ReadListener {
    private enum State {
        BODY_START, BODY_WAIT, BODY_END
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
    private byte[] incomplete = new byte[1024];

    public MultiPartReadListener(ServletInputStream in, AsyncContext ac, HttpServletResponse resp,
                                 OrderService service, String boundary) {
        this.input = in;
        this.context = ac;
        this.resp = resp;
        this.orderService = service;
        this.boundary = boundary.getBytes(Charset.forName("UTF-8"));
        order = new Order();
        parser = new XMLParser(order);
        state = State.BODY_START;
    }

    private void setState(State state) {
        this.state = state;
    }

    @Override
    public void onDataAvailable() {
        byte[] buffer = new byte[BUFFER_SIZE];
        try {
            do {
                switch (state) {
                    case BODY_START:
                        input.read(buffer);
                        Util.writeToFile(buffer);
                        for (int i = 0; i < buffer.length; i++) {
                            // Look for the initial boundary
                            if (buffer[i] == boundary[0] &&
                                    buffer[i + 1] == boundary[1] &&
                                    buffer[i + boundary.length - 1] == boundary[boundary.length - 1]) {
                                for (int j = i; j < buffer.length; j++) {
                                    // Got boundary. Look for the XML declaration (<? characters)
                                    if (buffer[j] == 60 && buffer[j + 1] == 63) {
                                        for (int k = j; k < buffer.length; k++) {
                                            // Copy data into the byte array until the boundary is found
                                            body[k] = buffer[k];
                                            if (buffer[k] == boundary[0] &&
                                                    buffer[k + 1] == boundary[1] &&
                                                    buffer[k + boundary.length - 1] == boundary[boundary.length - 1]) {
                                                body[k] = 0; // delete 1 redundant byte
                                                body = Util.trimBytes(body);
                                                parser.parseBytes(body);
                                                setState(State.BODY_END);
                                                break;
                                            } else if (k == buffer.length - 1) { // if there's no final boundary
                                                setState(State.BODY_WAIT);
                                                break;
                                            }
                                        }
                                    } else if (j == buffer.length - 1) { // if the file is empty
                                        break;
                                    } else if (state != State.BODY_START) {
                                        break;
                                    }
                                }
                            } else if (i == buffer.length - 1 || state != State.BODY_START) { // if there's no initial boundary
                                break;                                                        // or we've finished with the first part
                            }
                        }

                        if (state == State.BODY_WAIT && !Util.equalElements(body)) {
                            deletePartAfter();
                            body = Util.trimBytes(body);
                            parser.parseBytes(body);
                            break;
                        } else if (Util.equalElements(body)) {
                            throw new IOException("The file is empty!");
                        }
                        break;
                    case BODY_WAIT:
                        input.read(buffer);
                        body = new byte[buffer.length];
                        for (int i = 0; i < buffer.length; i++) {
                            body[i] = buffer[i];
                            // Look for the final boundary
                            if (buffer[i] == boundary[0] &&
                                    buffer[i + 1] == boundary[1] &&
                                    buffer[i + boundary.length - 1] == boundary[boundary.length - 1]) {
                                body[i] = 0; // deleting 1 redundant byte
                                addPartBefore();
                                parser.parseBytes(body);
                                setState(State.BODY_END);
                                break;
                            } else if (i == buffer.length - 1 && state == State.BODY_WAIT) {
                                addPartBefore();
                                deletePartAfter();
                                parser.parseBytes(body);
                                break;
                            }
                        }
                        break;
                    default:
                        break;
                }
            } while (input.isReady() && !input.isFinished());
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
        } catch (XMLStreamException e) {
            System.err.println("XML parsing error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onAllDataRead() {
        if (!order.getItemVol().isEmpty() &&
                order.getDepZip() != null &&
                order.getDelZip() != null) {
            int orderID = orderService.addOrder(order);

            try (PrintWriter out = resp.getWriter()) {
                out.print(orderID);
            } catch (IOException e) {
                System.err.println("PrintWriter error: " + e.getMessage());
                e.printStackTrace();
            }
            context.complete();
        }
    }

    @Override
    public void onError(Throwable t) {
        t.printStackTrace();
        try (PrintWriter out = resp.getWriter()) {
            out.print(t.getMessage());
        } catch (IOException e) {
            System.err.println("PrintWriter error: " + e.getMessage());
            e.printStackTrace();
        }
        context.complete();
    }

    /**
     * if the body doesn't end with the closing tag ">" (byte #62),
     * we cut out the incomplete part into a temporary byte array
     * and then place it into the beginning of the next byte chunk.
     */
    private void deletePartAfter() {
        if (body[body.length - 1] != 62 &&
                body[body.length - 1] != 10 &&
                body[body.length - 1] != 13) {
            incomplete = new byte[body.length];
            body = Util.trimBytes(body);

            for (int i = body.length - 1; i >= 0; i--) {
                if (body[i] == 62 && body[i - 1] == 47) {
                    // Copy the incomplete part from the body into incomplete
                    System.arraycopy(body, ++i, incomplete, 0, body.length - i);
                    incomplete = Util.trimBytes(incomplete);

                    // Then save the complete part of the body
                    byte[] completePart = new byte[body.length - incomplete.length];
                    System.arraycopy(body, 0, completePart, 0, body.length - incomplete.length);

                    completePart = Util.trimBytes(completePart);
                    body = new byte[completePart.length];
                    System.arraycopy(completePart, 0, body, 0, completePart.length);

                    addTagsToBody(false);
                    break;
                }
            }
        }
    }

    /**
     * Add necessary tags to the <codebyte[] >body</code><br>
     * True - initial tags.<br>
     * False - closing tags.<br>
     *
     * @param initial true for initial tags, false - closing
     */
    private void addTagsToBody(boolean initial) {
        if (initial) {
            byte[] tags = "<order><lines>".getBytes(Charset.forName("UTF-8"));
            byte[] newBody = new byte[body.length + tags.length];
            System.arraycopy(tags, 0, newBody, 0, tags.length);
            System.arraycopy(body, 0, newBody, tags.length, body.length);
            body = new byte[newBody.length];
            System.arraycopy(newBody, 0, body, 0, newBody.length);
        } else {
            byte[] tags = "</lines></order>".getBytes(Charset.forName("UTF-8"));
            byte[] newBody = new byte[body.length + tags.length];
            System.arraycopy(body, 0, newBody, 0, body.length);
            System.arraycopy(tags, 0, newBody, body.length, tags.length);
            body = new byte[newBody.length];
            System.arraycopy(newBody, 0, body, 0, newBody.length);
        }
    }

    /**
     * Adding <code>incomplete</code> before <code>body</code>
     * and initial tags as well.
     */
    private void addPartBefore() {
        body = Util.trimBytes(body);
        // Copy the incomplete part into the beginning of the current body
        byte[] tempBody = new byte[incomplete.length + body.length];
        System.arraycopy(incomplete, 0, tempBody, 0, incomplete.length);
        System.arraycopy(body, 0, tempBody, incomplete.length, body.length);
        body = new byte[tempBody.length];
        System.arraycopy(tempBody, 0, body, 0, tempBody.length);

        addTagsToBody(true); // attach initial tags
        body = Util.trimBytes(body);
    }
}