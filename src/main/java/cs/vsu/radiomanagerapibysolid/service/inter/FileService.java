package cs.vsu.radiomanagerapibysolid.service.inter;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    String saveFile(MultipartFile file, Long fileId);

    byte[] getFile(Long fileId, String originalFilename);

    boolean deleteFile(Long fileId, String originalFilename);

}
