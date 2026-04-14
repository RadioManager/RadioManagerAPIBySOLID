package cs.vsu.radiomanagerapibysolid.service.inter;

import org.springframework.data.util.Pair;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public interface DataTableService extends FileService{

    List<Pair<LocalDateTime, LocalDateTime>> getTimeFromTable(MultipartFile file);

}
