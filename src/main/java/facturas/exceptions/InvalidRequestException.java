package facturas.exceptions;

import org.apache.log4j.spi.ErrorCode;

public class InvalidRequestException extends Exception {
    private static final long serialVersionUID = 5324361403945574303L;

    private final String responseContent;

    public InvalidRequestException(String responseContent) {
        super("Invalid request: ");
        this.responseContent = responseContent;
    }

    public InvalidRequestException(String message, Throwable cause, String responseContent) {
        super(message, cause);
        this.responseContent = responseContent;
    }

    public InvalidRequestException(String message, String responseContent) {
        super(message);
        this.responseContent = responseContent;
    }

    public InvalidRequestException(Throwable cause, String responseContent) {
        super(cause);
        this.responseContent = responseContent;
    }

    public String getMessage() {
        return super.getMessage() + responseContent;
    }
}
