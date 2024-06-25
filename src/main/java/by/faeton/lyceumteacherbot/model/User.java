package by.faeton.lyceumteacherbot.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;



@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @NotNull
    private Long telegramUserId;
    @NotNull
    private String classParallel;
    @NotNull
    private String classLetter;
    @NotNull
    private String fieldOfSheetWithUser;
    @NotNull
    private String userLastName;
    @NotNull
    private String userFirstName;
    @NotNull
    private String userFatherName;
    @NotNull
    private String sex;
    @NotNull
    private UserLevel userLevel;

}
