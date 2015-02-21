import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class PastMeetingTest {

    private PastMeeting pm;
    String expectedNotes;

    @Before
    public void setUp() throws Exception {

        Calendar d = Calendar.getInstance();
        d.add(Calendar.DAY_OF_MONTH, -1);

        Set<Contact> c = new HashSet<Contact>();
        c.add(new ContactImpl(1, "Basil Mason"));
        c.add(new ContactImpl(2, "Rebecca White"));

        Meeting m = new MeetingImpl(1, d, c);

        expectedNotes = "Meeting notes test.";

        pm = new PastMeetingImpl(m, expectedNotes);

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testGetNotes() throws Exception {

        assertEquals(expectedNotes, pm.getNotes());

    }
}