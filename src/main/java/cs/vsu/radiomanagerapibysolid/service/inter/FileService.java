package cs.vsu.radiomanagerapibysolid.service.inter;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    String saveFile(MultipartFile file, Long fileId);

    // TODO(ревью): было getFile(String filename) / deleteFile(String filename) — вызывающий код (старый
    //   AudioRecordingController) сам собирал имя на диске через generateUniqueFilename(id, filePath).
    //   Теперь get/delete принимают то же, что и saveFile: id + исходное имя файла (в AudioRecordingDto это
    //   getId() и getFilePath()), а правило «id_имя» знает только реализация сервиса.
    byte[] getFile(Long fileId, String originalFilename);

    boolean deleteFile(Long fileId, String originalFilename);

}
