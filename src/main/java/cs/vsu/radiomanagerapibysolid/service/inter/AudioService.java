package cs.vsu.radiomanagerapibysolid.service.inter;

import org.springframework.web.multipart.MultipartFile;

public interface AudioService extends FileService{

    double getAudioDuration(MultipartFile file);

}
