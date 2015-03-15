// Unit testing libraries and methods
import org.hamcrest.Matcher;
import org.hamcrest.beans.SamePropertyValuesAs;
import org.hamcrest.collection.IsCollectionWithSize;
import org.junit.*;
import org.junit.rules.ExpectedException;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

// Utility libraries and methods
import java.util.*;

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

    @After
    public void tearDown() throws Exception {

        contactManager.flush();

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
    @Ignore
    public void testAddFutureMeeting() throws Exception {

        int m1 = contactManager.addFutureMeeting(contacts, future); // add first meeting
        future.add(Calendar.DAY_OF_MONTH, +1);                      // move meeting date
        int m2 = contactManager.addFutureMeeting(contacts, future); // add second meeting

        assertTrue(m1 != m2);   // check meeting ids are unique

    }

    @Ignore
    public void testAddFutureMeetingThrowsIllegalArgumentException() {

        thrown.expect(IllegalArgumentException.class);      // expect invalid argument exception
        contactManager.addFutureMeeting(contacts, past);    // due to meeting in past

        contacts.add(unknown);  // add contact unknown to contact manager to set of contacts for meeting
        thrown.expect(IllegalArgumentException.class);      // expect invalid argument exception
        contactManager.addFutureMeeting(contacts, future);    // due to unknown contact

    }

    /**
     * getPastMeeting Tests
     * Required tests:
     *      - get past meeting by id
     *      - check PastMeeting returned with expected id
     *      - check null is returned if no meeting present with that id
     *      - check for IllegalArgumentException if meeting set in the future
     */
    @Ignore
    public void testGetPastMeeting() throws Exception {

        PastMeeting pm;

        // method 1, setup future meeting and wait until it is a past meeting
        // then search by id
        int pastMeetingId = setupPastMeeting(contacts);     // setup meeting in past
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

    @Ignore
    public void testGetPastMeetingThrowsIllegalArgumentException() {

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
    @Ignore
    public void testGetFutureMeeting() throws Exception {

        // add future meeting and get ID
        int futureMeetingId = contactManager.addFutureMeeting(contacts, future);

        // test IDs the same
        assertEquals(futureMeetingId, contactManager.getFutureMeeting(futureMeetingId).getId());

        // test null returned if meeting does not exist
        assertTrue(contactManager.getFutureMeeting(99999) == null);

    }

    @Ignore
    public void testGetFutureMeetingThrowsIllegalArgumentException() {

        int futureMeetingId;

        // method 1, setup future meeting and wait until in past
        futureMeetingId = setupPastMeeting(contacts);       // setup past meeting
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

    /**
     * getMeeting Tests
     * Required tests:
     *      - get meeting by id
     *      - check meeting returned with expected id
     *      - check null is returned if no meeting present with that id
     *      - check it works for past and future meetings
     */
    @Ignore
    public void testGetMeeting() throws Exception {

        int meetingId;

        meetingId = contactManager.addFutureMeeting(contacts, future);          // future meeting
        assertEquals(meetingId, contactManager.getMeeting(meetingId).getId());  // test get meeting

        meetingId = setupPastMeeting(contacts);                                         // future meeting turned past meeting
        assertEquals(meetingId, contactManager.getMeeting(meetingId).getId());  // test get meeting

        contacts.add(finder);                                                   // add finder contact to contacts for meeting
        contactManager.addNewPastMeeting(contacts, past, "");                   // add new past meeting directly
        PastMeeting pm = contactManager.getPastMeetingList(finder).get(0);      // direct past meeting
        meetingId = pm.getId();                                                 // get id of past meeting
        assertEquals(meetingId, contactManager.getMeeting(meetingId).getId());  // test get meeting

        // test null returned if meeting does not exist
        assertTrue(contactManager.getMeeting(99999) == null);

    }

    /**
     * getFutureMeetingList Tests
     * Required tests:
     *      - get list of future meetings based on contact or date
     *      - check list is empty if no meetings present with that contact or date
     *      - check list contains all meetings expected with given contact or date
     *      - check past meetings not returned
     *      - confirm chronology of returned list
     *      - check for IllegalArgumentException if contact unknown to contact manager
     */
    @Ignore
    public void testGetFutureMeetingListByContact() throws Exception {

        assertTrue(contactManager.getFutureMeetingList(finder).size() == 0);    // expect empty list for this contact

        int meeting1Id = contactManager.addFutureMeeting(contacts, future);         // add meeting with basil
        future.add(Calendar.DAY_OF_MONTH, +1);                                      // move date
        int meeting2Id = contactManager.addFutureMeeting(contacts, future);         // add meeting with basil
        future.add(Calendar.DAY_OF_MONTH, +1);                                      // move date
        int meeting3Id = contactManager.addFutureMeeting(contacts, future);         // add meeting with basil
        future.add(Calendar.DAY_OF_MONTH, +1);                                      // move date
        Set<Contact> otherContacts = new HashSet<Contact>();                        // setup contact set without basil
        otherContacts.add(finder);
        int meeting4Id = contactManager.addFutureMeeting(otherContacts, future);    // add meeting without basil
        int meeting5Id = setupPastMeeting(contacts);                                // add past meeting with basil

        List<Meeting> meetings = contactManager.getFutureMeetingList(basil);        // get future meeting list

        // check list contains expected meetings
        Meeting meeting1 = contactManager.getMeeting(meeting1Id);       // future with basil
        Meeting meeting2 = contactManager.getMeeting(meeting2Id);       // future with basil
        Meeting meeting3 = contactManager.getMeeting(meeting3Id);       // future with basil
        Meeting meeting4 = contactManager.getMeeting(meeting4Id);       // future without basil
        Meeting meeting5 = contactManager.getMeeting(meeting5Id);       // past with basil

        Set<Meeting> expectedMeetings = new HashSet<Meeting>();         // expect only 3 of the 5 meetings to be returned
        expectedMeetings.add(meeting1);
        expectedMeetings.add(meeting2);
        expectedMeetings.add(meeting3);

        Set<Matcher<Meeting>> matcher = new HashSet<Matcher<Meeting>>();    // setup hamcrest matcher
        for(Meeting m: expectedMeetings)                                    // that checks for the property values of each Meeting
            matcher.add(new SamePropertyValuesAs(m));
        for (Matcher<Meeting> mf : matcher)                                 // confirm that expected meetings are in returned collection
            assertThat(meetings, hasItem(mf));

        assertThat(meetings, IsCollectionWithSize.hasSize(expectedMeetings.size()));    // confirm size of returned collection
        assertThat(meetings, not(hasItem(meeting4)));                                   // confirm return does not contain meeting without basil
        assertThat(meetings, not(hasItem(meeting5)));                                   // confirm return does not contain past meeting with basil

        assertTrue(checkChronologyOfList(meetings)); // confirm all meetings were in date order

    }

    @Ignore
    public void testGetFutureMeetingListByContactThrowsIllegalArgumentException() {

        thrown.expect(IllegalArgumentException.class);      // expect invalid argument exception
        contactManager.getFutureMeetingList(unknown);       // due to unknown contact

    }

    @Ignore
    public void testGetFutureMeetingListByDate() throws Exception {

        assertTrue(contactManager.getFutureMeetingList(future).size() == 0);    // no meetings added, expect empty list

        int meeting1Id = contactManager.addFutureMeeting(contacts, future);     // add meeting on future date
        int meeting2Id = contactManager.addFutureMeeting(contacts, future);     // add meeting on future date

        Calendar otherDate = future;
        otherDate.add(Calendar.DAY_OF_MONTH, +100);
        int meeting3Id = contactManager.addFutureMeeting(contacts, otherDate);  // add meeting on different date

        List<Meeting> meetings = contactManager.getFutureMeetingList(future);   // get meetings by future date

        Meeting meeting1 = contactManager.getMeeting(meeting1Id);   // meeting on future date
        Meeting meeting2 = contactManager.getMeeting(meeting2Id);   // meeting on future date
        Meeting meeting3 = contactManager.getMeeting(meeting3Id);   // meeting on different date

        Set<Meeting> expectedMeetings = new HashSet<Meeting>();     // expect 2 of 3 meetings
        expectedMeetings.add(meeting1);
        expectedMeetings.add(meeting2);

        Set<Matcher<Meeting>> matcher = new HashSet<Matcher<Meeting>>();    // setup hamcrest matcher
        for(Meeting m: expectedMeetings)                                    // that checks for the property values of each Meeting
            matcher.add(new SamePropertyValuesAs(m));
        for (Matcher<Meeting> mf : matcher)                                 // confirm that expected meetings are in returned collection
            assertThat(meetings, hasItem(mf));

        assertThat(meetings, IsCollectionWithSize.hasSize(expectedMeetings.size()));    // confirm size of returned collection
        assertThat(meetings, not(hasItem(meeting3)));                                   // confirm return does not contain meeting on different date

        assertTrue(checkChronologyOfList(meetings)); // confirm all meetings were in date order

    }

    /**
     * getPastMeetingList Tests
     * Required tests:
     *      - get list of past meetings based on contact
     *      - check list is empty if no meetings present with that contact
     *      - check list contains all meetings expected with given contact
     *      - check future meetings not returned
     *      - confirm chronology of returned list
     *      - check for IllegalArgumentException if contact unknown to contact manager
     */
    @Ignore
    public void testGetPastMeetingList() throws Exception {

        PastMeeting pm;

        assertTrue(contactManager.getPastMeetingList(finder).size() == 0);      // no meetings added, expect empty list

        contactManager.addNewPastMeeting(contacts, past, "");                   // add new past meeting directly
        pm = contactManager.getPastMeetingList(basil).get(0);                   // return the past meeting based on basil contact
        int meeting1Id = pm.getId();                                            // get id of past meeting

        int meeting2Id = setupPastMeeting(contacts);                            // setup future-turned-past meeting with basil
        int meeting3Id = setupPastMeeting(contacts);                            // setup future-turned-past meeting with basil

        Set<Contact> otherContacts = new HashSet<Contact>();                    // setup contact collection not including basil
        otherContacts.add(finder);
        contactManager.addNewPastMeeting(otherContacts, past, "");              // add new past meeting directly, without basil
        pm = contactManager.getPastMeetingList(finder).get(0);                  // return the past meeting based on finder contact
        int meeting4Id = pm.getId();                                            // get id of past meeting

        int meeting5Id = setupPastMeeting(otherContacts);                       // setup future-turned-past meeting without basil
        int meeting6Id = contactManager.addFutureMeeting(contacts, future);     // add future meeting

        List<PastMeeting> meetings = contactManager.getPastMeetingList(basil);  // get meetings

        // check list contains expected meetings
        PastMeeting meeting1 = (PastMeeting) contactManager.getMeeting(meeting1Id); // direct past meeting with basil
        PastMeeting meeting2 = (PastMeeting) contactManager.getMeeting(meeting2Id); // future-turned-past meeting with basil
        PastMeeting meeting3 = (PastMeeting) contactManager.getMeeting(meeting3Id); // future-turned-past meeting with basil
        PastMeeting meeting4 = (PastMeeting) contactManager.getMeeting(meeting4Id); // direct past meeting without basil
        PastMeeting meeting5 = (PastMeeting) contactManager.getMeeting(meeting5Id); // future-turned-past meeting without basil
        PastMeeting meeting6 = (PastMeeting) contactManager.getMeeting(meeting6Id); // future meeting with basil

        Set<PastMeeting> expectedMeetings = new HashSet<PastMeeting>(); // expect only 3 of 6 meetings returned
        expectedMeetings.add(meeting1);
        expectedMeetings.add(meeting2);
        expectedMeetings.add(meeting3);

        Set<Matcher<PastMeeting>> matcher = new HashSet<Matcher<PastMeeting>>();    // setup hamcrest matcher
        for(PastMeeting m: expectedMeetings)                                        // that checks for the property values of each Meeting
            matcher.add(new SamePropertyValuesAs(m));
        for (Matcher<PastMeeting> mf : matcher)                                     // confirm that expected meetings are in returned collection
            assertThat(meetings, hasItem(mf));

        assertThat(meetings, IsCollectionWithSize.hasSize(expectedMeetings.size()));    // confirm size of returned collection
        assertThat(meetings, not(hasItem(meeting4)));                                   // confirm return does not contain meeting without basil
        assertThat(meetings, not(hasItem(meeting5)));                                   // confirm return does not contain meeting without basil
        assertThat(meetings, not(hasItem(meeting6)));                                   // confirm return does not contain meeting future meeting

        assertTrue(checkChronologyOfListPast(meetings)); // confirm all meetings were in date order

    }

    @Ignore
    public void testGetPastMeetingListThrowsIllegalArgumentException() throws Exception {

        thrown.expect(IllegalArgumentException.class);      // expect invalid argument exception
        contactManager.getPastMeetingList(unknown);         // due to unknown contact

    }

    /**
     * addNewPastMeeting Tests
     * Required tests:
     *      - add new past meeting
     *      - check meeting added by contact search
     *      - check for IllegalArgumentException if contact unknown to contact manager
     *      - check for IllegalArgumentException if contacts empty
     *      - check for NullPointerException if any input is null
     */
    @Test
    public void testAddNewPastMeeting() throws Exception {

        String pastMeetingNotes = "blah blah";                                  // meeting notes to check on return
        contacts.add(finder);                                                   // add finder contact to contacts for meeting
        contactManager.addNewPastMeeting(contacts, past, pastMeetingNotes);     // add new past meeting
        PastMeeting pm = contactManager.getPastMeetingList(finder).get(0);      // return the past meeting based on finder contact
        assertTrue(pastMeetingNotes.equals(pm.getNotes()));                     // test meeting notes match

    }

    @Ignore
    public void testAddNewPastMeetingThrowsIllegalArgumentException() throws Exception {

        contacts.add(unknown);
        thrown.expect(IllegalArgumentException.class);                // expect illegal argument exception
        contactManager.addNewPastMeeting(contacts, past, "");         // due to unknown contact

        thrown.expect(IllegalArgumentException.class);                          // expect illegal argument exception
        contactManager.addNewPastMeeting(new HashSet<Contact>(), past, "");     // due to empty contact collection

    }

    @Ignore
    public void testAddNewPastMeetingThrowsNullPointerException() throws Exception {

        thrown.expect(NullPointerException.class);              // expect null pointer exception
        contactManager.addNewPastMeeting(null, past, "");       // due to null contacts

        thrown.expect(NullPointerException.class);              // expect null pointer exception
        contactManager.addNewPastMeeting(contacts, null, "");   // due to null date

        thrown.expect(NullPointerException.class);              // expect null pointer exception
        contactManager.addNewPastMeeting(contacts, past, null); // due to null notes

    }

    /**
     * addMeetingNotes Tests
     * Required tests:
     *      - add new meeting notes to past meeting
     *      - check for IllegalArgumentException if meeting does not exist
     *      - check for IllegalStateException if meeting set in future
     *      - check for NullPointerException if any notes are null
     */
    @Ignore
    public void testAddMeetingNotes() throws Exception {

        PastMeeting pm;
        String pastMeetingNotes = "blah blah";  // meeting notes to check

        // method 1, setup future meeting and wait until it is a past meeting
        // then add notes by id
        int pastMeetingId = setupPastMeeting(contacts);                         // setup future-turned-past meeting
        pm = contactManager.getPastMeeting(pastMeetingId);                      // get past meeting by id
        contactManager.addMeetingNotes(pm.getId(), pastMeetingNotes);           // add notes
        assertTrue(pastMeetingNotes.equals(pm.getNotes()));                     // test meeting notes match

        // method 2, setup new past meeting directly, with unique contact to search by
        contacts.add(finder);                                                   // add finder contact to contacts for meeting
        contactManager.addNewPastMeeting(contacts, past, "");                   // add new past meeting directly
        pm = contactManager.getPastMeetingList(finder).get(0);                  // return the past meeting based on finder contact
        contactManager.addMeetingNotes(pm.getId(), pastMeetingNotes);           // add notes
        assertTrue(pastMeetingNotes.equals(pm.getNotes()));                     // test meeting notes match

    }

    @Ignore
    public void testAddMeetingNotesThrowsIllegalArgumentException() {

        int meetingId = 12345678;       // meeting id does not exist
        String notes = "blah blah.";

        thrown.expect(IllegalArgumentException.class);      // expect illegal argument exception
        contactManager.addMeetingNotes(meetingId, notes);   // due to non-existent meeting

    }

    @Ignore
    public void testAddMeetingNotesThrowsIllegalStateException() {

        // add meeting in future
        int meetingId = contactManager.addFutureMeeting(contacts, future);
        String notes = "blah blah.";

        thrown.expect(IllegalStateException.class);         // expect illegal state exception
        contactManager.addMeetingNotes(meetingId, notes);   // due to meeting in future

    }

    @Ignore
    public void testAddMeetingNotesThrowsNullPointerException() {

        contacts.add(finder);                                                   // add finder contact to contacts for meeting
        contactManager.addNewPastMeeting(contacts, past, "");                   // add new past meeting directly
        PastMeeting pm = contactManager.getPastMeetingList(finder).get(0);      // return the past meeting based on finder contact

        thrown.expect(NullPointerException.class);          // expect null point exception
        contactManager.addMeetingNotes(pm.getId(), null);   // when notes are null

    }

    /**
     * addNewContact Tests
     * Required tests:
     *      - add new contact and check name and notes
     *      - check for NullPointerException if any input is null
     */
    @Ignore
    public void testAddNewContact() throws Exception {

        String newContactName = "Mr New Contact";                           // setup new contact
        String newContactNotes = "Mr New Contact's notes";
        contactManager.addNewContact(newContactName, newContactNotes);      // add new contact

        // retrieve all contacts based on name
        Set<Contact> cs = contactManager.getContacts(newContactName);

        int count = 0;

        // for each contact, check notes.
        for (Contact c : cs) {
            if (c.getNotes().equals(newContactNotes))   // if notes equal new contact notes, increment count
                count++;
        }

        assertTrue(count == 1);     // expect 1 contact with new contact notes

    }

    @Ignore
    public void testAddNewContactThrowsNullPointerException() {

        thrown.expect(NullPointerException.class);          // expect null pointer exception
        contactManager.addNewContact(null, "Not null");     // if name is null

        thrown.expect(NullPointerException.class);          // expect null pointer exception
        contactManager.addNewContact("Not null", null);     // if notes are null

    }

    /**
     * getContacts Tests
     * Required tests:
     *      - get contacts by name
     *      - get contacts by ids
     *      - check for IllegalArgumentException if id does not exist
     *      - check for NullPointerException if name is null
     */
    @Ignore
    public void testGetContactsById() throws Exception {

        Set<Contact> cs = contactManager.getContacts(basil.getId(), rebecca.getId());       // get two known contacts by id
        for (Contact c : cs) {
            assertTrue(c.getName().equals(basil.getName()) || c.getName().equals(rebecca.getName()));   // check id returns name that exists
        }

    }

    @Ignore
    public void testGetContactsByIdThrowsIllegalArgumentException() throws Exception {

        thrown.expect(IllegalArgumentException.class);  // expect illegal argument exception
        contactManager.getContacts(12345678);           // if id does not exist


    }

    @Ignore
    public void testGetContactsByName() throws Exception {

        String contactName = "New Contact";                 // setup new contacts
        Contact contact1 = new ContactImpl(contactName);
        Contact contact2 = new ContactImpl(contactName);
        Contact contact3 = new ContactImpl(contactName);

        int[] contactIds = {contact1.getId(), contact2.getId(), contact3.getId()};  // array of expected contact ids
        Set<Contact> cs = contactManager.getContacts(contactName);  // get contacts by name

        int count = 0;
        for (Contact c : cs) {              // for each contact
            for (int id : contactIds) {
                if (c.getId() == id)        // check id corresponds to an expected id
                    count++;
            }

        }

        assertTrue(count == 3); // count of contacts by name should be 3

    }

    @Ignore
    public void testGetContactsByNameThrowsNullPointerException() throws Exception {

        String sNull = null;                        // setup null string
        thrown.expect(NullPointerException.class);  // expect null pointer exception
        contactManager.getContacts(sNull);          // due to null input

    }

    /**
     * flush Tests
     * Required tests:
     *      - check data saved to file
     */
    @Ignore
    public void testFlush() throws Exception {

        String contactName = "Test Contact";
        String contactNotes = "Test Notes";

        int meetingId = contactManager.addFutureMeeting(contacts, future);  // add meeting to contact manager
        contactManager.addNewContact(contactName, contactNotes);            // add contact to contact manager

        contactManager.flush();     // save contents to file

        ContactManager newContactManager = new ContactManagerImpl();    // create new contact manager with saved file

        assertTrue(newContactManager.getMeeting(meetingId) != null);    // check meeting exists in new contact manager

        Set<Contact> cs = newContactManager.getContacts(contactName);   // get contacts by name
        assertTrue(cs.size() == 1);     // check contact exists

    }

    /**
     * internal test method to setup meeting that starts in future but turns into past meeting
     * @param withContacts contacts for meeting
     * @return id of meeting
     */
    private int setupPastMeeting(Set<Contact> withContacts) {

        boolean wait = true;

        // create meeting 10 seconds in future and get ID
        Calendar soon = Calendar.getInstance();
        soon.add(Calendar.SECOND, +10);   // increase 10 seconds
        int pastMeetingId = contactManager.addFutureMeeting(withContacts, soon);

        while(wait) {   // wait until meeting is in the past

            Calendar now = Calendar.getInstance();
            if (now.compareTo(soon) > 0)    // if time now is greater than time when meeting occurred
                wait = false;

        }

        return pastMeetingId;

    }

    /**
     * internal test method to check chronology of collection of PastMeetings
     * @param meetings List of meetings to check order on
     * @return true if list chronologically ordered
     */
    private boolean checkChronologyOfListPast(List<PastMeeting> meetings) {

        // check chronology of given meeting collection
        PastMeeting prevMeeting = meetings.get(0);          // get first meeting
        boolean before = true;                          // initialise check toggle

        for (int i = 1; i < meetings.size(); i++) {     // iterate through meeting list
            PastMeeting curMeeting = meetings.get(i);
            if (curMeeting.getDate().after(prevMeeting.getDate())) {    // compare dates
                // meetings in order
            } else {
                before = false; // meetings not in order
            }
            prevMeeting = curMeeting;   // reset previous meeting for next iteration
        }

        return before;

    }

    /**
     * internal test method to check chronology of collection of Meetings
     * @param meetings List of meetings to check order on
     * @return true if list chronologically ordered
     */
    private boolean checkChronologyOfList(List<Meeting> meetings) {

        // check chronology of given meeting collection
        Meeting prevMeeting = meetings.get(0);          // get first meeting
        boolean before = true;                          // initialise check toggle

        for (int i = 1; i < meetings.size(); i++) {     // iterate through meeting list
            Meeting curMeeting = meetings.get(i);
            if (curMeeting.getDate().after(prevMeeting.getDate())) {    // compare dates
                // meetings in order
            } else {
                before = false; // meetings not in order
            }
            prevMeeting = curMeeting;   // reset previous meeting for next iteration
        }

        return before;

    }

}