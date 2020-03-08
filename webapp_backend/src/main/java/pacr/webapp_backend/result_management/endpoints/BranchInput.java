package pacr.webapp_backend.result_management.endpoints;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Transfer object for name of a branch from the frontend
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BranchInput {
    private String name;
}
