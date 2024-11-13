package studio.aroundhub.todolistappproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import studio.aroundhub.todolistappproject.domain.ScheduleDomain;

import java.util.List;

public interface ToDoRepository extends JpaRepository<ScheduleDomain,Long> {
    ScheduleDomain findByIdAndEmail(Long id, String email);//id와 email을 기반으로 entity를 불러오기
    @Query("SELECT s FROM ScheduleDomain s WHERE s.email = :email AND MONTH(s.schedule) = :month AND YEAR(s.schedule) = :year")
    List<ScheduleDomain> findByEmailAndScheduleMonthAndScheduleYear(@Param("email") String email,
                                                                    @Param("month") int month,
                                                                    @Param("year") int year);
}
