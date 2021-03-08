package pjarosz;

import lombok.*;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Calendar {

    private Hour working_hours;
    private List<Hour> planned_meeting;
}
