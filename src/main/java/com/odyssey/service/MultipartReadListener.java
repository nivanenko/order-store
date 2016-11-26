package com.odyssey.service;

import com.odyssey.model.Order;
import com.odyssey.util.Util;
import com.odyssey.util.xml.XMLParser;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

public class MultipartReadListener implements ReadListener {
    enum State { // FSM
        CONTENT_START, CONTENT_PROCESS, CONTENT_END
    }

    private static final int BUFFER_SIZE = 1024 * 1024;

    /**
     * The (/, >) character in bytes
     */
    private static final byte[] TAG_CLOSE = {0x2F, 0x3E};

    /**
     * The (<, ?) characters in bytes
     */
    private static final byte[] XML_BEGIN = {0x3C, 0x3F};

    /**
     * The (\r) character in bytes
     */
    private static final byte CR = 0x0D;

    /**
     * The (\n) character in bytes
     */
    private static final byte LF = 0x0A;

    private final ServletInputStream input;
    private final OrderService orderService;
    private final Order order;
    private final XMLParser parser;
    private final DeferredResult<String> deferredResult;

    private State currentState;
    private byte[] boundary;
    private byte[] body;
    private byte[] incomplete;

    public MultipartReadListener(ServletInputStream input, OrderService service,
                                 DeferredResult<String> deferredResult, String boundary) {
        this.input = input;
        this.boundary = boundary.getBytes(Charset.forName("UTF-8"));
        this.orderService = service;
        this.deferredResult = deferredResult;

        body = new byte[BUFFER_SIZE];
        incomplete = new byte[1024];
        order = new Order();
        parser = new XMLParser(order);
        currentState = State.CONTENT_START;
    }

    private void setState(State nextState) {
        currentState = nextState;
    }

    @Override
    public void onDataAvailable() throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        try {
            do {
                switch (currentState) {
                    case CONTENT_START:
                        input.read(buffer);
                        for (int i = 0; i < buffer.length; i++) {
                            // Search for the initial boundary
                            if (buffer[i] == boundary[0] &&
                                    buffer[i + 1] == boundary[1] &&
                                    buffer[i + boundary.length - 1] == boundary[boundary.length - 1]) {
                                for (int j = i; j < buffer.length; j++) {
                                    // Reached boundary. Search for the XML declaration's start
                                    if (buffer[j] == XML_BEGIN[0] && buffer[j + 1] == XML_BEGIN[1]) {
                                        for (int k = j; k < buffer.length; k++) {
                                            body[k] = buffer[k];  // copying until the boundary's found
                                            if (buffer[k] == boundary[0] &&
                                                    buffer[k + 1] == boundary[1] &&
                                                    buffer[k + boundary.length - 1] == boundary[boundary.length - 1]) {
                                                body[k] = 0;
                                                body = Util.trimBytes(body);
                                                parser.parseBytes(body);
                                                setState(State.CONTENT_END);
                                                break;
                                            } else if (k == buffer.length - 1) { // if there's no final boundary
                                                setState(State.CONTENT_PROCESS);
                                                break;
                                            }
                                        }
                                    } else if ((j == buffer.length - 1) && currentState == State.CONTENT_PROCESS) {
                                        throw new IOException("The file is empty!");
                                    } else if (currentState != State.CONTENT_START) {
                                        break;
                                    }
                                }
                            } else if (i == buffer.length - 1 || currentState != State.CONTENT_START) { // if there's no initial boundary
                                break;                                                        // or we've finished with the first part
                            }
                        }

                        /* Whether the XML body doesn't end with the closing tag ">",
                           we cut out the incomplete part into a byte array and then
                           place it into the beginning of the next byte chunk in CONTENT_PROCESS state. */

                        if (currentState == State.CONTENT_PROCESS && !Util.equalElements(body)) {
                            cutChunkAfterBody();
                            body = Util.trimBytes(body);
                            parser.parseBytes(body);
                            break;
                        } else if (Util.equalElements(body)) {
                            throw new IOException("The file is empty!");
                        }
                        break;
                    case CONTENT_PROCESS:
                        input.read(buffer);
                        body = new byte[buffer.length];
                        for (int i = 0; i < buffer.length; i++) {
                            body[i] = buffer[i];
                            // Search for the final boundary
                            if (buffer[i] == boundary[0] &&
                                    buffer[i + 1] == boundary[1] &&
                                    buffer[i + boundary.length - 1] == boundary[boundary.length - 1]) {
                                body[i] = 0;
                                insertChunkBeforeBody();
                                parser.parseBytes(body);
                                setState(State.CONTENT_END);
                                break;
                            } else if (i == buffer.length - 1 && currentState == State.CONTENT_PROCESS) {
                                insertChunkBeforeBody();
                                cutChunkAfterBody();
                                parser.parseBytes(body);
                                break;
                            }
                        }
                        break;
                    default:
                        break;
                }
            } while (input.isReady() && !input.isFinished());
        } catch (Exception e) {
            deferredResult.setErrorResult(e);
        }
    }

    @Override
    public void onAllDataRead() {
        if (!order.getItemVol().isEmpty() &&
                order.getDepZip() != null &&
                order.getDelZip() != null) {
            deferredResult.setResult(String.valueOf(orderService.addOrder(order)));
        }
    }

    @Override
    public void onError(Throwable t) {
        deferredResult.setErrorResult(t);
    }

    /**
     * Cuts the incomplete part of the XML body and save it
     * into <code>byte[] incomplete</code>. Then the <code>body</code>
     * array attached with closing tags to make body completed for the XML parser.
     */
    private void cutChunkAfterBody() {
        if (body[body.length - 1] != TAG_CLOSE[1] &&
                body[body.length - 1] != CR &&
                body[body.length - 1] != LF) {
            incomplete = new byte[body.length];
            body = Util.trimBytes(body);

            for (int i = body.length - 1; i >= 0; i--) {
                if (body[i - 1] == TAG_CLOSE[0] && body[i] == TAG_CLOSE[1]) {
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
     * Add necessary tags to the <code>byte[] body</code><br>
     * True - opening tags.<br>
     * False - closing tags.<br>
     *
     * @param start true for opening tags, false - closing
     */
    private void addTagsToBody(boolean start) {
        if (start) {
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
     * and adding initial tags as well.
     */
    private void insertChunkBeforeBody() {
        body = Util.trimBytes(body);
        // Copy the incomplete chunk into the beginning of the current body
        byte[] tempBody = new byte[incomplete.length + body.length];
        System.arraycopy(incomplete, 0, tempBody, 0, incomplete.length);
        System.arraycopy(body, 0, tempBody, incomplete.length, body.length);
        body = new byte[tempBody.length];
        System.arraycopy(tempBody, 0, body, 0, tempBody.length);

        addTagsToBody(true);
        body = Util.trimBytes(body);
    }
}