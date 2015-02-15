import java.util.Calendar;
import java.util.Set;

/**
 * Created by Basil on 15/02/2015.
 *
 * An implementation of the Meeting interface
 */

public class MeetingImpl implements Meeting {

    // class variables

    private int meetingId;
    private Calendar meetingDate;
    private Set<Contact> meetingContacts;

    // constructors

    public MeetingImpl(int meetingId, Calendar meetingDate, Set<Contact> meetingContacts) {
        this.meetingId = meetingId;
        this.meetingDate = meetingDate;
        this.meetingContacts = meetingContacts;
    }

    /** {@inheritDoc}
     *
     */
    @Override
    public int getId() {
        return meetingId;
    }

    /** {@inheritDoc}
     *
     */
    @Override
    public Calendar getDate() {
        return meetingDate;
    }

    /** {@inheritDoc}
     *
     */
    @Override
    public Set<Contact> getContacts() {
        return meetingContacts;
    }
}
