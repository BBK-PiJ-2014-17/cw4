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
import java.io.IOException;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Date;

/**
 * <h1>ContactManager Test Module</h1>
 * <p>
 *      The ContactManagerTest module implements a number of unit tests for the ContactManager class
 * </p>
 * <p>
 *     The following tests are covered, see links for details.
 * <ul>
 * <li>1. <code>addFutureMeetings()</code> main {@link #testAddFutureMeeting() testAddFutureMeeting main}</li>
 * <li>2. <code>addFutureMeeting() IllegalArgumentException</code> {@link #testAddFutureMeeting() testAddFutureMeeting IllegalArgumentException}</li>
 * <li>3. <code>getPastMeetings</code> main {@link #testGetPastMeeting() testGetPastMeeting main}</li>
 *
 * </ul></p>
 *
 * @author Basil Mason
 * @version 1.0.1
 * @since 07/02/2015
 */
public class ContactManagerTest {

    // set general variables for use in any test
    private ContactManager contactManager, contactManagerWithFile, contactManagerWithoutFile;              // the contact manager object to test
    private Contact basil, rebecca, unknown, finder;    // individual contacts to test
    private String basilString, rebeccaString, unknownString, finderString;                        // search string to find past meetings by contact
    private Set<Contact> contacts;                      // a collection of contacts for meetings
    private Calendar past, future;                      // dates for past and future meetings
    SimpleDateFormat sdf;

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
     * <code>testAddFutureMeeting()</code> main test
     * <p>
     *     This method tests the contact manager's {@link ContactManager#addFutureMeeting addFutureMeeting} method and its
     *     main functionality. The method should add a meeting to to be held in future to the contact manager's internal
     *     list of meetings. This meeting should also receive a unique id.
     * </p>
     */
    @Ignore
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
     * <code>testAddFutureMeeting() IllegalArgumentException</code> test
     * <p>
     *     This test attempts to create a future meeting with a past date, and a future meeting with
     *     contacts unknown to the contact manager. In both cases, a IllegalArgumentException should
     *     be thrown.
     * </p>
     */
    @Ignore
    public void testAddFutureMeetingThrowsIllegalArgumentException() {

        // check future meeting create with past date
        thrown.expect(IllegalArgumentException.class);      // expect invalid argument exception
        contactManager.addFutureMeeting(contacts, past);    // due to meeting in past

        // check future meeting created with contact unknown to contact manager
        contacts.add(new ContactImpl("Unknown Contact"));   // add contact unknown to contact manager to set of contacts for meeting
        thrown.expect(IllegalArgumentException.class);      // expect invalid argument exception
        contactManager.addFutureMeeting(contacts, future);  // due to unknown contact

    }

    /**
     * <code>testGetPastMeeting()</code> main test
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
     * getPastMeeting Tests
     * Required tests:
     *      - get past meeting by id
     *      - check PastMeeting returned with expected id
     *      - check null is returned if no meeting present with that id
     *      - check for IllegalArgumentException if meeting set in the future
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

    @Ignore           // TESTED
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
    @Ignore           // TESTED
    public void testGetFutureMeeting() throws Exception {

        // add future meeting and get ID
        int futureMeetingId = contactManager.addFutureMeeting(contacts, future);

        // test IDs the same
        assertEquals(futureMeetingId, contactManager.getFutureMeeting(futureMeetingId).getId());

        // test null returned if meeting does not exist
        assertTrue(contactManager.getFutureMeeting(99999) == null);

    }

    @Ignore           // TESTED
    public void testGetFutureMeetingThrowsIllegalArgumentException() {

        int futureMeetingId;

        // method 1, setup future meeting and wait until in past
        futureMeetingId = setupPastMeeting(contacts);       // setup past meeting
        thrown.expect(IllegalArgumentException.class);      // expect invalid argument exception
        contactManager.getFutureMeeting(futureMeetingId);   // due to meeting in past

        // method 2, setup new past meeting directly, with unique contact to search by
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String n1 = sdf.format(new Date()).toString();
        int c1 = generateUniqueContactForMeetings(n1);
        Contact c = (Contact) contactManager.getContacts(c1).toArray()[0];

        Set<Contact> cs = new HashSet<Contact>();
        cs.add(c);

        contactManager.addNewPastMeeting(cs, past, n1);

        List<PastMeeting> pms = contactManager.getPastMeetingList(c);

        for (PastMeeting m : pms) {

            futureMeetingId = m.getId();                                           // get id of past meeting
            thrown.expect(IllegalArgumentException.class);                          // expect invalid argument exception
            contactManager.getFutureMeeting(futureMeetingId);                       // due to meeting in past
        }

    }

    /**
     * getMeeting Tests
     * Required tests:
     *      - get meeting by id
     *      - check meeting returned with expected id
     *      - check null is returned if no meeting present with that id
     *      - check it works for past and future meetings
     */
    @Ignore             // TESTED
    public void testGetMeeting() throws Exception {

        int meetingId;

        // 1. get future meeting
        meetingId = contactManager.addFutureMeeting(contacts, future);          // future meeting
        assertEquals(meetingId, contactManager.getMeeting(meetingId).getId());  // test get meeting

        // 2. get future-turned-past meeting
        meetingId = setupPastMeeting(contacts);                                         // future meeting turned past meeting
        assertEquals(meetingId, contactManager.getMeeting(meetingId).getId());  // test get meeting

        // 3. get direct past meeting
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String n1 = sdf.format(new Date()).toString();
        int c1 = generateUniqueContactForMeetings(n1);
        Contact c = (Contact) contactManager.getContacts(c1).toArray()[0];

        Set<Contact> cs = new HashSet<Contact>();
        cs.add(c);

        contactManager.addNewPastMeeting(cs, past, n1);

        List<PastMeeting> pms = contactManager.getPastMeetingList(c);

        for (PastMeeting m : pms) {

            meetingId = m.getId();                                           // get id of past meeting
            assertEquals(meetingId, contactManager.getMeeting(meetingId).getId());
        }

        // 4. test null returned if meeting does not exist
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
    @Ignore             // TESTED
    public void testGetFutureMeetingListByContact() throws Exception {

        //String newName = "New Name";
        //contactManager.addNewContact(newName, "No notes");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String n1 = sdf.format(new Date()).toString();

        Set<Contact> cs = contactManager.getContacts(generateUniqueContactForMeetings(n1));
        Contact n = (Contact) cs.toArray()[0];

        assertTrue(contactManager.getFutureMeetingList(n).size() == 0);             // expect empty list for this contact

        int meeting1Id = contactManager.addFutureMeeting(cs, future);         // add meeting with basil
        future.add(Calendar.DAY_OF_MONTH, +1);                                      // move date
        int meeting2Id = contactManager.addFutureMeeting(cs, future);         // add meeting with basil
        future.add(Calendar.DAY_OF_MONTH, +1);                                      // move date
        int meeting3Id = contactManager.addFutureMeeting(cs, future);         // add meeting with basil
        future.add(Calendar.DAY_OF_MONTH, +1);                                      // move date
        //Set<Contact> otherContacts = new HashSet<Contact>();                        // setup contact set without basil
        //otherContacts.add(finder);
        int meeting4Id = contactManager.addFutureMeeting(contacts, future);    // add meeting without basil
        int meeting5Id = setupPastMeeting(cs);                                // add past meeting with basil

        List<Meeting> meetings = contactManager.getFutureMeetingList(n);        // get future meeting list

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

        //assertTrue(checkChronologyOfList(meetings)); // confirm all meetings were in date order

    }

    @Ignore               // TESTED
    public void testGetFutureMeetingListByContactThrowsIllegalArgumentException() {

        thrown.expect(IllegalArgumentException.class);      // expect invalid argument exception
        contactManager.getFutureMeetingList(new ContactImpl(99999, "Anon"));       // due to unknown contact

    }

    @Ignore         // TESTED
    public void testGetFutureMeetingListByDate() throws Exception {

        future.add(Calendar.DAY_OF_MONTH, +11);
        assertTrue(contactManager.getFutureMeetingList(future).size() == 0);    // no meetings added, expect empty list

        int meeting1Id = contactManager.addFutureMeeting(contacts, future);     // add meeting on future date
        int meeting2Id = contactManager.addFutureMeeting(contacts, future);     // add meeting on future date

        Calendar otherDate = Calendar.getInstance();
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

        //assertTrue(checkChronologyOfList(meetings)); // confirm all meetings were in date order

    }

    @Ignore
    public void testGetFutureMeetingListChronology() throws Exception {

        Calendar date1 = Calendar.getInstance();
        date1.add(Calendar.DAY_OF_MONTH, +100);

        Calendar date2 = Calendar.getInstance();
        date2.add(Calendar.DAY_OF_MONTH, +50);

        contactManager.addFutureMeeting(contacts, date1);
        contactManager.addFutureMeeting(contacts, date2);

        assertTrue(checkChronologyOfList(contactManager.getFutureMeetingList((Contact) contacts.toArray()[0])));

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
    @Ignore         // TESTED (needs improving) --  retest
    public void testGetPastMeetingList() throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String n1 = sdf.format(new Date()).toString();

        Set<Contact> cs = contactManager.getContacts(generateUniqueContactForMeetings(n1));
        Contact n = (Contact) cs.toArray()[0];

        PastMeeting pm;

        assertTrue(contactManager.getPastMeetingList(n).size() == 0);      // no meetings added, expect empty list

        contactManager.addNewPastMeeting(cs, past, "");                   // add new past meeting directly
        pm = contactManager.getPastMeetingList(n).get(0);                   // return the past meeting based on basil contact
        int meeting1Id = pm.getId();                                            // get id of past meeting

        //int meeting2Id = setupPastMeeting(cs);                            // setup future-turned-past meeting with basil
        //int meeting3Id = setupPastMeeting(cs);                            // setup future-turned-past meeting with basil

        //Set<Contact> otherContacts = new HashSet<Contact>();                    // setup contact collection not including basil
        //otherContacts.add(finder);

        String n2 = sdf.format(new Date()).toString();

        Set<Contact> cs2 = contactManager.getContacts(generateUniqueContactForMeetings(n2));
        Contact contact2 = (Contact) cs2.toArray()[0];

        contactManager.addNewPastMeeting(cs2, past, "");              // add new past meeting directly, without basil
        pm = contactManager.getPastMeetingList(contact2).get(0);                  // return the past meeting based on finder contact
        int meeting4Id = pm.getId();                                            // get id of past meeting

        //int meeting5Id = setupPastMeeting(contacts);                       // setup future-turned-past meeting without basil
        int meeting6Id = contactManager.addFutureMeeting(contacts, future);     // add future meeting

        List<PastMeeting> meetings = contactManager.getPastMeetingList(n);  // get meetings

        // check list contains expected meetings
        PastMeeting meeting1 = (PastMeeting) contactManager.getMeeting(meeting1Id); // direct past meeting with basil
        //PastMeeting meeting2 = (PastMeeting) contactManager.getMeeting(meeting2Id); // future-turned-past meeting with basil
        //PastMeeting meeting3 = (PastMeeting) contactManager.getMeeting(meeting3Id); // future-turned-past meeting with basil
        PastMeeting meeting4 = (PastMeeting) contactManager.getMeeting(meeting4Id); // direct past meeting without basil
        //PastMeeting meeting5 = (PastMeeting) contactManager.getMeeting(meeting5Id); // future-turned-past meeting without basil
        FutureMeeting meeting6 = (FutureMeeting) contactManager.getMeeting(meeting6Id); // future meeting with basil

        Set<PastMeeting> expectedMeetings = new HashSet<PastMeeting>(); // expect only 3 of 6 meetings returned
        expectedMeetings.add(meeting1);
        //expectedMeetings.add(meeting2);
        //expectedMeetings.add(meeting3);

        Set<Matcher<PastMeeting>> matcher = new HashSet<Matcher<PastMeeting>>();    // setup hamcrest matcher
        for(PastMeeting m: expectedMeetings)                                        // that checks for the property values of each Meeting
            matcher.add(new SamePropertyValuesAs(m));
        for (Matcher<PastMeeting> mf : matcher)                                     // confirm that expected meetings are in returned collection
            assertThat(meetings, hasItem(mf));

        assertThat(meetings, IsCollectionWithSize.hasSize(expectedMeetings.size()));    // confirm size of returned collection
        assertThat(meetings, not(hasItem(meeting4)));                                   // confirm return does not contain meeting without basil
        //assertThat(meetings, not(hasItem(meeting5)));                                   // confirm return does not contain meeting without basil
        //assertThat(meetings, not(hasItem(meeting6)));                                   // confirm return does not contain meeting future meeting

        //assertTrue(checkChronologyOfListPast(meetings)); // confirm all meetings were in date order

    }

    @Ignore             // TESTED
    public void testGetPastMeetingListThrowsIllegalArgumentException() throws Exception {

        thrown.expect(IllegalArgumentException.class);      // expect invalid argument exception
        contactManager.getPastMeetingList(new ContactImpl(9999, "Anon"));         // due to unknown contact

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
    @Ignore             // TESTED
    public void testAddNewPastMeeting() throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String n1 = sdf.format(new Date()).toString();
        String pastMeetingNotes = "blah blah" + n1;  // meeting notes to check

        int c1 = generateUniqueContactForMeetings(n1);
        Contact c = (Contact) contactManager.getContacts(c1).toArray()[0];

        Set<Contact> cs = new HashSet<Contact>();
        cs.add(c);

        contactManager.addNewPastMeeting(cs, past, pastMeetingNotes);     // add new past meeting

        List<PastMeeting> pms = contactManager.getPastMeetingList(c);

        for (PastMeeting pm : pms) {

            String notes = pm.getNotes();

            assertTrue(pastMeetingNotes.equals(notes));
        }

    }

    @Ignore               // TESTED
    public void testAddNewPastMeetingThrowsIllegalArgumentException() throws Exception {

        //contacts.add(new ContactImpl(99999, "Anon"));

        Set<Contact> cs = new HashSet<Contact>();
        cs.add(new ContactImpl(99999, "Anon"));

        thrown.expect(IllegalArgumentException.class);                // expect illegal argument exception
        contactManager.addNewPastMeeting(cs, past, "");         // due to unknown contact

        thrown.expect(IllegalArgumentException.class);                          // expect illegal argument exception
        contactManager.addNewPastMeeting(new HashSet<Contact>(), past, "");     // due to empty contact collection

    }

    @Ignore               // TESTED
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
    @Ignore           // TESTED -- retest
    public void testAddMeetingNotes() throws Exception {

        PastMeeting pm;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String n1 = sdf.format(new Date()).toString();
        String pastMeetingNotes = "blah blah" + n1;  // meeting notes to check

        // method 1, setup future meeting and wait until it is a past meeting
        // then add notes by id

        int c = generateUniqueContactForMeetings("blah blah" + n1);

        Set<Contact> cs = contactManager.getContacts(c);

        int pastMeetingId = setupPastMeeting(cs);                         // setup future-turned-past meeting
        pm = contactManager.getPastMeeting(pastMeetingId);                      // get past meeting by id
        contactManager.addMeetingNotes(pm.getId(), pastMeetingNotes);           // add notes

        List<PastMeeting> pms = contactManager.getPastMeetingList((Contact) contactManager.getContacts(c).toArray()[0]);

        for (PastMeeting m : pms) {

            assertTrue(pastMeetingNotes.equals(m.getNotes()));                     // test meeting notes match
        }

        // method 2, setup new past meeting directly, with unique contact to search by

        String n2 = sdf.format(new Date()).toString();
        int c2 = generateUniqueContactForMeetings(n1);
        Contact contact2 = (Contact) contactManager.getContacts(c2).toArray()[0];

        Set<Contact> cs2 = new HashSet<Contact>();
        cs2.add(contact2);

        contactManager.addNewPastMeeting(cs2, past, n2);

        List<PastMeeting> pms2 = contactManager.getPastMeetingList(contact2);

        for (PastMeeting m : pms2) {

            String notes = m.getNotes();

            assertTrue(n2.equals(notes));

        }

    }

    @Ignore             // TESTED -- retest
    public void testAddMeetingNotesThrowsIllegalArgumentException() {

        int meetingId = 12345678;       // meeting id does not exist
        String notes = "blah blah.";

        thrown.expect(IllegalArgumentException.class);      // expect illegal argument exception
        contactManager.addMeetingNotes(meetingId, notes);   // due to non-existent meeting

    }

    

    @Ignore             // TESTED
    public void testAddMeetingNotesThrowsNullPointerException() {

        //contacts.add(finder);                                                   // add finder contact to contacts for meeting
        //contactManager.addNewPastMeeting(contacts, past, "");                   // add new past meeting directly
        //PastMeeting pm = contactManager.getPastMeetingList(finder).get(0);      // return the past meeting based on finder contact

        int pmid = setupPastMeeting(contacts);

        thrown.expect(NullPointerException.class);          // expect null point exception
        contactManager.addMeetingNotes(pmid, null);   // when notes are null

    }

    /**
     * addNewContact Tests
     * Required tests:
     *      - add new contact and check name and notes
     *      - check for NullPointerException if any input is null
     */
    @Ignore           // TESTED -- retest
    public void testAddNewContact() throws Exception {

        //String newContactName = "Mr New Contact";                           // setup new contact
        //String newContactNotes = "Mr New Contact's notes";
        //contactManager.addNewContact(newContactName, newContactNotes);      // add new contact

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String n1 = sdf.format(new Date()).toString();

        int contactId = generateUniqueContactForMeetings(n1);

        // retrieve all contacts based on name
        Set<Contact> cs = contactManager.getContacts(contactId);

        int count = 0;

        // for each contact, check notes.
        for (Contact c : cs) {
            if (c.getNotes().equals(n1))   // if notes equal new contact notes, increment count
                count++;
        }

        assertTrue(count == 1);     // expect 1 contact with new contact notes

    }

    @Ignore           // TESTED
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
    @Ignore           // TESTED
    public void testGetContactsById() throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String n1 = sdf.format(new Date()).toString();
        int contact1 = generateUniqueContactForMeetings(n1);
        String n2 = sdf.format(new Date()).toString();
        int contact2 = generateUniqueContactForMeetings(n2);

        Set<Contact> cs = contactManager.getContacts(contact1, contact2);       // get two known contacts by id
        for (Contact c : cs) {
            assertTrue(c.getNotes().equals(n1) || c.getNotes().equals(n2));   // check id returns name that exists
        }

    }

    @Ignore             // TESTED
    public void testGetContactsByIdThrowsIllegalArgumentException() throws Exception {

        thrown.expect(IllegalArgumentException.class);  // expect illegal argument exception
        contactManager.getContacts(12345678);           // if id does not exist


    }

    @Ignore           // TESTED
    public void testGetContactsByName() throws Exception {

        String contactName = "New Contact";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String n1 = sdf.format(new Date()).toString();

        String contactNotes1 = "New Notes 1" + n1;
        String contactNotes2 = "New Notes 2" + n1;
        String contactNotes3 = "New Notes 3" + n1;

        contactManager.addNewContact(contactName, contactNotes1);
        contactManager.addNewContact(contactName, contactNotes2);
        contactManager.addNewContact(contactName, contactNotes3);

        String[] contactNotes = {contactNotes1, contactNotes2, contactNotes3};  // array of expected contact notes
        Set<Contact> cs = contactManager.getContacts(contactName);  // get contacts by name

        int count = 0;
        for (Contact c : cs) {              // for each contact
            for (String s : contactNotes) {
                if (c.getNotes().equals(s))        // check id corresponds to an expected id
                    count++;
            }

        }

        assertTrue(count == 3); // count of contacts by name should be 3

    }

    @Ignore             // TESTED
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
    @Ignore            // TESTED
    public void testFlush() throws Exception {

        //String contactName = "Test Contact";
        //String contactNotes = "Test Notes";

        int meetingId = contactManager.addFutureMeeting(contacts, future);  // add meeting to contact manager
        //contactManager.addNewContact(contactName, contactNotes);            // add contact to contact manager

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String n1 = sdf.format(new Date()).toString();

        int contactId = generateUniqueContactForMeetings(n1);

        contactManager.flush();     // save contents to file

        ContactManager newContactManager = new ContactManagerImpl();    // create new contact manager with saved file

        assertTrue(newContactManager.getMeeting(meetingId) != null);    // check meeting exists in new contact manager

        Set<Contact> cs = newContactManager.getContacts(contactId);   // get contacts by name
        assertTrue(cs.size() == 1);     // check contact exists

    }

    @Ignore           // TESTED
    public void testAddMeetingNotesThrowsIllegalStateException() {

        // add meeting in future
        int meetingId = contactManager.addFutureMeeting(contacts, future);
        String notes = "blah blah.";

        thrown.expect(IllegalStateException.class);         // expect illegal state exception
        contactManager.addMeetingNotes(meetingId, notes);   // due to meeting in future

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

    private Set<Contact> generateContactSetForMeeting() {

        Set<Contact> ret = new HashSet<Contact>();

        //ret.add(contactManager.getContacts(generateUniqueContactForMeetings()))

        return ret;

    }

}