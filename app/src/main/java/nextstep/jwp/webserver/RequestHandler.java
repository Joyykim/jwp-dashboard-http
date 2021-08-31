package nextstep.jwp.webserver;

import nextstep.jwp.application.controller.StaticFileController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;

    public RequestHandler(Socket connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {

            String requestString = readInputStream(inputStream);
            HttpRequest httpRequest = new HttpRequest(requestString);
            HttpResponse httpResponse = new HttpResponse();

            Controller controller = getController(httpRequest);
            controller.service(httpRequest, httpResponse);

            outputStream.write(httpResponse.toBytes());
            outputStream.flush();
        } catch (IOException ioException) {
            log.error("Exception stream", ioException);
        } catch (Exception exception) {
            log.error("Exception", exception);
        } finally {
            close();
        }
    }

    private String readInputStream(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        List<String> lines = new ArrayList<>();
        while (reader.ready()) {
            line = reader.readLine();
            if (line == null) {
                break;
            }

            lines.add(line);
        }

        return String.join("\r\n", lines);
    }

    private Controller getController(HttpRequest httpRequest) {
        Controller controller = Router.get(httpRequest.getUri());
        return Objects.requireNonNullElseGet(controller, StaticFileController::new);
    }

    private void close() {
        try {
            connection.close();
            log.debug("Client Connection Close! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
