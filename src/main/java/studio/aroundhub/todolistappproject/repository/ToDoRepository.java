package studio.aroundhub.todolistappproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import studio.aroundhub.todolistappproject.domain.ScheduleDomain;

import java.time.LocalDate;
import java.util.List;

public interface ToDoRepository extends JpaRepository<ScheduleDomain,Long> {
    ScheduleDomain findByIdAndEmail(Long id, String email);//id와 email을 기반으로 entity를 불러오기
    //해당 이메일과 월을 모두 만족하는 데이터 List 반환
    @Query("SELECT s FROM ScheduleDomain s WHERE s.email = :email AND YEAR(s.schedule) = :year")
    List<ScheduleDomain> findByEmailAndScheduleYear(@Param("email") String email, @Param("year") int year);

    @Query("SELECT s FROM ScheduleDomain s WHERE s.email = :email AND s.schedule >= :today")
    List<ScheduleDomain> findByEmailAndScheduleAfterIncludingToday(@Param("email") String email, @Param("today") LocalDate today);

}
