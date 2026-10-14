package cs.vsu.radiomanagerapibysolid.service.inter;

// TODO(ревью): вся работа с деньгами пользователя. Сюда же на диплом ляжет refund(...) — возврат денег при
//   отмене размещения, и PlacementService для этого трогать не придётся.
public interface BillingService {

    void charge(Long userId, double amount, String description);

}
