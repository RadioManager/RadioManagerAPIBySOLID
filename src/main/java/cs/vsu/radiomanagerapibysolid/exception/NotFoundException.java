package cs.vsu.radiomanagerapibysolid.exception;

/**
 * HTTP 404 Not Found — запрошенная сущность не существует
 * (например, нет аудиозаписи или слота с переданным id).
 * В ответ превращается в GlobalExceptionHandler.
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
