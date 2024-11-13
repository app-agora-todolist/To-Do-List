package studio.aroundhub.todolistappproject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ModificationRequest {
    private String email;
    private String title;
    private String category;
    private LocalDate schdeule;
    private LocalDate reminderDate;
    private boolean CompletionStatus;
}
