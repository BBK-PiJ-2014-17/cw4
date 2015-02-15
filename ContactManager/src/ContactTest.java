import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class ContactTest {

    private Contact c;

    @Before
    public void setUp() throws Exception {

        c = new ContactImpl(1, "Basil Mason");

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testGetId() throws Exception {

        assertEquals(1, c.getId());

    }

    @Test
    public void testGetName() throws Exception {

    }

    @Test
    public void testGetNotes() throws Exception {

    }

    @Test
    public void testAddNotes() throws Exception {

    }
}