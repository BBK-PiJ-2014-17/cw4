// Unit testing libraries and methods
import org.hamcrest.Matcher;
import org.hamcrest.beans.SamePropertyValuesAs;
import org.hamcrest.collection.IsCollectionWithSize;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

// Utility libraries and methods
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ContactManagerTest {

    // set general variables for use in any test
    private ContactManager contactManager;              // the contact manager object to test
    private Contact basil, rebecca, unknown, finder;    // individual contacts to test
    private String finderString;                        // search string to find past meetings by contact
    private Set<Contact> contacts;                      // a collection of contacts for meetings
    private Calendar past, future;                      // dates for past and future meetings

    // setup the test variables and environment
    @Before
    public void setUp() throws Exception {

        // setup contacts
        basil = new ContactImpl("Basil Mason");                 // meeting contact
        rebecca = new ContactImpl("Rebecca White");             // meeting contact
        unknown = new ContactImpl("Anon");                      // unknown contact, not added to manager
        finderString = "Finder";                                // set string to search by
        finder = new ContactImpl(finderString);                 // setup contact to use in searches
        contacts = new HashSet<Contact>();                      // collection of contacts for meetings
        contacts.add(basil);                                    // add contact
        contacts.add(rebecca);                                  // add contact

        // setup dates
        past = Calendar.getInstance();
        past.add(Calendar.DAY_OF_MONTH, -1);                    // date for past meeting
        future = Calendar.getInstance();
        future.add(Calendar.DAY_OF_MONTH, +1);                  // date for future meeting

        // setup contact manager
        contactManager = new ContactManagerImpl();
        contactManager.addNewContact(basil.getName(), "");      // add contact to contact manager
        contactManager.addNewContact(rebecca.getName(), "");    // add contact to contact manager
        contactManager.addNewContact(finder.getName(), "");     // add contact to contact manager

    }

    // Rule for exception testing
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * addFutureMeetings Tests
     * Required tests:
     *      - add meeting and return id
     *      - add multiple meetings and check uniqueness of ids
     *      - check for IllegalArgumentException if meeting set in the past
     *      - check for IllegalArgumentException if contact unknown to the contact manager
     */
    @Test
    public void testAddFutureMeeting() throws Exception {

        int m1 = contactManager.addFutureMeeting(contacts, future); // add first meeting
        future.add(Calendar.DAY_OF_MONTH, +1);                      // move meeting date
        int m2 = contactManager.addFutureMeeting(contacts, future); // add second meeting

        assertTrue(m1 != m2);   // check meeting ids are unique

    }

    @Test
    public void testAddFutureMeetingThrowsExceptionIfPastMeeting() {

        thrown.expect(IllegalArgumentException.class);      // expect invalid argument exception
        contactManager.addFutureMeeting(contacts, past);    // due to meeting in past

    }

    @Test
    public void testAddFutureMeetingThrowsExceptionInvalidContacts() {

        contacts.add(unknown);  // add contact unknown to contact manager to set of contacts for meeting

        thrown.expect(IllegalArgumentException.class);      // expect invalid argument exception
        contactManager.addFutureMeeting(contacts, past);    // due to unknown contact

    }

    /**
     * getPastMeeting Tests
     * Required tests:
     *      - get past meeting by id
     *      - check PastMeeting returned with expected id
     *      - check null is returned if no meeting present with that id
     *      - check for IllegalArgumentException if meeting set in the future
     */
    @Test
    public void testGetPastMeeting() throws Exception {

        PastMeeting pm;

        // method 1, setup future meeting and wait until it is a past meeting
        // then search by id
        int pastMeetingId = setupPastMeeting();             // setup meeting in past
        pm = contactManager.getPastMeeting(pastMeetingId);  // get past meeting by id
        assertEquals(pastMeetingId, pm.getId());            // test IDs the same

        // method 2, setup new past meeting directly, with unique contact to search by
        String pastMeetingNotes = "blah blah";                                  // meeting notes to check on return
        contacts.add(finder);                                                   // add finder contact to contacts for meeting
        contactManager.addNewPastMeeting(contacts, past, pastMeetingNotes);     // add new past meeting directly
        pm = contactManager.getPastMeetingList(finder).get(0);                  // return the past meeting based on finder contact
        assertTrue(pastMeetingNotes.equals(pm.getNotes()));                     // test meeting notes match

        // test null returned if meeting does not exist
        assertTrue(contactManager.getPastMeeting(99999) == null);

    }

    @Test
    public void testGetPastMeetingThrowsExceptionIfFutureMeeting() {

        int futureMeetingId = contactManager.addFutureMeeting(contacts, future);    // add future meeting and get ID

        thrown.expect(IllegalArgumentException.class);      // expect invalid argument exception
        contactManager.getPastMeeting(futureMeetingId);     // due to meeting in future

    }

    /**
     * getFutureMeeting Tests
     * Required tests:
     *      - get future meeting by id
     *      - check FutureMeeting returned with expected id
     *      - check null is returned if no meeting present with that id
     *      - check for IllegalArgumentException if meeting set in the past
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

    @Test
    public void testGetFutureMeetingThrowsExceptionIfPastMeeting() {

        int futureMeetingId;

        // method 1, setup future meeting and wait until in past
        futureMeetingId = setupPastMeeting();               // setup past meeting
        thrown.expect(IllegalArgumentException.class);      // expect invalid argument exception
        contactManager.getFutureMeeting(futureMeetingId);   // due to meeting in past

        // method 2, setup new past meeting directly, with unique contact to search by
        contacts.add(finder);                                                   // add finder contact to contacts for meeting
        contactManager.addNewPastMeeting(contacts, past, "");                   // add new past meeting directly
        PastMeeting pm = contactManager.getPastMeetingList(finder).get(0);      // return the past meeting based on finder contact
        futureMeetingId = pm.getId();                                           // get id of past meeting
        thrown.expect(IllegalArgumentException.class);                          // expect invalid argument exception
        contactManager.getFutureMeeting(futureMeetingId);                       // due to meeting in past

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

    // getFutureMeetingList

    /**
     * Test getFutureMeetingList method of ContactManager given contact
     * Check list is empty if no meetings present with that contact
     * Check list contains all meetings expected with given contact
     * Add past meetings to check?
     * Check for chronology?
     */
    @Test
    public void testGetFutureMeetingListByContact() throws Exception {

        Contact unknown = new ContactImpl("Anon");  // contact without any meetings

        // expect empty list
        assertTrue(contactManager.getFutureMeetingList(unknown).size() == 0);

        // add meetings with basil
        int meeting1Id = contactManager.addFutureMeeting(contacts, future);
        future.add(Calendar.DAY_OF_MONTH, +1);
        int meeting2Id = contactManager.addFutureMeeting(contacts, future);
        future.add(Calendar.DAY_OF_MONTH, +1);
        int meeting3Id = contactManager.addFutureMeeting(contacts, future);

        // add meeting without basil
        future.add(Calendar.DAY_OF_MONTH, +1);
        Set<Contact> otherContacts = new HashSet<Contact>();
        otherContacts.add(unknown);
        int meeting4Id = contactManager.addFutureMeeting(otherContacts, future);

        // get meetings
        List<Meeting> meetings = contactManager.getFutureMeetingList(basil);

        // check list contains expected meetings
        Meeting meeting1 = contactManager.getMeeting(meeting1Id);
        Meeting meeting2 = contactManager.getMeeting(meeting2Id);
        Meeting meeting3 = contactManager.getMeeting(meeting3Id);

        Set<Meeting> expectedMeetings = new HashSet<Meeting>();
        expectedMeetings.add(meeting1);
        expectedMeetings.add(meeting2);
        expectedMeetings.add(meeting3);

        Set<Matcher<Meeting>> matcher = new HashSet<Matcher<Meeting>>();

        // create a matcher that checks for the property values of each Meeting
        for(Meeting m: expectedMeetings)
            matcher.add(new SamePropertyValuesAs(m));

        // check that each matcher matches something in the list
        for (Matcher<Meeting> mf : matcher)
            assertThat(meetings, hasItem(mf));

        // check that list sizes match
        assertThat(meetings, IsCollectionWithSize.hasSize(expectedMeetings.size()));

        // check list does not contain meeting without basil
        Meeting meeting4 = contactManager.getMeeting(meeting4Id);

        assertThat(meetings,not(hasItem(meeting4)));

    }

    /**
     * Test getFutureMeetingList method of ContactManager given contact
     * Check IllegalArgumentException thrown if invalid contacts
     */
    @Test
    public void testGetFutureMeetingListByContactThrowsExceptionInvalidContacts() {

        // create contact unknown to contactManager
        Contact unknown = new ContactImpl("Anon");

        // expect invalid argument due to unknown contact
        thrown.expect(IllegalArgumentException.class);
        contactManager.getFutureMeetingList(unknown);

    }

    /**
     * Test getFutureMeetingList method of ContactManager given date
     * Check list is empty if no meetings present with that date
     * Check list contains all meetings expected with given date
     * Check if contains past meeting
     * Check for chronology?
     */
    @Test
    public void testGetFutureMeetingListByDate() throws Exception {

        // expect empty list
        assertTrue(contactManager.getFutureMeetingList(future).size() == 0);

        // add meetings with future date
        Set<Contact> otherContacts1 = new HashSet<Contact>();
        otherContacts1.add(new ContactImpl("Contact One"));
        otherContacts1.add(new ContactImpl("Contact Two"));
        int meeting1Id = contactManager.addFutureMeeting(otherContacts1, future);
        Set<Contact> otherContacts2 = new HashSet<Contact>();
        otherContacts2.add(new ContactImpl("Contact Three"));
        otherContacts2.add(new ContactImpl("Contact Four"));
        otherContacts2.add(new ContactImpl("Contact Five"));
        int meeting2Id = contactManager.addFutureMeeting(otherContacts2, future);

        // add meeting on different date
        Calendar otherDate = Calendar.getInstance();
        otherDate.add(Calendar.DAY_OF_MONTH, +100);
        int meeting3Id = contactManager.addFutureMeeting(otherContacts2, otherDate);

        // get meetings
        List<Meeting> meetings = contactManager.getFutureMeetingList(future);

        // check list contains expected meetings
        Meeting meeting1 = contactManager.getMeeting(meeting1Id);
        Meeting meeting2 = contactManager.getMeeting(meeting2Id);

        Set<Meeting> expectedMeetings = new HashSet<Meeting>();
        expectedMeetings.add(meeting1);
        expectedMeetings.add(meeting2);

        Set<Matcher<Meeting>> matcher = new HashSet<Matcher<Meeting>>();

        // create a matcher that checks for the property values of each Meeting
        for(Meeting m: expectedMeetings)
            matcher.add(new SamePropertyValuesAs(m));

        // check that each matcher matches something in the list
        for (Matcher<Meeting> mf : matcher)
            assertThat(meetings, hasItem(mf));

        // check that list sizes match
        assertThat(meetings, IsCollectionWithSize.hasSize(expectedMeetings.size()));

        // check list does not contain meeting on different date
        Meeting meeting3 = contactManager.getMeeting(meeting3Id);

        assertThat(meetings,not(hasItem(meeting3)));

    }

    // getPastMeetingList

    /**
     * Test getPastMeetingList method of ContactManager given contact
     * Check list is empty if no meetings present with that contact
     * Check list contains all meetings expected with given contact
     * Check it contains only past meetings
     * Check for chronology
     */
    @Test
    public void testGetPastMeetingList() throws Exception {

        boolean wait = true;

        Contact unknown = new ContactImpl("Anon");  // contact without any meetings

        // expect empty list
        assertTrue(contactManager.getPastMeetingList(unknown).size() == 0);

        // add meetings with basil]
        Calendar soon = Calendar.getInstance();
        soon.add(Calendar.SECOND, +10);
        int meeting1Id = contactManager.addFutureMeeting(contacts, soon);
        soon.add(Calendar.SECOND, +10);
        int meeting2Id = contactManager.addFutureMeeting(contacts, soon);
        soon.add(Calendar.SECOND, +10);
        int meeting3Id = contactManager.addFutureMeeting(contacts, soon);

        // add meeting without basil
        soon.add(Calendar.SECOND, +10);
        Set<Contact> otherContacts = new HashSet<Contact>();
        otherContacts.add(unknown);
        int meeting4Id = contactManager.addFutureMeeting(otherContacts, soon);

        // wait until meetings are in past

        while(wait) {   // wait until meeting is in the past and check again

            Calendar now = Calendar.getInstance();

            if (now.compareTo(soon) > 0)    // if time now is greater than time when meeting occurred
                wait = false;

        }

        // get meetings
        List<PastMeeting> meetings = contactManager.getPastMeetingList(basil);

        // check list contains expected meetings
        PastMeeting meeting1 = (PastMeeting) contactManager.getMeeting(meeting1Id);
        PastMeeting meeting2 = (PastMeeting) contactManager.getMeeting(meeting2Id);
        PastMeeting meeting3 = (PastMeeting) contactManager.getMeeting(meeting3Id);

        Set<PastMeeting> expectedMeetings = new HashSet<PastMeeting>();
        expectedMeetings.add(meeting1);
        expectedMeetings.add(meeting2);
        expectedMeetings.add(meeting3);

        Set<Matcher<PastMeeting>> matcher = new HashSet<Matcher<PastMeeting>>();

        // create a matcher that checks for the property values of each Meeting
        for(PastMeeting m: expectedMeetings)
            matcher.add(new SamePropertyValuesAs(m));

        // check that each matcher matches something in the list
        for (Matcher<PastMeeting> mf : matcher)
            assertThat(meetings, hasItem(mf));

        // check that list sizes match
        assertThat(meetings, IsCollectionWithSize.hasSize(expectedMeetings.size()));

        // check list does not contain meeting without basil
        PastMeeting meeting4 = (PastMeeting) contactManager.getMeeting(meeting4Id);

        assertThat(meetings,not(hasItem(meeting4)));

    }

    @Test
    public void testAddNewPastMeeting() throws Exception {

    }

    // addMeetingNotes

    /**
     * Test addMeetingNotes adds correct details to correct meeting
     *
     */
    @Test
    public void testAddMeetingNotes() throws Exception {

        boolean wait = true;
        Calendar soon = Calendar.getInstance();
        soon.add(Calendar.SECOND, +10);

        // add meeting in future
        int meetingId = contactManager.addFutureMeeting(contacts, soon);
        String notes = "blah blah.";

        // add meeting notes
        contactManager.addMeetingNotes(meetingId, notes);

        PastMeeting pm  = (PastMeeting) contactManager.getMeeting(meetingId); // cast meeting to PastMeeting to access notes
        assertTrue(pm.getNotes().equals(notes));

        // wait until meetings are in past

        while(wait) {   // wait until meeting is in the past and check again

            Calendar now = Calendar.getInstance();

            if (now.compareTo(soon) > 0)    // if time now is greater than time when meeting occurred
                wait = false;

        }

        pm  = contactManager.getPastMeeting(meetingId);
        assertTrue(pm.getNotes().equals(notes));

    }

    @Test
    public void testAddMeetingNotesThrowsIllegalArgumentException() {

        int meetingId = 12345678;
        String notes = "blah blah.";

        thrown.expect(IllegalArgumentException.class);
        contactManager.addMeetingNotes(meetingId, notes);

    }

    @Test
    public void testAddMeetingNotesThrowsIllegalStateException() {

        // add meeting in future
        int meetingId = contactManager.addFutureMeeting(contacts, future);
        String notes = "blah blah.";

        thrown.expect(IllegalStateException.class);
        contactManager.addMeetingNotes(meetingId, notes);

    }

    @Test
    public void testAddMeetingNotesThrowsNullPointerException() {

        Contact finder = new ContactImpl("Finder");  // contact to find past meeting by...

        // add past meeting
        contactManager.addNewPastMeeting(contacts, past, "blah blah");

        // get past meeting ID
        List<PastMeeting> pms = contactManager.getPastMeetingList(finder);
        PastMeeting pm = pms.get(0);

        String newNotes = null;

        thrown.expect(NullPointerException.class);
        contactManager.addMeetingNotes(pm.getId(), newNotes);

    }

    // addNewContact

    @Test
    public void testAddNewContact() throws Exception {

        String newContactName = "Mr New Contact";
        String newContactNotes = "Mr New Contact's notes";

        // add contact
        contactManager.addNewContact(newContactName, newContactNotes);

        // retrieve all contacts based on name
        Set<Contact> cs = contactManager.getContacts(newContactName);

        // for each contact, check notes. Only one contact should be present
        for (Contact c : cs) {
            assertTrue(c.getNotes().equals(newContactNotes));   // change to lambda?
        }

    }

    @Test
    public void testAddNewContactThrowsNullPointerException() {

        thrown.expect(NullPointerException.class);
        contactManager.addNewContact(null, "Not null");
        contactManager.addNewContact("Not null", null);

    }

    // getContacts

    @Test
    public void testGetContactsById() throws Exception {

        Set<Contact> cs = contactManager.getContacts(basil.getId(), rebecca.getId());

        for (Contact c : cs) {

            assertTrue(c.getName().equals(basil.getName()) || c.getName().equals(rebecca.getName()));

        }

    }

    @Test
    public void testGetContactsByIdThrowsIllegalArgumentException() throws Exception {

        thrown.expect(IllegalArgumentException.class);
        contactManager.getContacts(12345678);


    }

    @Test
    public void testGetContactsByName() throws Exception {

        String contactName = "New Contact";

        Contact contact1 = new ContactImpl(contactName);
        Contact contact2 = new ContactImpl(contactName);
        Contact contact3 = new ContactImpl(contactName);

        int[] contactIds = {contact1.getId(), contact2.getId(), contact3.getId()};
        Set<Contact> cs = contactManager.getContacts(contactName);

        int count = 0;

        for (Contact c : cs) {

            for (int id : contactIds) {

                if (c.getId() == id)
                    count++;

            }

        }

        assertTrue(count == 3);

    }

    @Test
    public void testGetContactsByNameThrowsNullPointerException() throws Exception {

        String sNull = null;

        thrown.expect(NullPointerException.class);
        contactManager.getContacts(sNull);

    }

    @Test
    public void testFlush() throws Exception {

    }

    private int setupPastMeeting() {

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

        return pastMeetingId;

    }
}