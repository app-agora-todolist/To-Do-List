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
    @PostMapping("/schedule")
    public ResponseEntity<ScheduleDomain> newSchedule(@RequestBody AddScheduleRequest addScheduleRequest ) {
        ScheduleDomain scheduleDomain = toDoService.save(addScheduleRequest);
        if(scheduleDomain == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(scheduleDomain);
    }

    @Operation(summary = "일정 수정", description = "요청한 일정을 Id와 email 기반으로 수정한다.")
    @PutMapping("/schedule/{id}")
    public ResponseEntity<ScheduleDomain> ModificationSchedule(@PathVariable Long id, @RequestBody ModificationRequest modificationRequest) {
        ScheduleDomain scheduleDomain = toDoService.Modification(id,modificationRequest);
        if(scheduleDomain == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(scheduleDomain);//null이 아닌 경우 OK를 return
    }



    @Operation(summary = "일정 삭제", description = "요청한 일정을 Id와 email 기반으로 삭제한다.")
    @DeleteMapping("/schedule/{id}")
    public ResponseEntity<Boolean> deleteSchedule(@PathVariable Long id, @RequestParam String email) {
        boolean deleted = toDoService.delete(id, email);
        if (deleted) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(true);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
    }


    @Operation(summary = "일정 요청",description = "요청한 사용자의 해당 연도 데이터를 모두 불러온다.")
    @GetMapping("/schedule")
     public ResponseEntity<List<ScheduleDomain>> getMonthList(@RequestParam String email, @RequestParam  int year) {
        //년도와 달을 기반으로 데이터를 가져온다.

            List<ScheduleDomain> list = toDoService.YearGetAll(year,email);
            if(list == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.status(HttpStatus.OK).body(list);
    }
    @Operation(summary = "카테고리 데이터 요청", description = "오늘을 포함한 사용자의 모든 데이터를 전송한다..")
    @GetMapping("/schedule/category")
    public ResponseEntity<List<ScheduleDomain>> getCategoryList(@RequestParam String email) {
        List<ScheduleDomain> list = toDoService.getCategory(email);
        if(list == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }


}