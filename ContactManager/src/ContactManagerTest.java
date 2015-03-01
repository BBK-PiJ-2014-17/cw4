import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

public class ContactManagerTest {

    private ContactManager contactManager;
    private Set<Contact> contacts;

    @Before
    public void setUp() throws Exception {

        // general contacts set
        contacts = new HashSet<Contact>();
        contacts.add(new ContactImpl(1, "Basil Mason"));
        contacts.add(new ContactImpl(2, "Rebecca White"));

        // contact manager
        contactManager = new ContactManagerImpl();

    }

    @After
    public void tearDown() throws Exception {

    }

    // Rule for exception testing
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    // addFutureMeeting Tests * 3

    /**
     * Test addFutureMeeting method of ContactManager
     * Check unique ID returned
     */
    @Test
    public void testAddFutureMeeting() throws Exception {

        Calendar future = Calendar.getInstance();
        future.add(Calendar.DAY_OF_MONTH, +1);   // future meeting
        //FutureMeeting futureMeeting = new FutureMeetingImpl(future, contacts);
        int m1 = contactManager.addFutureMeeting(contacts, future);

        future.add(Calendar.DAY_OF_MONTH, +1);   // change meeting date
        int m2 = contactManager.addFutureMeeting(contacts, future);

        System.out.println("Meeting 1 ID: " + m1);
        System.out.println("Meeting 2 ID: " + m2);
        assertTrue(m1 != m2);

    }

    /**
     * Test addFutureMeeting method of ContactManager
     * Check IllegalArgumentException thrown if the meeting is set for a time in the past
     */
    @Test
    public void testAddFutureMeetingThrowsExceptionIfPastMeeting() {

        Calendar past = Calendar.getInstance();
        past.add(Calendar.DAY_OF_MONTH, -1);   // past meeting
        //FutureMeeting pastMeeting = new FutureMeetingImpl(past,contacts);

        thrown.expect(IllegalArgumentException.class);
        contactManager.addFutureMeeting(contacts, past);

    }

    /**
     * Test addFutureMeeting method of ContactManager
     * Check IllegalArgumentException thrown if invalid contacts
     */
    @Test
    public void testAddFutureMeetingThrowsExceptionInvalidContacts() {

        Calendar past = Calendar.getInstance();
        past.add(Calendar.DAY_OF_MONTH, -1);   // past meeting
        FutureMeeting pastMeeting = new FutureMeetingImpl(past,contacts);


    }

    // getPastMeeting

    @Test
    public void testGetPastMeeting() throws Exception {

    }

    @Test
    public void testGetFutureMeeting() throws Exception {

    }

    @Test
    public void testGetMeeting() throws Exception {

    }

    @Test
    public void testGetFutureMeetingList() throws Exception {

    }

    @Test
    public void testGetFutureMeetingList1() throws Exception {

    }

    @Test
    public void testGetPastMeetingList() throws Exception {

    }

    @Test
    public void testAddNewPastMeeting() throws Exception {

    }

    @Test
    public void testAddMeetingNotes() throws Exception {

    }

    @Test
    public void testAddNewContact() throws Exception {

    }

    @Test
    public void testGetContacts() throws Exception {

    }

    @Test
    public void testGetContacts1() throws Exception {

    }

    @Test
    public void testFlush() throws Exception {

    }
}