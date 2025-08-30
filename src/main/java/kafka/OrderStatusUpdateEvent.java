package kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusUpdateEvent {
    private String event;
    private UUID orderId;
    private Long customerId;
    private String oldStatus;
    private String newStatus;
    private Instant timestamp;
}
