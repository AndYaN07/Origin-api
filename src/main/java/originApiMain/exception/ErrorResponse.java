package originApiMain.exception;

import java.time.LocalDateTime;

//Clase para personalizar las respuestas a los errores


public class ErrorResponse {
    private LocalDateTime timestamp;
    private Integer status;
    private String message;
    private String path;

    public ErrorResponse(LocalDateTime timestamp, Integer status, String message, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.message = message;
        this.path = path;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Integer getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

}//class
