package studio.aroundhub.todolistappproject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class DeleteScheduleRequest {
    private Long Id;
    private String email;
    //id와 eamil 기반으로 삭제한다.

}
