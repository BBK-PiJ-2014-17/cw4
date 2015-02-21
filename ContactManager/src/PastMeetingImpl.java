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

    @Override
    public String getNotes() {
        return null;
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public Calendar getDate() {
        return null;
    }

    @Override
    public Set<Contact> getContacts() {
        return null;
    }
}
