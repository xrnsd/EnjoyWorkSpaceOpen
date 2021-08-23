package kuyou.sdk.jt808.base.exceptions;

public class SocketManagerException extends Exception{
    public SocketManagerException() {
        super();
    }

    public SocketManagerException(String message) {
        super(message);
    }

    public SocketManagerException(String message, Throwable cause) {
        super(message, cause);
    }

    public SocketManagerException(Throwable cause) {
        super(cause);
    }
}
