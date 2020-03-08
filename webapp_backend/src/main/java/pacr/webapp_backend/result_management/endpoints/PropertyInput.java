package pacr.webapp_backend.result_management.endpoints;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Transfer object for name of a property from the frontend
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PropertyInput {
    private String name;
}
