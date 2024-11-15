package studio.aroundhub.todolistappproject.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import studio.aroundhub.todolistappproject.domain.ScheduleDomain;
import studio.aroundhub.todolistappproject.dto.AddScheduleRequest;
import studio.aroundhub.todolistappproject.dto.ModificationRequest;
import studio.aroundhub.todolistappproject.repository.ToDoRepository;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ToDoService {
    private  final ToDoRepository repository;

    public ScheduleDomain save(AddScheduleRequest addScheduleRequest) { //새로운 일정 추가
        return repository.save(addScheduleRequest.toEntity());
    }


    @Transactional
    public boolean delete(Long id, String email) {
        ScheduleDomain schedule = repository.findByIdAndEmail(id, email);
        if (schedule != null) {
            repository.delete(schedule);
            return true;
        } else {
            return false;
        }
    }
    public ScheduleDomain Modification(Long id, ModificationRequest modificationRequest)
    {
        ScheduleDomain schedule = repository.findByIdAndEmail(id, modificationRequest.getEmail());
        if(schedule != null) {
            schedule.ModificationEntity(modificationRequest);
            schedule = repository.save(schedule);
        }
        return schedule; //null인 경우 해당 데이터를 못 찾은 경우이다.
    }

   public List<ScheduleDomain> YearGetAll(int year, String email) {
        return   repository.findByEmailAndScheduleYear(email,year);
   }

   public List<ScheduleDomain> getCategory(String email)
   {
       LocalDate today = LocalDate.now();
       return repository.findByEmailAndScheduleAfterIncludingToday(email, today);
   }


}
