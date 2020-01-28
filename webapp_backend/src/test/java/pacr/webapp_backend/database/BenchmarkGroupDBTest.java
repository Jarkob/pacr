package pacr.webapp_backend.database;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pacr.webapp_backend.SpringBootTestWithoutShell;
import pacr.webapp_backend.result_management.BenchmarkGroup;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class BenchmarkGroupDBTest extends SpringBootTestWithoutShell {

    private static final String GROUP_NAME = "group";
    private static final String GROUP_NAME_TWO = "group2";
    private static final int EXPECTED_NUM_OF_GROUPS = 2;

    private BenchmarkGroupDB groupDB;

    @Autowired
    public BenchmarkGroupDBTest(BenchmarkGroupDB groupDB, BenchmarkDB benchmarkDB) {
        this.groupDB = groupDB;
    }

    @AfterEach
    public void setUp() {
        groupDB.deleteAll();
    }

    /**
     * Tests whether a group can be saved with saveBenchmarkGroup and retrieved with getBenchmarkGroup
     */
    @Test
    public void saveGroup_saveInDatabase_getGroupShouldReturnGroup() {
        BenchmarkGroup group = new BenchmarkGroup(GROUP_NAME);
        groupDB.saveBenchmarkGroup(group);
        int id = group.getId();

        BenchmarkGroup savedGroup = groupDB.getBenchmarkGroup(id);

        assertEquals(GROUP_NAME, savedGroup.getName());
    }

    /**
     * Tests whether all groups are returned by getAllGroups.
     */
    @Test
    public void getAllGroups_multipleGroupsSaved_shouldReturnAllGroups() {
        BenchmarkGroup group = new BenchmarkGroup(GROUP_NAME);
        groupDB.saveBenchmarkGroup(group);
        BenchmarkGroup groupTwo = new BenchmarkGroup(GROUP_NAME_TWO);
        groupDB.saveBenchmarkGroup(groupTwo);

        Collection<BenchmarkGroup> groups = groupDB.getAllGroups();

        assertEquals(EXPECTED_NUM_OF_GROUPS, groups.size());
    }

    /**
     * Tests whether a group can be properly deleted.
     */
    @Test
    public void  deleteGroup_groupSaved_shouldRemoveGroup() {
        BenchmarkGroup group = new BenchmarkGroup(GROUP_NAME);
        groupDB.saveBenchmarkGroup(group);
        int id = group.getId();

        groupDB.deleteGroup(group);

        BenchmarkGroup deletedGroup = groupDB.getBenchmarkGroup(id);

        assertNull(deletedGroup);
    }
}
