package studio.aroundhub.todolistappproject.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import studio.aroundhub.todolistappproject.domain.ScheduleDomain;

import java.time.LocalDate;


@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddScheduleRequest {
    private String email;
    private   String title;
    private  String category;
    private  LocalDate Schedule;//날짜
    private   LocalDate reminderDate =null;

    public ScheduleDomain toEntity() {
        return ScheduleDomain.builder().email(email).title(title).category(category).schedule(Schedule).reminderDate(reminderDate).build();
    }
}



/*
(제목, 날짜, 카테고리, reminder(null 가능) 날짜
이 때 reaminder 날짜의 경우 설정한 입력 값을 front가 일정을 추가한 날짜에 맞춰 가공하여 요청한다.), 할 일을 수행 여부에 대한 boolean)
backend의 경우 데이터를 받아서 저장만 한다.
 */