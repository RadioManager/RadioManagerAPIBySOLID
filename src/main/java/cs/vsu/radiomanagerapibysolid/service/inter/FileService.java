package cs.vsu.radiomanagerapibysolid.service.inter;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    String saveFile(MultipartFile file, Long userId);

    byte[] getFile(String filename);

    boolean deleteFile(String filename);

}
