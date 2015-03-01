/**
 * Created by Basil on 15/02/2015.
 *
 * An implementation of the Contact Implementation
 */
public class ContactImpl implements Contact {

    // class variables
    private int contactId;
    private String contactName, contactNotes;
    public static int UNIQUE_ID = 0;

    // constructors

    public ContactImpl(String contactName) {

        UNIQUE_ID++;

        this(UNIQUE_ID, contactName);

    }

    public ContactImpl(int contactId, String contactName) {

        // by default notes is an empty string
        this(contactId, contactName, "");

    }

    public ContactImpl(int contactId, String contactName, String contactNotes) {
        this.contactId = contactId;
        this.contactName = contactName;
        this.contactNotes = contactNotes;
    }

    /** {@inheritDoc}
     *
     */
    public int getId() {
        return contactId;
    }

    /** {@inheritDoc}
     *
     */
    @Override
    public String getName() {
        return contactName;
    }

    /** {@inheritDoc}
     *
     */
    @Override
    public String getNotes() {
        return contactNotes;
    }

    /** {@inheritDoc}
     *
     */
    @Override
    public void addNotes(String note) {
        contactNotes = contactNotes + note;
    }

}
