import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

public class ContactManagerTest {

    private ContactManager contactManager;
    private Set<Contact> contacts;
    private Calendar past, future;

    @Before
    public void setUp() throws Exception {

        // general contacts set

        Contact basil = new ContactImpl("Basil Mason");
        Contact rebecca = new ContactImpl("Rebecca White");

        contacts = new HashSet<Contact>();
        contacts.add(basil);
        contacts.add(rebecca);

        // dates for testing

        past = Calendar.getInstance();
        past.add(Calendar.DAY_OF_MONTH, -1);   // past meeting

        future = Calendar.getInstance();
        future.add(Calendar.DAY_OF_MONTH, +1);   // future meeting

        // contact manager
        contactManager = new ContactManagerImpl();

        // add contacts to contact manager for testing

        contactManager.addNewContact(basil.getName(), "");
        contactManager.addNewContact(rebecca.getName(), "");

    }

    @After
    public void tearDown() throws Exception {

    }

    // Rule for exception testing
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    // addFutureMeeting Tests

    /**
     * Test addFutureMeeting method of ContactManager
     * Check unique ID returned
     */
    @Test
    public void testAddFutureMeeting() throws Exception {

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

        // expect invalid argument exception due to meeting in past
        thrown.expect(IllegalArgumentException.class);
        contactManager.addFutureMeeting(contacts, past);

    }

    /**
     * Test addFutureMeeting method of ContactManager
     * Check IllegalArgumentException thrown if invalid contacts
     */
    @Test
    public void testAddFutureMeetingThrowsExceptionInvalidContacts() {

        // create contact unknown to contactManager
        Contact unknown = new ContactImpl("Anon");
        contacts.add(unknown);

        // expect invalid argument due to unknown contact
        thrown.expect(IllegalArgumentException.class);
        contactManager.addFutureMeeting(contacts, past);

    }

    // getPastMeeting

    /**
     * Test getPastMeeting method of ContactManager
     * Check PastMeeting returned with expected ID
     * Check null returned if no meeting found
     */
    @Test
    public void testGetPastMeeting() throws Exception {

        boolean wait = true;

        // create meeting 10 seconds in future and get ID
        Calendar soon = Calendar.getInstance();
        soon.add(Calendar.SECOND, +10);   // increase 10 seconds
        int pastMeetingId = contactManager.addFutureMeeting(contacts, soon);

        while(wait) {   // wait until meeting is in the past

            Calendar now = Calendar.getInstance();

            if (now.compareTo(soon) > 0)    // if time now is greater than time when meeting occurred
                wait = false;

        }

        PastMeeting pm = contactManager.getPastMeeting(pastMeetingId);

        // test IDs the same
        assertEquals(pastMeetingId, pm.getId());

        // test null returned if meeting does not exist
        assertTrue(contactManager.getPastMeeting(99999) == null);

    }

    /**
     * Test getPastMeeting method of ContactManager
     * Check IllegalArgumentException thrown if there is a meeting with that ID happening in the future
     */
    @Test
    public void testGetPastMeetingThrowsExceptionIfFutureMeeting() {

        // add future meeting and get ID
        int futureMeetingId = contactManager.addFutureMeeting(contacts, future);

        // expect invalid argument exception due to meeting in future
        thrown.expect(IllegalArgumentException.class);
        contactManager.getPastMeeting(futureMeetingId);

    }

    // getFutureMeeting

    /**
     * Test getFutureMeeting method of ContactManager
     * Check FutureMeeting returned with expected ID
     * Check null returned if no meeting found
     */
    @Test
    public void testGetFutureMeeting() throws Exception {

        // add future meeting and get ID
        int futureMeetingId = contactManager.addFutureMeeting(contacts, future);

        // test IDs the same
        assertEquals(futureMeetingId, contactManager.getFutureMeeting(futureMeetingId).getId());

        // test null returned if meeting does not exist
        assertTrue(contactManager.getFutureMeeting(99999) == null);

    }

    /**
     * Test getFutureMeeting method of ContactManager
     * Check IllegalArgumentException thrown if there is a meeting with that ID happening in the past
     */
    @Test
    public void testGetFutureMeetingThrowsExceptionIfPastMeeting() {

        boolean wait = true;

        // create meeting 10 seconds in future and get ID
        Calendar soon = Calendar.getInstance();
        soon.add(Calendar.SECOND, +10);   // increase 10 seconds
        int futureMeetingId = contactManager.addFutureMeeting(contacts, soon);

        while(wait) {   // wait until meeting is in the past

            Calendar now = Calendar.getInstance();

            if (now.compareTo(soon) > 0)    // if time now is greater than time when meeting occurred
                wait = false;

        }

        // expect invalid argument exception due to meeting in past
        thrown.expect(IllegalArgumentException.class);
        contactManager.getFutureMeeting(futureMeetingId);

    }

    // getMeeting

    /**
     * Test getMeeting method of ContactManager
     * Check Meeting returned with expected ID
     * Check null returned if no meeting found
     */
    @Test
    public void testGetMeeting() throws Exception {

        boolean wait = true;

        // create meeting 10 seconds in future and get ID
        Calendar soon = Calendar.getInstance();
        soon.add(Calendar.SECOND, +10);   // increase 10 seconds

        // add meeting and get ID
        int meetingId = contactManager.addFutureMeeting(contacts, soon);

        // test IDs the same for meeting whilst a future meeting
        assertEquals(meetingId, contactManager.getMeeting(meetingId).getId());

        while(wait) {   // wait until meeting is in the past and check again

            Calendar now = Calendar.getInstance();

            if (now.compareTo(soon) > 0)    // if time now is greater than time when meeting occurred
                wait = false;

        }

        // test IDs the same for meeting whilst a past meeting
        assertEquals(meetingId, contactManager.getMeeting(meetingId).getId());

        // test null returned if meeting does not exist
        assertTrue(contactManager.getMeeting(99999) == null);

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