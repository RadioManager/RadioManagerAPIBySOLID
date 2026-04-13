package cs.vsu.radiomanagerapibysolid.service.inter;

public interface ResetService {

    void sendPasswordReset(String email);

    boolean updatePasswordByLogin(String login, String newPassword);

    boolean updatePasswordById(Long id, String newPassword);

}
