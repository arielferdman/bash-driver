package bblfsh.bash;

import java.io.IOException;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Driver {

    private final RequestReader reader;
    private final ResponseWriter writer;

    public Driver(final RequestReader reader, final ResponseWriter writer) {
        this.reader = reader;
        this.writer = writer;
    }

    public void run() throws DriverException, CloseException {
        while (true) {
            this.processOne();
        }
    }

    private void printStdError(Throwable ex) {
        StringWriter outError = new StringWriter();
        ex.printStackTrace(new PrintWriter(outError));
        String errorString = outError.toString();
        System.err.println(errorString);
    }

    public void processOne() throws DriverException, CloseException {
        Request request;
        try {
            request = this.reader.read();
            final Response response = this.processRequest(request);
            this.writer.write(response);
        } catch (Exception ex) {
            // Show the error on stderr too to help with debugging
            printStdError(ex);

            final Response response = createFatalResponse(ex);
            try {
                this.writer.write(response);
            } catch (IOException ex2) {
                throw new DriverException("exception while writing fatal response", ex2);
            }

            return;
        }
    }

    private Response createFatalResponse(final Exception e) {
        final Response r = new Response();
        r.status = "fatal";
        r.errors = new ArrayList<>();
        r.errors.add(e.getMessage());
        return r;
    }

    private Response processRequest(final Request request) {
        Response response = new Response();
        response.ast = Parser.parse(request.content);

        response.status = "ok";
        return response;
    }
}
