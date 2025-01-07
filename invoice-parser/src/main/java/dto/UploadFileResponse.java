package dto;  // Package declaration should match the directory structure

public class UploadFileResponse {
    private String message;
    private String filename;

    public UploadFileResponse(String message, String filename) {
        this.message = message;
        this.filename = filename;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}