import java.util.Calendar;
import java.util.Set;

/**
 * Created by Basil on 01/03/2015.
 *
 * Implementation of FutureMeeting interface
 */
public class FutureMeetingImpl implements FutureMeeting {

    private Meeting m;  // internal meeting variable

    // constructor

    public FutureMeetingImpl(Calendar meetingDate, Set<Contact> meetingContacts) {

        m = new MeetingImpl(meetingDate, meetingContacts);

    }

    @Override
    public int getId() {
        return m.getId();
    }

    @Override
    public Calendar getDate() {
        return m.getDate();
    }

    @Override
    public Set<Contact> getContacts() {
        return m.getContacts();
    }
}
