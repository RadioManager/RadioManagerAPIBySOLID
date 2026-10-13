package cs.vsu.radiomanagerapibysolid.exception;

/**
 * HTTP 409 Conflict — запрос корректный, но противоречит текущему состоянию данных
 * (например, аудиозапись длиннее слота, слот уже занят).
 * В ответ превращается в GlobalExceptionHandler.
 */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
