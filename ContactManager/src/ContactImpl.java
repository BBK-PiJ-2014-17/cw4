/**
 * Created by Basil on 15/02/2015.
 *
 * An implementation of the Contact Implementation
 */
public class ContactImpl implements Contact {

    // class variables
    private int contactId;
    private String contactName, contactNotes;

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

    }


}
