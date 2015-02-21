import java.util.Calendar;
import java.util.Set;

/**
 * Created by Basil on 21/02/2015.
 *
 *  An implementation of the Past Meeting interface
 */
public class PastMeetingImpl implements PastMeeting {

    // class variables

    private int meetingId;
    private Calendar meetingDate;
    private Set<Contact> meetingContacts;
    private String meetingNotes;

    // constructors

    // fully specified constructor
    public PastMeetingImpl(int meetingId, Calendar meetingDate, Set<Contact> meetingContacts, String meetingNotes) {
        this.meetingId = meetingId;
        this.meetingDate = meetingDate;
        this.meetingContacts = meetingContacts;
        this.meetingNotes = meetingNotes;
    }

    // copy constructor
    public PastMeetingImpl(Meeting meeting, String meetingNotes) {
        this.meetingId = meeting.getId();
        this.meetingDate = meeting.getDate();
        this.meetingContacts = meeting.getContacts();
        this.meetingNotes = meetingNotes;
    }

    /** {@inheritDoc}
     *
     */
    @Override
    public String getNotes() {
        return meetingNotes;
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
