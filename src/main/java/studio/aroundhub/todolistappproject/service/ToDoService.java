package studio.aroundhub.todolistappproject.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import studio.aroundhub.todolistappproject.domain.ScheduleDomain;
import studio.aroundhub.todolistappproject.dto.AddScheduleRequest;
import studio.aroundhub.todolistappproject.dto.ModificationRequest;
import studio.aroundhub.todolistappproject.repository.ToDoRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ToDoService {
    private  final ToDoRepository repository;

    public ScheduleDomain save(AddScheduleRequest addScheduleRequest) { //새로운 일정 추가
        return repository.save(addScheduleRequest.toEntity());
    }


    public boolean delete(Long id, String email) { //id와 email 기반으로 해당 데이터 삭제하기
    ScheduleDomain schedule = repository.findByIdAndEmail(id, email);
    if(schedule != null) {
        repository.delete(schedule);
        return true;
    }
    return false;
    }

    public ScheduleDomain Modification(Long id, ModificationRequest modificationRequest)
    {
        ScheduleDomain schedule = repository.findByIdAndEmail(id, modificationRequest.getEmail());
        if(schedule != null) {
            schedule.ModificationEntity(modificationRequest);
            repository.save(schedule);
        }
        return schedule; //null인 경우 해당 데이터를 못 찾은 경우이다.
    }

   public List<ScheduleDomain> YearAndMonthGetAll(int year, int month,String email) {
        //이메일을 기반으로 해당 년 월에 해당하는 데이터를 리스트 형태로 준다.
        //이 때 도메인 형태로 준 이유는 대부분의 데이터가 다 필요하고 id까지 주는 이유는 나중에 쿼리 요청 때 필요하기 때문이다.
    return   repository.findByEmailAndScheduleMonthAndScheduleYear(email, month, year);
   }

}
