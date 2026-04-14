package cs.vsu.radiomanagerapibysolid.mapper;

import cs.vsu.radiomanagerapibysolid.dto.TransactionDto;
import cs.vsu.radiomanagerapibysolid.model.Transaction;
import cs.vsu.radiomanagerapibysolid.model.User;
import cs.vsu.radiomanagerapibysolid.repository.UserRep;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring", uses = UserRep.class)
public abstract class TransactionMapper {

    @Autowired
    private UserRep userRep;

    @Mappings({
            @Mapping(source = "user.id", target = "userId"),
            @Mapping(source = "admin.id", target = "adminId")
    })
    public abstract TransactionDto toDto(Transaction transaction);

    @Mappings({
            @Mapping(target = "user", source = "userId", qualifiedByName = "userFromId"),
            @Mapping(target = "admin", source = "adminId", qualifiedByName = "userFromId")
    })
    public abstract Transaction toEntity(TransactionDto transactionDto);

    public abstract List<TransactionDto> toDtoList(List<Transaction> transactionList);

    public abstract List<Transaction> toEntityList(List<TransactionDto> transactionDtoList);

    @Named("userFromId")
    protected User userFromId(Long id) {
        return userRep.findById(id).orElse(null);
    }
}
