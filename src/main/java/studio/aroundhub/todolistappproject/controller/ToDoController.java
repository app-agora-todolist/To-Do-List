package studio.aroundhub.todolistappproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import studio.aroundhub.todolistappproject.domain.ScheduleDomain;
import studio.aroundhub.todolistappproject.dto.AddScheduleRequest;
import studio.aroundhub.todolistappproject.dto.ModificationRequest;
import studio.aroundhub.todolistappproject.service.ToDoService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ToDoController {

    private final ToDoService toDoService;
    @Operation(summary = "새로운 일정 추가", description = "DB에 일정을 추가하고 추가한 일정을 반환")
    @PostMapping("/Schedule")
    public ResponseEntity<ScheduleDomain> newSchedule(@RequestBody AddScheduleRequest addScheduleRequest ) {
        ScheduleDomain scheduleDomain = toDoService.save(addScheduleRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(scheduleDomain);
    }

    @Operation(summary = "일정 삭제", description = "요청한 일정을 Id와 email 기반으로 삭제한다.")
    @DeleteMapping("/Schedule/{id}")
    public boolean DeleteSchedule(@PathVariable Long id,@RequestBody String email) {
        return toDoService.delete(id,email);
    }

    @Operation(summary = "일정 수정", description = "요청한 일정을 Id와 email 기반으로 수정한다. ")
    @PutMapping("/Schedule/{id}")
    public ResponseEntity<ScheduleDomain> ModificationSchedule(@PathVariable Long id, @RequestBody ModificationRequest modificationRequest) {
    ScheduleDomain scheduleDomain = toDoService.Modification(id,modificationRequest);
    if(scheduleDomain == null) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    return ResponseEntity.status(HttpStatus.OK).body(scheduleDomain);//null이 아닌 경우 OK를 return
    }

    @Operation(summary = "일정 요청",description = "요청한 달을 기준으로 한달치 데이터를 가져온다. 1월 13일이면 해당 년도의 1월 데이터 전부를 가져온다.")
    @GetMapping("/Schedule")
     public ResponseEntity<List<ScheduleDomain>> getMonthList(@RequestParam String email, @RequestParam int year,@RequestParam int month) {
        //년도와 달을 기반으로 데이터를 가져온다.
        if(month >=1 && month <= 12)
        {
            List<ScheduleDomain> list = toDoService.YearAndMonthGetAll(year,month,email);
            return ResponseEntity.status(HttpStatus.OK).body(list);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }




}