import static org.junit.Assert.*;
import org.junit.Before;
import java.org.junit.Test;

public class TaskManagerTest {
    private TaskManager tm;

    @Before
    public void setUp() {
        tm = new TaskManager();
    }

    @Test
    public void testAddTask() {
        Task t = new SimpleTask("Teste", "Desc");
        tm.addTask(t);
        assertEquals(1, tm.getTasks().size());
        assertEquals("Teste", tm.getTasks().get(0).getTitle());
    }

    @Test
    public void testRemoveTask() {
        Task t = new SimpleTask("Teste", "Desc");
        tm.addTask(t);
        tm.removeTask(t);
        assertEquals(0, tm.getTasks().size());
    }

    @Test
    public void testUpdateTask() {
        Task t = new SimpleTask("Teste", "Desc");
        tm.addTask(t);
        t.setDone(true);
        tm.updateTask(tm.getTasks().get(0), t);
        assertTrue(tm.getTasks().get(0).isDone());
    }
}
