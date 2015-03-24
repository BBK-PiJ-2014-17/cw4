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
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <h1>ContactManager Test Module</h1>
 * <p>
 *      The ContactManagerTest module implements a number of unit tests for the ContactManager class
 * </p>
 * <p>
 *     The following tests are covered, see links for details.
 * <ul>
 * <li>1. <code>addFutureMeetings()</code> main test: {@link #testAddFutureMeeting() testAddFutureMeeting main}</li>
 * <li>2. <code>addFutureMeeting() IllegalArgumentException</code> test: {@link #testAddFutureMeetingThrowsIllegalArgumentException() testAddFutureMeeting IllegalArgumentException}</li>
 * <li>3. <code>getPastMeetings()</code> main test: {@link #testGetPastMeeting() testGetPastMeeting main}</li>
 * <li>4. <code>getPastMeetings() IllegalArgumentException</code> test: {@link #testGetPastMeetingThrowsIllegalArgumentException() testGetPastMeeting IllegalArgumentException}</li>
 * <li>5. <code>getFutureMeeting()</code> main test: {@link #testGetFutureMeeting() testGetFutureMeeting main}</li>
 * <li>6. <code>getFutureMeeting() IllegalArgumentException</code> test: {@link #testGetFutureMeetingThrowsIllegalArgumentException() testGetFutureMeeting IllegalArgumentException}</li>
 * <li>7. <code>getMeeting()</code> main test: {@link #testGetMeeting() testGetMeeting main}</li>
 * <li>8. <code>getFutureMeetingList()</code> by contact test: {@link #testGetFutureMeetingListByContact() testGetFutureMeetingListByContact}</li>
 * <li>9. <code>getFutureMeetingList()</code> by contact <code>IllegalArgumentException</code> test: {@link #testGetFutureMeetingListByContactThrowsIllegalArgumentException() testGetFutureMeetingListByContact IllegalArgumentException}</li>
 * <li>11. <code>getPastMeetingList()</code> main test: {@link #testGetPastMeetingList() testGetPastMeetingList main}</li>
 * <li>12. <code>getPastMeetingList() IllegalArgumentException</code> test: {@link #testGetPastMeetingListThrowsIllegalArgumentException() testGetPastMeetingList IllegalArgumentException}</li>
 * <li>13. <code>addNewPastMeeting()</code> main test: {@link #testAddNewPastMeeting() testAddNewPastMeeting main}</li>
 * <li>14. <code>addNewPastMeeting() IllegalArgumentException</code> test: {@link #testAddNewPastMeetingThrowsIllegalArgumentException() testAddNewPastMeeting IllegalArgumentException}</li>
 * <li>15. <code>addNewPastMeeting() NullPointerException</code> test: {@link #testAddNewPastMeetingThrowsNullPointerException() testAddNewPastMeeting NullPointerException}</li>
 * <li>16. <code>addMeetingNotes()</code> main test: {@link #testAddMeetingNotes() testAddMeetingNotes main}</li>
 * <li>17. <code>addMeetingNotes() IllegalArgumentException</code> test: {@link #testAddMeetingNotesThrowsIllegalArgumentException() testAddMeetingNotes IllegalArgumentException}</li>
 * <li>18. <code>addMeetingNotes() NullPointerException</code> test: {@link #testAddMeetingNotesThrowsNullPointerException() testAddMeetingNotes NullPointerException}</li>
 * <li>19. <code>addMeetingNotes() IllegalStateException</code> test: {@link #testAddMeetingNotesThrowsIllegalStateException() testAddMeetingNotes IllegalStateException}</li>
 * <li>20. <code>addNewContact()</code> main test: {@link #testAddNewContact() testAddNewContact main}</li>
 * <li>21. <code>addNewContact() NullPointerException</code> test: {@link #testAddNewContactThrowsNullPointerException() testAddNewContact NullPointerException}</li>
 * <li>22. <code>getContacts()</code> by id test: {@link #testGetContactsById() testGetContacts by id}</li>
 * <li>23. <code>getContacts()</code> by id <code>IllegalArgumentException</code> test: {@link #testGetContactsByIdThrowsIllegalArgumentException() testGetContacts by id IllegalArgumentException}</li>
 * <li>24. <code>getContacts()</code> by name test: {@link #testGetContactsByName() testGetContacts by name}</li>
 * <li>25. <code>getContacts()</code> by name <code>NullPointerException</code> test: {@link #testGetContactsByNameThrowsNullPointerException() testGetContacts by name NullPointerException}</li>
 * <li>26. <code>flush()</code> main test: {@link #testFlush() testFlush main}</li>
 * </ul></p>
 *
 * All tests passed in single run.
 *
 * @author Basil Mason
 * @version 1.0.1
 * @since 07/02/2015
 */
public class ContactManagerTest {

    // set general variables for use in any test
    private ContactManager contactManager;              // the contact manager object to test
    private String basilString, rebeccaString;          // search string to find past meetings by contact
    private Set<Contact> contacts;                      // a collection of contacts for meetings
    private Calendar past, future;                      // dates for past and future meetings
    SimpleDateFormat sdf;                               // date format for creation of unique strings in tests

    /**
     * <code>setUp()</code>
     * <p>
     *     This method constructs the environment for the tests, initialising a number of variables
     *     that will be used widely throughout the tests. This includes contacts to be used for any
     *     meetings that are created, as well as dates for past and future meetings.
     *
     *     The ContactManager object itself is also initialised.
     * </p>
     */
    @Before
    public void setUp() {

        // setup generic past and future dates to be used when meetings are created.
        past = Calendar.getInstance();
        past.add(Calendar.DAY_OF_MONTH, -1);                    // date for past meeting
        future = Calendar.getInstance();
        future.add(Calendar.DAY_OF_MONTH, +1);                  // date for future meeting

        // setup date formatting to be used in test methods
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        // setup generic contact set with contacts known to ContactManager
        // 1. create strings for contacts
        basilString = "Basil Mason";
        rebeccaString = "Rebecca White";

        // 2. setup the contact manager object
        contactManager = new ContactManagerImpl();

        // 3. add the new contacts to the contact manager
        contactManager.addNewContact(basilString, "");
        contactManager.addNewContact(rebeccaString, "");

        // 4. retrieve the contacts and store in collection for future use
        contacts = new HashSet<Contact>();
        for (Contact c : contactManager.getContacts(basilString))
            contacts.add(c);
        for (Contact c : contactManager.getContacts(rebeccaString))
            contacts.add(c);

    }

    /**
     * <code>tearDown()</code>
     * <p>
     *     This method flushes the contact manager to file for further tests. The first run of all tests will start from
     *     a blank file, i.e. no meetings or contacts. But, future tests will load the file from any previous tests. This
     *     continues to test the contact managers ability to store and load data, as well as work with the existing data.
     * </p>
     */
    @After
    public void tearDown() {

        // flush contact manager
        contactManager.flush();

    }

    // A unit testing rule to check exception handling. Initially set to type none.
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * 1. <code>testAddFutureMeeting()</code> main test
     * <p>
     *     This method tests the contact manager's {@link ContactManager#addFutureMeeting addFutureMeeting} method and its
     *     main functionality. The method should add a meeting to to be held in future to the contact manager's internal
     *     list of meetings. This meeting should also receive a unique id.
     * </p>
     */
    @Test
    public void testAddFutureMeeting() {

        // two future meetings are added and their IDs stored
        int m1 = contactManager.addFutureMeeting(contacts, future); // add first meeting
        int m2 = contactManager.addFutureMeeting(contacts, future); // add second meeting

        // check that the meeting ids are unique
        assertTrue(m1 != m2);

        // check that the meetings exist in the contact manager
        FutureMeeting fm1 = contactManager.getFutureMeeting(m1);
        FutureMeeting fm2 = contactManager.getFutureMeeting(m2);

        assertTrue(fm1 != null);    // confirm meeting one return is not null
        assertTrue(fm2 != null);    // confirm meeting two return is not null

    }

    /**
     * 2. <code>testAddFutureMeeting() IllegalArgumentException</code> test
     * <p>
     *     This test attempts to create a future meeting with a past date, and a future meeting with
     *     contacts unknown to the contact manager. In both cases, a IllegalArgumentException should
     *     be thrown.
     * </p>
     */
    @Test
    public void testAddFutureMeetingThrowsIllegalArgumentException() {

        // check future meeting create with past date
        thrown.expect(IllegalArgumentException.class);      // expect invalid argument exception
        contactManager.addFutureMeeting(contacts, past);    // due to meeting in past

        // check future meeting created with contact unknown to contact manager
        contacts.add(new ContactImpl(99999, "Anon"));   // add contact unknown to contact manager to set of contacts for meeting
        thrown.expect(IllegalArgumentException.class);      // expect invalid argument exception
        contactManager.addFutureMeeting(contacts, future);  // due to unknown contact

    }

    /**
     * 3. <code>testGetPastMeeting()</code> main test
     * <p>
     *     This method tests the contact manager's {@link ContactManager#getPastMeeting getPastMeeting} method and its
     *     main functionality. The method should return a past meeting given an id or null if there is not one.
     *
     *     This method is tested in 3 ways. 1. adding a new future meeting and waiting until it is a past meeting,
     *     checking the conversion to past meeting works, 2. adding a new past meeting directly (this requires a method
     *     for identifying the new past meeting as the contact manager does not return an id from this method, so some
     *     searching is done on a unique contact and meeting notes string), 3. that null is returned if the meeting does
     *     not exist.
     * </p>
     */
    @Test
    public void testGetPastMeeting() {

        PastMeeting pm;     // past meeting to be found
        int pastMeetingId;  // id of past meeting to be found

        // method 1, setup future meeting and wait until it is a past meeting
        // then search by id
        // this is necessary to confirm that future meetings convert to past meetings correctly
        pastMeetingId = setupPastMeeting(contacts);         // setup meeting in past using internal method
        pm = contactManager.getPastMeeting(pastMeetingId);  // get past meeting by id
        assertEquals(pastMeetingId, pm.getId());            // test the id of the returned meeting is in fact the correct id

        // method 2, setup a new past meeting directly
        // contact manager does not return ids from newPastMeeting, so other identification method required
        // meeting setup with with unique contact and notes to search by
        String uniqueNotes = sdf.format(new Date()).toString();             // prepare a string of unique notes to inspect meeting by
        int c1 = generateUniqueContactForMeetings(uniqueNotes);             // call internal method to generate unique contact
        Contact c = (Contact) contactManager.getContacts(c1).toArray()[0];  // find unique contact, one expected

        // create contact collection with unique contact to create meeting with
        Set<Contact> cs = new HashSet<Contact>();
        cs.add(c);  // add unique contact

        // create new past meeting directly with unique notes
        contactManager.addNewPastMeeting(cs, past, uniqueNotes);

        // get list of past meetings based on unique contact
        List<PastMeeting> pms = contactManager.getPastMeetingList(c);

        // one meeting expected in list with unique contact, set as past meeting
        pm = contactManager.getPastMeeting(pms.get(0).getId());

        // check unique notes on meeting confirms meeting added to contact manager
        assertTrue(uniqueNotes.equals(pm.getNotes()));

        // test null returned if meeting does not exist
        assertTrue(contactManager.getPastMeeting(99999) == null);

    }

    /**
     * 4. <code>testGetPastMeeting() IllegalArgumentException</code> test
     * <p>
     *     This test attempts to request a past meeting with the id of a future meeting. An IllegalArgumentException
     *     should be thrown.
     * </p>
     */
    @Test
    public void testGetPastMeetingThrowsIllegalArgumentException() {

        // create future meeting and get id
        int futureMeetingId = contactManager.addFutureMeeting(contacts, future);

        // request past meeting with future meeting id
        thrown.expect(IllegalArgumentException.class);      // expect invalid argument exception
        contactManager.getPastMeeting(futureMeetingId);     // due to meeting in future

    }

    /**
     * 5. <code>testGetFutureMeeting()</code> main test
     * <p>
     *     This method tests the contact manager's {@link ContactManager#getFutureMeeting getFutureMeeting} method and its
     *     main functionality. The method should get a future meeting, given an id or null id no such meeting exists.
     * </p>
     */
    @Test
    public void testGetFutureMeeting() {

        // add future meeting and get ID
        int futureMeetingId = contactManager.addFutureMeeting(contacts, future);

        // get future meeting by id and check that the returned meeting has the id expected
        assertEquals(futureMeetingId, contactManager.getFutureMeeting(futureMeetingId).getId());

        // test null returned if meeting does not exist
        assertTrue(contactManager.getFutureMeeting(99999) == null);

    }

    /**
     * 6. <code>testGetFutureMeeting() IllegalArgumentException</code> test
     * <p>
     *     This test attempts to request a future meeting with the id of a past meeting. An IllegalArgumentException
     *     should be thrown.
     * </p>
     */
    @Test
    public void testGetFutureMeetingThrowsIllegalArgumentException() {

        // id of future meeting to be created
        int futureMeetingId;

        // setup future meeting and wait until it is a past meeting
        // then search by id
        // this is necessary to confirm that future meetings convert to past meetings correctly
        futureMeetingId = setupPastMeeting(contacts);       // setup meeting in past using internal method
        thrown.expect(IllegalArgumentException.class);      // expect invalid argument exception
        contactManager.getFutureMeeting(futureMeetingId);   // due to meeting in past

    }

    /**
     * 7. <code>testGetMeeting()</code> main test
     * <p>
     *     This method tests the contact manager's {@link ContactManager#getMeeting getMeeting} method and its
     *     main functionality. The method should get a meeting, given an id or null id no such meeting exists. This
     *     should work for returning both past and future meetings
     * </p>
     */
    @Test
    public void testGetMeeting() {

        int meetingId;  // meeting id to be found
        Meeting m;      // meeting to be found

        // 1. get future meeting
        meetingId = contactManager.addFutureMeeting(contacts, future);          // future meeting
        m = contactManager.getMeeting(meetingId);
        assertTrue(m instanceof FutureMeeting);                                 // check is future meeting
        assertEquals(meetingId, m.getId());                                     // test meeting id as expected

        // 2. get future-turned-past meeting
        meetingId = setupPastMeeting(contacts);                                 // future meeting turned past meeting
        m = contactManager.getMeeting(meetingId);
        assertTrue(m instanceof PastMeeting);                                   // check is past meeting
        assertEquals(meetingId, m.getId());                                     // test meeting id as expected

        // 3. setup a new past meeting directly
        String uniqueNotes = sdf.format(new Date()).toString();             // prepare a string of unique notes to inspect meeting by
        int c1 = generateUniqueContactForMeetings(uniqueNotes);             // call internal method to generate unique contact
        Contact c = (Contact) contactManager.getContacts(c1).toArray()[0];  // find unique contact, one expected

        // create contact collection with unique contact to create meeting with
        Set<Contact> cs = new HashSet<Contact>();
        cs.add(c);  // add unique contact

        // create new past meeting directly with unique notes
        contactManager.addNewPastMeeting(cs, past, uniqueNotes);

        // get list of past meetings based on unique contact
        List<PastMeeting> pms = contactManager.getPastMeetingList(c);

        // one meeting expected in list with unique contact, set as meeting
        meetingId = pms.get(0).getId();
        m = contactManager.getMeeting(meetingId);
        assertTrue(m instanceof PastMeeting);               // check is past meeting
        assertEquals(meetingId, m.getId());                 // test meeting id as expected

        // 4. test null returned if meeting does not exist
        assertTrue(contactManager.getMeeting(99999) == null);

    }

    /**
     * 8. <code>testGetFutureMeetingList()</code> by contact test
     * <p>
     *     This method tests the contact manager's {@link ContactManager#getFutureMeetingList(Contact) getFutureMeetingList by contact}
     *     method and its search by contact overload. The method should return a list of future meetings, sorted in
     *     chronological order, with no duplicates, to which the contact is a party. An empty list is returned if no
     *     meeting with the given contact exists.
     *
     *     Check no past meetings are returned, or meetings without the specified contact.
     * </p>
     */
    @Test
    public void testGetFutureMeetingListByContact() {

        // create a unique contact with no meetings
        String uniqueNotes = sdf.format(new Date()).toString();
        Set<Contact> cs = contactManager.getContacts(generateUniqueContactForMeetings(uniqueNotes));
        Contact c = (Contact) cs.toArray()[0];

        // expect an empty list
        assertTrue(contactManager.getFutureMeetingList(c).size() == 0);

        // add a set of meetings with the unique contact
        int meeting1Id = contactManager.addFutureMeeting(cs, future);
        Calendar date2 = Calendar.getInstance();
        date2.add(Calendar.DAY_OF_MONTH, +2);
        int meeting2Id = contactManager.addFutureMeeting(cs, date2);
        Calendar date3 = Calendar.getInstance();
        date3.add(Calendar.DAY_OF_MONTH, +3);
        int meeting3Id = contactManager.addFutureMeeting(cs, date3);

        // add a meeting without the unique contact
        int meeting4Id = contactManager.addFutureMeeting(contacts, future);

        // add a past meeting with the unique contact
        int meeting5Id = setupPastMeeting(cs);

        // get all meetings
        Meeting meeting1 = contactManager.getMeeting(meeting1Id);       // future meeting with contact
        Meeting meeting2 = contactManager.getMeeting(meeting2Id);       // future meeting with contact
        Meeting meeting3 = contactManager.getMeeting(meeting3Id);       // future meeting with contact
        Meeting meeting4 = contactManager.getMeeting(meeting4Id);       // future meeting without contact
        Meeting meeting5 = contactManager.getMeeting(meeting5Id);       // past meeting with contact

        // setup list of expected meetings (3 of 5)
        List<Meeting> expectedMeetings = new ArrayList<Meeting>();
        expectedMeetings.add(meeting1);
        expectedMeetings.add(meeting2);
        expectedMeetings.add(meeting3);

        // get the list of future meetings from contact manager
        List<Meeting> meetings = contactManager.getFutureMeetingList(c);

        // setup hamcrest matcher to check contents of lists
        List<Matcher<Meeting>> matcher = new ArrayList<Matcher<Meeting>>();
        for(Meeting m: expectedMeetings)
            matcher.add(new SamePropertyValuesAs(m));

        // check that returned meetings match expected meetings
        for (Matcher<Meeting> mf : matcher)
            assertThat(meetings, hasItem(mf));

        // check size matches
        assertThat(meetings, IsCollectionWithSize.hasSize(expectedMeetings.size()));

        // confirm returned list does not contact past meeting or meeting without contact
        assertThat(meetings, not(hasItem(meeting4)));
        assertThat(meetings, not(hasItem(meeting5)));

        // check chronology of returned list
        assertTrue(checkChronologyOfList(meetings));

    }

    /**
     * 9. <code>testGetFutureMeetingList()</code> by contact <code>IllegalArgumentException</code> test
     * <p>
     *     This test attempts to request a list of future meetings with a contact that is unknown to the contact
     *     manager. An IllegalArgumentException should be thrown.
     * </p>
     */
    @Test
    public void testGetFutureMeetingListByContactThrowsIllegalArgumentException() {

        thrown.expect(IllegalArgumentException.class);                          // expect invalid argument exception
        contactManager.getFutureMeetingList(new ContactImpl(99999, "Anon"));    // due to unknown contact

    }

    /**
     * 10. <code>testGetFutureMeetingList()</code> by date test
     * <p>
     *     This method tests the contact manager's {@link ContactManager#getFutureMeetingList(Calendar) getFutureMeetingList by date}
     *     method and its search by date overload. The method should return a list of future meetings, sorted in
     *     chronological order, with no duplicates, on the given date. An empty list is returned if no
     *     meeting on the given date exists.
     *
     *     Check no past meetings are returned, or meetings on different dates.
     * </p>
     */
    @Test
    public void testGetFutureMeetingListByDate() {

        // pick random date in the future
        future.add(Calendar.DAY_OF_MONTH, +110);

        // no meetings should be scheduled for this date, expect empty list
        assertTrue(contactManager.getFutureMeetingList(future).size() == 0);

        // create future meetings on specific date
        int meeting1Id = contactManager.addFutureMeeting(contacts, future);     // add meeting on future date
        int meeting2Id = contactManager.addFutureMeeting(contacts, future);     // add meeting on future date

        // create future meeting on a different date
        Calendar otherDate = Calendar.getInstance();
        otherDate.add(Calendar.DAY_OF_MONTH, +100);
        int meeting3Id = contactManager.addFutureMeeting(contacts, otherDate);  // add meeting on different date

        // get meetings
        Meeting meeting1 = contactManager.getMeeting(meeting1Id);   // meeting on future date
        Meeting meeting2 = contactManager.getMeeting(meeting2Id);   // meeting on future date
        Meeting meeting3 = contactManager.getMeeting(meeting3Id);   // meeting on different date

        // setup list of expected meetings (2 of 3)
        List<Meeting> expectedMeetings = new ArrayList<Meeting>();
        expectedMeetings.add(meeting1);
        expectedMeetings.add(meeting2);

        // get list of meetings based on date
        List<Meeting> meetings = contactManager.getFutureMeetingList(future);

        // setup hamcrest matcher
        List<Matcher<Meeting>> matcher = new ArrayList<Matcher<Meeting>>();
        for(Meeting m: expectedMeetings)
            matcher.add(new SamePropertyValuesAs(m));

        // check each member of the collections match
        for (Matcher<Meeting> mf : matcher)
            assertThat(meetings, hasItem(mf));

        // check collections are the same size
        assertThat(meetings, IsCollectionWithSize.hasSize(expectedMeetings.size()));

        // check meeting on different date is not present
        assertThat(meetings, not(hasItem(meeting3)));

        // check list for is in chronological order
        assertTrue(checkChronologyOfList(meetings));

    }

    /**
     * 11. <code>testGetPastMeetingList()</code> main test
     * <p>
     *     This method tests the contact manager's {@link ContactManager#getPastMeetingList(Contact) getPastMeetingList}
     *     method. The method should return a list of past meetings, sorted in chronological order, with no duplicates,
     *     based on the a given contact. An empty list is returned if no meeting with the given contact exists.
     *
     *     Check no future meetings are returned, or meetings on without the given contact.
     * </p>
     */
    @Test
    public void testGetPastMeetingList() throws Exception {

        // setup unique contact
        String uniqueNotes = sdf.format(new Date()).toString();
        Set<Contact> cs = contactManager.getContacts(generateUniqueContactForMeetings(uniqueNotes));
        Contact c = (Contact) cs.toArray()[0];

        // no meetings added with this contact, expect empty list
        assertTrue(contactManager.getPastMeetingList(c).size() == 0);

        // add past meetings with unique contact
        contactManager.addNewPastMeeting(cs, past, "");
        contactManager.addNewPastMeeting(cs, past, "");
        Calendar otherPast = Calendar.getInstance();
        otherPast.add(Calendar.DAY_OF_MONTH, -120);
        contactManager.addNewPastMeeting(cs, otherPast, "");

        // add future meeting with contact
        int fmid = contactManager.addFutureMeeting(cs, future);

        // add past meeting without unique contact
        int pmid = setupPastMeeting(contacts);

        // get list of past meetings with unique contact
        List<PastMeeting> pms = contactManager.getPastMeetingList(c);

        // expect 3 meetings
        assertTrue(pms.size() == 3);

        // check future meeting, and meeting without contact, not contained
        for (PastMeeting pm : pms) {
            assertTrue(pm.getId() != pmid || pm.getId() != fmid);
        }

        // confirm all meetings were in date order
        assertTrue(checkChronologyOfListPast(pms));

    }

    /**
     * 12. <code>testGetPastMeetingList() IllegalArgumentException</code> test
     * <p>
     *     This test attempts to get a list of meetings for a contact that is unknown to the contact manager.
     *     An IllegalArgumentException should be thrown.
     * </p>
     */
    @Test
    public void testGetPastMeetingListThrowsIllegalArgumentException() throws Exception {

        thrown.expect(IllegalArgumentException.class);                      // expect invalid argument exception
        contactManager.getPastMeetingList(new ContactImpl(9999, "Anon"));   // due to unknown contact

    }

    /**
     * 13. <code>testAddNewPastMeeting()</code> main test
     * <p>
     *     This method tests the contact manager's {@link ContactManager#addNewPastMeeting addNewPastMeeting} method and its
     *     main functionality. The method should add a meeting to to be held in past to the contact manager's internal
     *     list of meetings.
     * </p>
     */
    @Test
    public void testAddNewPastMeeting() {

        // setup unique contact to search by
        String pastMeetingNotes = sdf.format(new Date()).toString();
        int c1 = generateUniqueContactForMeetings(pastMeetingNotes);
        Contact c = (Contact) contactManager.getContacts(c1).toArray()[0];

        // setup collection containing unique contact
        Set<Contact> cs = new HashSet<Contact>();
        cs.add(c);

        // add new past meeting with unique contact and notes
        contactManager.addNewPastMeeting(cs, past, pastMeetingNotes);

        // get all meetings with unique contacts
        List<PastMeeting> pms = contactManager.getPastMeetingList(c);

        // check meeting exists with unique notes
        assertTrue(pastMeetingNotes.equals(pms.get(0).getNotes()));

    }

    /**
     * 14. <code>testAddNewPastMeeting() IllegalArgumentException</code> test
     * <p>
     *     This test attempts to get create a new past meeting with an invalid contact collection: either
     *     empty, or containing contacts unknown to the contact manager. An IllegalArgumentException should be thrown.
     * </p>
     */
    @Test
    public void testAddNewPastMeetingThrowsIllegalArgumentException() {

        // create collection of contacts with contact unknown to contact manager
        Set<Contact> cs = new HashSet<Contact>();
        cs.add(new ContactImpl(99999, "Anon"));

        thrown.expect(IllegalArgumentException.class);          // expect illegal argument exception
        contactManager.addNewPastMeeting(cs, past, "");         // due to unknown contact

        thrown.expect(IllegalArgumentException.class);                          // expect illegal argument exception
        contactManager.addNewPastMeeting(new HashSet<Contact>(), past, "");     // due to empty contact collection

    }

    /**
     * 15. <code>testAddNewPastMeeting() NullPointerException</code> test
     * <p>
     *     This test attempts to get create a new past meeting with invalid arguments, i.e. <code>null</code>. A
     *     NullPointerException should be thrown.
     * </p>
     */
    @Test
    public void testAddNewPastMeetingThrowsNullPointerException() {

        thrown.expect(NullPointerException.class);              // expect null pointer exception
        contactManager.addNewPastMeeting(null, past, "");       // due to null contacts

        thrown.expect(NullPointerException.class);              // expect null pointer exception
        contactManager.addNewPastMeeting(contacts, null, "");   // due to null date

        thrown.expect(NullPointerException.class);              // expect null pointer exception
        contactManager.addNewPastMeeting(contacts, past, null); // due to null notes

    }

    /**
     * 16. <code>testAddMeetingNotes()</code> main test
     * <p>
     *     This method tests the contact manager's {@link ContactManager#addMeetingNotes addMeetingNotes} method and its
     *     main functionality. This method should add meeting notes to a past meeting.
     * </p>
     */
    @Test
    public void testAddMeetingNotes() {

        PastMeeting pm;                                                 // past meeting to be found
        int pastMeetingId;                                              // id of past meeting to be found
        String pastMeetingNotes = sdf.format(new Date()).toString();    // meeting notes to check

        // method 1, setup future meeting and wait until it is a past meeting
        // then search by id
        // this is necessary to confirm that future meetings convert to past meetings correctly
        pastMeetingId = setupPastMeeting(contacts);                     // setup meeting in past using internal method
        pm = contactManager.getPastMeeting(pastMeetingId);              // get past meeting by id
        contactManager.addMeetingNotes(pm.getId(), pastMeetingNotes);   // add notes
        pm = contactManager.getPastMeeting(pm.getId());                 // re-get pm by ID because addMeetingNotes creates
                                                                        // new instance of PastMeeting, since there is no
                                                                        // method for adding notes without creating a meeting.

        // check unique notes on meeting confirms meeting notes added to past meeting
        assertTrue(pm.getNotes().equals(pastMeetingNotes));

        // method 2, setup a new past meeting directly
        // checking that notes can be added to meetings that have been converted to past meetings
        int c1 = generateUniqueContactForMeetings(pastMeetingNotes);        // call internal method to generate unique contact
        Contact c = (Contact) contactManager.getContacts(c1).toArray()[0];  // find unique contact, one expected

        // create contact collection with unique contact to create meeting with
        Set<Contact> cs = new HashSet<Contact>();
        cs.add(c);  // add unique contact

        // create new past meeting directly with blank notes
        contactManager.addNewPastMeeting(cs, past, "");

        // get list of past meetings based on unique contact
        List<PastMeeting> pms = contactManager.getPastMeetingList(c);

        // one meeting expected in list with unique contact, set as past meeting
        pm = contactManager.getPastMeeting(pms.get(0).getId());

        // add meeting notes to now past meeting and re-get
        contactManager.addMeetingNotes(pm.getId(), pastMeetingNotes);
        pm = contactManager.getPastMeeting(pm.getId());

        // check unique notes on meeting confirms meeting notes added to past meeting
        assertTrue(pm.getNotes().equals(pastMeetingNotes));

    }

    /**
     * 17. <code>testAddMeetingNotes() IllegalArgumentException</code> test
     * <p>
     *     This test attempts to add some notes to a meeting that does not exist. An IllegalArgumentException
     *     should be thrown.
     * </p>
     */
    @Test
    public void testAddMeetingNotesThrowsIllegalArgumentException() {

        int meetingId = 12345678;                           // meeting id does not exist
        String notes = "blah blah.";

        thrown.expect(IllegalArgumentException.class);      // expect illegal argument exception
        contactManager.addMeetingNotes(meetingId, notes);   // due to non-existent meeting

    }

    /**
     * 18. <code>testAddMeetingNotes() NullPointerException</code> test
     * <p>
     *     This test attempts to add <code>null</code> notes to a past meeting. A NullPointerException
     *     should be thrown.
     * </p>
     */
    @Test
    public void testAddMeetingNotesThrowsNullPointerException() {

        int pmid = setupPastMeeting(contacts);          // setup past meeting

        thrown.expect(NullPointerException.class);      // expect null point exception
        contactManager.addMeetingNotes(pmid, null);     // when notes are null

    }

    /**
     * 19. <code>testAddMeetingNotes() IllegalStateException</code> test
     * <p>
     *     This test attempts to add some notes to a future meeting. An IllegalStateException
     *     should be thrown.
     * </p>
     */
    @Test
    public void testAddMeetingNotesThrowsIllegalStateException() {

        // add meeting in future
        int meetingId = contactManager.addFutureMeeting(contacts, future);
        String notes = "blah blah.";

        thrown.expect(IllegalStateException.class);         // expect illegal state exception
        contactManager.addMeetingNotes(meetingId, notes);   // due to meeting in future

    }

    /**
     * 20. <code>testAddNewContact()</code> main test
     * <p>
     *     This method tests the contact manager's {@link ContactManager#addNewContact addNewContact} method and its
     *     main functionality. This method should add a new meeting to the contact manager.
     * </p>
     */
    @Test
    public void testAddNewContact() {

        String uniqueNotes = sdf.format(new Date()).toString();         // unique string
        String contactName = "New Contact";                             // contact name
        contactManager.addNewContact(contactName, uniqueNotes);         // add contact with unique notes to be identified by

        // find all contacts with new contact name
        Set<Contact> cs = contactManager.getContacts(contactName);

        int count = 0;      // counter
        // loop through collection of contacts and locate contact based on unique notes string
        for (Contact c : cs) {
            if (c.getNotes().equals(uniqueNotes))     // check contact has the unique notes
                count++;                // get the id of the contact that has the unique notes
        }

        assertTrue(count == 1);     // expect 1 contact with new contact notes

    }

    /**
     * 21. <code>testAddNewContact() NullPointerException</code> test
     * <p>
     *     This test attempts to add a contact with a <code>null</code> argument. A NullPointerException
     *     should be thrown.
     * </p>
     */
    @Test
    public void testAddNewContactThrowsNullPointerException() {

        thrown.expect(NullPointerException.class);          // expect null pointer exception
        contactManager.addNewContact(null, "Not null");     // if name is null

        thrown.expect(NullPointerException.class);          // expect null pointer exception
        contactManager.addNewContact("Not null", null);     // if notes are null

    }

    /**
     * 22. <code>testGetContacts()</code> by id test
     * <p>
     *     This method tests the contact manager's {@link ContactManager#getContacts(int...) getContacts(int)} by id method
     *     and its main functionality. This method should retrieve one or many contacts from the contact manager based on id.
     * </p>
     */
    @Test
    public void testGetContactsById() {

        String uniqueString1 = "1 : " + sdf.format(new Date()).toString();
        int contact1 = generateUniqueContactForMeetings(uniqueString1);
        String uniqueString2 = "2 : " + sdf.format(new Date()).toString();
        int contact2 = generateUniqueContactForMeetings(uniqueString1);

        Set<Contact> cs = contactManager.getContacts(contact1, contact2);       // get two known contacts by id
        for (Contact c : cs) {
            assertTrue(c.getNotes().equals(uniqueString1) || c.getNotes().equals(uniqueString2));   // check id returns name that exists
        }

    }

    /**
     * 23. <code>testGetContacts() IllegalArgumentException</code> test
     * <p>
     *     This test attempts to get a contact that does not exist. An IllegalArgumentException
     *     should be thrown.
     * </p>
     */
    @Test
    public void testGetContactsByIdThrowsIllegalArgumentException() {

        thrown.expect(IllegalArgumentException.class);  // expect illegal argument exception
        contactManager.getContacts(12345678);           // if id does not exist

    }

    /**
     * 24. <code>testGetContacts()</code> by name test
     * <p>
     *     This method tests the contact manager's {@link ContactManager#getContacts(String) getContacts(String)} by name method
     *     and its main functionality. This method should retrieve a collection of contacts based on name. It should
     *     contain all contacts who's names contain the given string, or be empty otherwise.
     * </p>
     */
    @Test
    public void testGetContactsByName() {

        String contactName = "New Contact";                         // new contact name
        String uniqueString = sdf.format(new Date()).toString();    // a unique string

        String contactNotes1 = "New Notes 1" + uniqueString;        // unique notes for contact 1
        String contactNotes2 = "New Notes 2" + uniqueString;        // unique notes for contact 2
        String contactNotes3 = "New Notes 3" + uniqueString;        // unique notes for contact 3
        String contactNotes4 = "New Notes 4" + uniqueString;        // unique notes for contact 4

        contactManager.addNewContact(contactName, contactNotes1);   // add contact 1
        contactManager.addNewContact(contactName, contactNotes2);   // add contact 2
        contactManager.addNewContact(contactName, contactNotes3);   // add contact 3
        contactManager.addNewContact("contains" + contactName + "contains", contactNotes4);     // add contact 4, contains
                                                                                                // contact name

        String[] contactNotes = {contactNotes1, contactNotes2, contactNotes3, contactNotes4};  // array of expected contact notes
        Set<Contact> cs = contactManager.getContacts(contactName);  // get contacts by name

        int count = 0;
        for (Contact c : cs) {                      // for each contact
            for (String s : contactNotes) {
                if (c.getNotes().equals(s))         // check id corresponds to an expected id
                    count++;
            }

        }

        assertTrue(count == 4); // count of contacts by name should be 4

    }

    /**
     * 25. <code>testGetContacts() NullPointerException</code> test
     * <p>
     *     This test attempts to find a contact with a null string. A NullPointerException
     *     should be thrown.
     * </p>
     */
    @Test
    public void testGetContactsByNameThrowsNullPointerException() throws Exception {

        String sNull = null;                        // setup null string
        thrown.expect(NullPointerException.class);  // expect null pointer exception
        contactManager.getContacts(sNull);          // due to null input

    }

    /**
     * 26. <code>testFlush()</code> main test
     * <p>
     *     This method tests the contact manager's {@link ContactManager#flush() flush} method. Data should be written
     *     to file and then retrieved.
     * </p>
     */
    @Test
    public void testFlush() {

        // add meeting to contact manager and get id
        int meetingId = contactManager.addFutureMeeting(contacts, future);

        // add contact the contact manager and get id
        String uniqueString = sdf.format(new Date()).toString();
        int contactId = generateUniqueContactForMeetings(uniqueString);

        // flush the data to file
        contactManager.flush();

        // create new instance of contact manager with saved file
        ContactManager newContactManager = new ContactManagerImpl();

        // check meeting exists in new contact manager
        assertTrue(newContactManager.getMeeting(meetingId) != null);

        // check contact exists in new contact manager
        Set<Contact> cs = newContactManager.getContacts(contactId);   // get contacts by id
        assertTrue(cs.size() == 1);     // check contact exists

    }

    /* I N T E R N A L   M E T H O D S */

    /**
     * <code>setupPastMeeting</code> internal method
     * <p>
     *     internal test method to setup meeting that starts in future but turns into past meeting
     *     @param withContacts contacts for meeting
     *     @return id of meeting
     * </p>
     */
    private int setupPastMeeting(Set<Contact> withContacts) {

        boolean wait = true;

        // create meeting 2 seconds in future and get ID
        Calendar soon = Calendar.getInstance();
        soon.add(Calendar.SECOND, +2);   // increase 10 seconds
        int pastMeetingId = contactManager.addFutureMeeting(withContacts, soon);

        while(wait) {   // wait until meeting is in the past

            Calendar now = Calendar.getInstance();
            if (now.compareTo(soon) > 0)    // if time now is greater than time when meeting occurred
                wait = false;

        }

        return pastMeetingId;

    }

    /**
     * <code>checkChronologyOfListPast</code> internal method
     * <p>
     *     internal test method to check chronology of collection of PastMeetings
     *     @param meetings List of meetings to check order on
     *     @return true if list chronologically ordered
     * </p>
     */
    private boolean checkChronologyOfListPast(List<PastMeeting> meetings) {

        // check chronology of given meeting collection
        PastMeeting prevMeeting = meetings.get(0);          // get first meeting
        boolean before = true;                          // initialise check toggle

        for (int i = 1; i < meetings.size(); i++) {     // iterate through meeting list
            PastMeeting curMeeting = meetings.get(i);
            if (curMeeting.getDate().compareTo(prevMeeting.getDate()) >= 0) {    // compare dates
                // meetings in order
            } else {
                before = false; // meetings not in order
            }
            prevMeeting = curMeeting;   // reset previous meeting for next iteration
        }

        return before;

    }

    /**
     * <code>checkChronologyOfList</code> internal method
     * <p>
     *      internal test method to check chronology of collection of Meetings
     *      @param meetings List of meetings to check order on
     *      @return true if list chronologically ordered
     * </p>
     */
    private boolean checkChronologyOfList(List<Meeting> meetings) {

        // check chronology of given meeting collection
        Meeting prevMeeting = meetings.get(0);          // get first meeting
        boolean before = true;                          // initialise check toggle

        for (int i = 1; i < meetings.size(); i++) {     // iterate through meeting list
            Meeting curMeeting = meetings.get(i);
            if (curMeeting.getDate().compareTo(prevMeeting.getDate()) >= 0) {    // compare dates
                // meetings in order
            } else {
                before = false; // meetings not in order
            }
            prevMeeting = curMeeting;   // reset previous meeting for next iteration
        }

        return before;

    }

    /**
     * <code>generateUniqueContactForMeetings()</code> internal method
     * <p>
     *     This method creates a new contact a new contact and adds it to the contact manager. It then finds the contact
     *     by the unique notes passed to the method, and retrieves the id of the contact. This id is then returned.
     *
     *     Between the returned id and the contact notes, any tests methods will be able to verify unique contacts
     *     and use them to search for meetings using the contact manager api.
     *
     *     Uniqueness is assumed when the string is provided. The string cannot be generated within this method as tests
     *     will also need this string for further validation in tests.
     * </p>
     *
     * @param notes a unique string to be provided, used for searching for the contact
     * @return id of the new contact to be used to search for meetings
     */
    private int generateUniqueContactForMeetings(String notes) {

        // create new contact in contact manager
        String name = "FINDER";                     // with default name
        contactManager.addNewContact(name, notes);  // and unique notes to be identified by

        // return value, by default zero but assigned to contact id when found
        int ret = 0;

        // find all contacts with FINDER name
        Set<Contact> cs = contactManager.getContacts(name);

        // loop through collection of contacts and location contact based on unique notes string
        for (Contact c : cs) {
            if (c.getNotes().equals(notes))     // check contact has the unique notes
                ret = c.getId();                // get the id of the contact that has the unique notes
        }

        // return the contact id
        return ret;
    }

}