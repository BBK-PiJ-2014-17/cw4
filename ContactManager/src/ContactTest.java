import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class ContactTest {

    private Contact c;
    private int expectedId;
    private String expectedName, expectedNotes;

    @Before
    public void setUp() throws Exception {

        expectedId = 1;
        expectedName = "Basil Mason";
        expectedNotes = "This is a note.";

        c = new ContactImpl(expectedId, expectedName);

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testGetId() throws Exception {

        assertEquals(expectedId, c.getId());

    }

    @Test
    public void testGetName() throws Exception {

        assertEquals(expectedName, c.getId());

    }

    @Test
    public void testGetNotes() throws Exception {

        c.addNotes(expectedNotes);
        assertEquals(expectedNotes, c.getNotes());

    }

    @Test
    public void testAddNotes() throws Exception {

    }
}