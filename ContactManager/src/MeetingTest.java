import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Set;
import java.util.HashSet;

import org.hamcrest.Matcher;
import static org.hamcrest.Matchers.hasItem;
import org.hamcrest.beans.SamePropertyValuesAs;
import org.hamcrest.collection.IsCollectionWithSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class MeetingTest {

    private Meeting m, mUnique1, mUnique2;
    private int expectedId;
    private Calendar expectedDate;
    private Set<Contact> expectedContacts;

    @Before
    public void setUp() throws Exception {

        expectedId = 1;
        expectedDate = new GregorianCalendar(2015,03,23,14,30);
        expectedContacts = new HashSet<Contact>();
        expectedContacts.add(new ContactImpl(1, "Basil Mason"));
        expectedContacts.add(new ContactImpl(2, "Rebecca White"));

        m = new MeetingImpl(expectedId, expectedDate, expectedContacts);
        mUnique1 = new MeetingImpl(expectedDate, expectedContacts);
        mUnique2 = new MeetingImpl(expectedDate, expectedContacts);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testGetId() throws Exception {

        assertEquals(expectedId, m.getId());
        assertTrue(mUnique1.getId() != mUnique2.getId());

    }

    @Test
    public void testGetDate() throws Exception {

        assertEquals(expectedDate, m.getDate());

    }

    @Test
    public void testContactsContainsAtLeastOneContact() throws Exception {

        assertTrue(m.getContacts().size() >= 1);

    }

    @Test
    public void testGetContacts() throws Exception {

        Set<Contact> ret = m.getContacts();

        //Set<Contact> ret = new HashSet<Contact>();    // fail case
        //ret.add(new ContactImpl(1, "Basil blahblah"));
        //ret.add(new ContactImpl(2, "Rebecca blahblah"));

        Set<Matcher<Contact>> matcher = new HashSet<Matcher<Contact>>();

        // create a matcher that checks for the property values of each Contact
        for(Contact c: expectedContacts)
            matcher.add(new SamePropertyValuesAs(c));

        // check that each matcher matches something in the list
        for (Matcher<Contact> mf : matcher)
            assertThat(ret, hasItem(mf));

        // check that list sizes match
        assertThat(ret, IsCollectionWithSize.hasSize(expectedContacts.size()));

    }
}