package application.err;

public class WordError extends Exception{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    WordError() {
        super();
    }
    
    public WordError(String msg) {
        super(msg);
    }
    
    public WordError(String message, Throwable cause) {
        super(message, cause);
    }
    
    public WordError(Throwable cause) {
        super(cause);
    }
}

