package studio.aroundhub.todolistappproject.domain;
import jakarta.persistence.*;
import lombok.*;
import studio.aroundhub.todolistappproject.dto.ModificationRequest;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduleDomain {
    @Column(name = "email",updatable = false,nullable = false)
    private String email;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",updatable = false,nullable = false)
    private long id;

    @Column(name ="title",nullable = false)
    private String title;

    @Column(name = "category",nullable = false)
    private String category;

    @Column(name = "schedule", nullable = false)
    private LocalDate schedule;

    @Column(name = "reminderdate",nullable = true)//reminder의 경우 꼭 설정 필요 x
    private LocalDate reminderdate;

    @Column(name = "completionstatus",nullable = false)
    boolean CompletionStatus;
    //기본적으로 false로 설정한다.

    @Builder
    public ScheduleDomain(String email, String title, String category, LocalDate schedule, LocalDate reminderDate) {
        this.email = email;
        this.title = title;
        this.category = category;
        this.schedule = schedule;
        this.reminderdate = reminderDate;
        CompletionStatus = false;
    }

    public void ModificationEntity(ModificationRequest modificationRequest) {
        this.title = modificationRequest.getTitle();
        this.category = modificationRequest.getCategory();
        this.schedule = modificationRequest.getSchedule();
        this.reminderdate = modificationRequest.getReminderDate();
        this.CompletionStatus = modificationRequest.isCompletionStatus();
    }//기존 데이터를 수정하기 위한 메서드

    //년도와 달을 기반으로 Entity List를 불러오기 위한 method
    public int getMonth() {
        return schedule.getMonthValue();
    }

    // 년도를 반환하는 메소드
    public int getYear() {
        return schedule.getYear();
    }










}