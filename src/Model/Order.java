package Model;

import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Order {
    private String id;
    private LocalDate date;
    private String customerId;
}
