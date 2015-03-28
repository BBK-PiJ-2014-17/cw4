// file read, xml and java utility libraries and methods
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 * Created by Basil on 07/03/2015.
 *
 * Implementation of ContactManager interface
 *
 * A contact manager object for maintaining meetings and contacts. The meetings and contacts are stored internally in
 * a List and Set, respectively. All meetings, Past and Future are stored as Meetings only and cast when required.
 *
 * IDs are unique for all contacts and meetings. The ID generation is centralised within the contact manager and the
 * contact and meeting classes are considered dependent on the contact manager, not separate entities. See internal
 * method {@link #uniqueId() uniqueId()}.
 *
 * All data is stored offline in an xml file, contacts.txt. If a file is not present at initialisation, one is created.
 * Otherwise, the existing file is read and used to populate the internal data structures. The file is written to by
 * a call to the {@link #flush() flush} method.
 *
 * The xml format has three sections: manager, contacts and, meetings.
 *
 *      manager:    stores to current unique ID seed
 *      contacts:   stores details of the contacts
 *      meetings:   stores details of the meetings. Each meeting also has a list of contacts. Within each meeting,
 *                  only a reference to the contact is made by id.
 *
 * *** Example XML ***
 *
 * <contactmanager>
 *      <manager>
 *          <CM_ID>103</CM_ID>
 *      </manager>
 *      <contacts>
 *          <contact id="34">
 *              <name>New Contact</name>
 *              <notes>Met 2015-03-23</notes>
 *          </contact>
 *      </contacts>
 *      <meetings>
 *          <meeting id="96" type="past">
 *              <date>22-03-2015</date>
 *              <meetingContacts>
 *                  <meetingContact id="95"/>
 *                  <meetingContact id="43"/>
 *              </meetingContacts>
 *              <notes>Agenda</notes>
 *          </meeting>
 *      </meetings>
 * </contactmanager>
 *
 */
public class ContactManagerImpl implements ContactManager {

    /* V A R I A B L E S */

    private final String filePath = "contacts.txt"; // contact manager output file
    private Set<Contact> contacts;                  // collection of contacts
    private List<? super Meeting> meetings;         // list of meetings (Past or Future)
    private static int CM_ID = 0;                   // unique ID for meeting and contact creation
    SimpleDateFormat format;                        // format for dates in file

    /* E N U M S */

    // an internal string enum for meeting type checking
    private enum MeetingType {
        PAST ("past"),
        FUTURE ("future");
        private final String type;
        private MeetingType(final String type) {
            this.type = type;
        }
        @Override
        public String toString() {
            return type;
        }
    }

    /* P U B L I C   S T A T I C */

    // a static method to generate unique IDs
    // called externally by meetings and contacts upon creation
    public static int uniqueId() {
        CM_ID++;
        return CM_ID;
    }

    /* C O N S T R U C T O R S */

    /**
     * <code>ContactManagerImpl()</code> constructor
     * <p>
     *     The default constructor prepares the internal collections of meetings and contacts. If a contacts.txt file
     *     is present, the data is read in. Otherwise, the internal collections are initialised as empty.
     * </p>
     */
    public ContactManagerImpl() {

        // xml file read setup
        File contactsXml = new File(filePath);                      // set xml file path
        NodeList managerNodes, contactNodes,
                    meetingNodes, meetingContactNodes;              // node lists for xml read
        DocumentBuilderFactory dbFactory;                           // for xml output
        DocumentBuilder dBuilder;                                   // for xml output
        Document doc;                                               // for xml output

        // variable initialisation
        contacts = new HashSet<Contact>();                          // initialise contacts set
        meetings = new ArrayList<Meeting>();                        // initialise meetings list
        format = new SimpleDateFormat("dd-MM-yyyy");                // initialise date format

        // file read
        try {

            // if file doesn't already exist
            if(!contactsXml.exists()) {
                contactsXml.createNewFile();        // create the file
            } else {                                // else read file

                // xml DOM builder
                dbFactory = DocumentBuilderFactory.newInstance();   // setup xml read
                dBuilder = dbFactory.newDocumentBuilder();          // setup xml read
                doc = dBuilder.parse(contactsXml);                  // read in file
                doc.getDocumentElement().normalize();               // normalise xml

                // 1. Manager section - contains unique ID
                // read in current unique ID
                managerNodes = doc.getElementsByTagName("manager");                 // get manager section
                CM_ID = Integer.parseInt(managerNodes.item(0).getTextContent());    // only one node expected
                                                                                    // containing unique ID seed
                // 2. Contacts section - contains all known contacts
                // read in contacts
                contactNodes = doc.getElementsByTagName("contact");     // get list of contact nodes
                for (int i = 0; i < contactNodes.getLength(); i++) {

                    Node nNode = contactNodes.item(i);      // current node

                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {     // read only element type nodes

                        Element eElement = (Element) nNode; // current element node

                        // create contact based on node
                        Contact c = new ContactImpl(Integer.parseInt(eElement.getAttribute("id")),
                                eElement.getElementsByTagName("name").item(0).getTextContent(),
                                eElement.getElementsByTagName("notes").item(0).getTextContent());

                        contacts.add(c);    // add contact to set

                    }
                }

                // 3. Meetings section - contacts all scheduled meetings, past and future
                // read in meetings
                meetingNodes = doc.getElementsByTagName("meeting");     // get list of meeting nodes

                for (int i = 0; i < meetingNodes.getLength(); i++) {    // read each node into meeting list

                    Node nMeeting = meetingNodes.item(i);      // current node

                    if (nMeeting.getNodeType() == Node.ELEMENT_NODE) {     // read only element type nodes

                        Element eElement = (Element) nMeeting; // current element node

                        // 3.1 meeting date from file as calendar object
                        Date xmlDate = format.parse(eElement.getElementsByTagName("date").item(0).getTextContent());    // read date from string
                        Calendar meetingDate = Calendar.getInstance();  // create calendar object
                        meetingDate.setTime(xmlDate);                   // initialise calendar object with date from file

                        // 3.2 meeting notes
                        String meetingNotes = eElement.getElementsByTagName("notes").item(0).getTextContent();

                        // 3.3 meeting contacts collection from file
                        Set<Contact> meetingContacts = new HashSet<Contact>();  // set for current meeting

                        Element mcs = (Element) eElement.getElementsByTagName("meetingContacts").item(0);
                        meetingContactNodes = mcs.getElementsByTagName("meetingContact");  // get list of meeting contact nodes

                        for (int j = 0; j < meetingContactNodes.getLength(); j++) {     // read each node into current meeting contact set

                            Node nMeetingContactNode = meetingContactNodes.item(j);             // current node

                            if (nMeetingContactNode.getNodeType() == Node.ELEMENT_NODE) {       // read only element type nodes

                                Element eMeetingContactElement = (Element) nMeetingContactNode; // current element node

                                int id = Integer.parseInt(eMeetingContactElement.getAttribute("id"));   // get meeting ID

                                Set<Contact> cs = getContacts(id);      // find contact by ID
                                Contact c = (Contact) cs.toArray()[0];  // one contact return expected since only one unique ID searched
                                meetingContacts.add(c);                 // add contact to meeting contacts set

                            }
                        }

                        // determine meeting type and add meeting to meeting list
                        if (eElement.getAttribute("type").equals(MeetingType.PAST.toString())) {

                            PastMeeting m = new PastMeetingImpl(Integer.parseInt(eElement.getAttribute("id")),
                                    meetingDate,
                                    meetingContacts,
                                    meetingNotes);

                            meetings.add(m);

                        } else if (eElement.getAttribute("type").equals(MeetingType.FUTURE.toString())) {

                            FutureMeeting m = new FutureMeetingImpl(Integer.parseInt(eElement.getAttribute("id")),
                                    meetingDate,
                                    meetingContacts);

                            meetings.add(m);

                        }
                    }
                }
            }

        // exception handling
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    /* I N T E R F A C E   M E T H O D S */

    /**
     * <code>addFutureMeeting</code>
     * {@inheritDoc}
     * <p>
     *     After checking that the contacts for the meeting are known to the contact manager
     *     and that the date is in the future, this methods creates a new instance of a future meeting
     *     and adds it to the internal list of meetings
     * </p>
     *
     * @return a unique id
     */
    @Override
    public int addFutureMeeting(Set<Contact> contacts, Calendar date) {

        // check date for meeting is a future date
        Calendar now = Calendar.getInstance();      // get time as at now
        if (now.compareTo(date) > 0)                // compare the argument date against now
            throw new IllegalArgumentException();   // if now is greater than the meeting date, an exception is thrown

        // check contacts are known to contact manager
        if (!checkContactsExist(contacts))          // call internal method to verify contacts
            throw new IllegalArgumentException();   // if false returned, throw exception

        // if all exceptions are passed, generate a unique id for the meeting
        int id = uniqueId();    // call internal method to generate unique id

        // create new instance of future meeting and add to internal list of meetings
        FutureMeeting fm = new FutureMeetingImpl(id, date, contacts);
        meetings.add(fm);

        // return the id of the new future meeting
        return id;
    }

    /**
     * <code>getPastMeeting</code>
     * {@inheritDoc}
     * <p>
     *     This method returns a past meeting given an id. If the id does not exist, <code>null</code>
     *     is returned. Or, if the id relates to a future meeting, an <code>IllegalArgumentException</code>
     *     exception is thrown.
     *
     *     A check is made for all future meetings that have become past meetings since the last update to the internal
     *     list of meetings. Any future meetings whose dates is now in the past are updated accordingly. See internal
     *     method {@link #updateMeetingTypes() updateMeetingTypes}.
     * </p>
     *
     * @return past meeting based on unique id, or null
     */
    @Override
    public PastMeeting getPastMeeting(int id) {

        // past meeting to be returned, initialised to null
        PastMeeting ret = null;

        // call internal method to update any future meetings that are now in the past
        updateMeetingTypes();

        // scan internal list of meetings
        for (Object m : meetings) {

            // if the meeting is a past meeting, check the id and return if found
            if (m instanceof PastMeeting) {

                PastMeeting pm = (PastMeeting) m;   // cast to past meeting to get id
                if (pm.getId() == id)               // check id
                    ret = pm;                       // set return object if meeting found

            // if the meeting is a future meeting, check id and throw exception if found
            } else if (m instanceof FutureMeeting) {

                FutureMeeting fm = (FutureMeeting) m;       // cast to future meeting to get id
                if (fm.getId() == id)                       // check id
                    throw new IllegalArgumentException();   // thrown exception if found

            }

        }

        // return past meeting or null
        return ret;
    }

    /**
     * <code>getFutureMeeting</code>
     * {@inheritDoc}
     * <p>
     *     This method returns a future meeting given an id. If the id does not exist, <code>null</code>
     *     is returned. Or, if the id relates to a past meeting, an <code>IllegalArgumentException</code>
     *     exception is thrown.
     *
     *     A check is made for all future meetings that have become past meetings since the last update to the internal
     *     list of meetings. Any future meetings whose dates is now in the past are updated accordingly. See internal
     *     method {@link #updateMeetingTypes() updateMeetingTypes}.
     * </p>
     *
     * @return future meeting based on unique id, or null
     */
    @Override
    public FutureMeeting getFutureMeeting(int id) {

        // past meeting to be returned, initialised to null
        FutureMeeting ret = null;

        // call internal method to update any future meetings that are now in the past
        updateMeetingTypes();

        // scan internal list of meetings
        for (Object m : meetings) {

            // if the meeting is a future meeting, check the id and return if found
            if (m instanceof FutureMeeting) {

                FutureMeeting fm = (FutureMeeting) m;   // cast to future meeting to get id
                if (fm.getId() == id)                   // check id
                    ret = fm;                           // set return object if meeting found

            // if the meeting is a past meeting, check id and throw exception if found
            } else if (m instanceof PastMeeting) {

                PastMeeting pm = (PastMeeting) m;           // cast to future meeting to get id
                if (pm.getId() == id)                       // check id
                    throw new IllegalArgumentException();   // thrown exception if found

            }

        }

        // return past meeting or null
        return ret;
    }

    /**
     * <code>getMeeting</code>
     * {@inheritDoc}
     * <p>
     *     This method returns a meeting given an id. If the id does not exist, <code>null</code>
     *     is returned.
     *
     *     A check is made for all future meetings that have become past meetings since the last update to the internal
     *     list of meetings. Any future meetings whose dates is now in the past are updated accordingly. See internal
     *     method {@link #updateMeetingTypes() updateMeetingTypes}.
     * </p>
     *
     * @return future meeting based on unique id, or null
     */
    @Override
    public Meeting getMeeting(int id) {

        Meeting ret = null, m;

        updateMeetingTypes();   // update any future meetings

        // scan internal meeting list
        for (Object o : meetings) {

            m = (Meeting) o;        // cast to meeting to get id
            if (m.getId() == id)    // check id
                ret = m;            // set return object if id found

        }

        // return meeting or null
        return ret;
    }

    /**
     * <code>getFutureMeetingList(Contact)</code> by contact
     * {@inheritDoc}
     * <p>
     *     This method returns a list of future meetings given a contact. If no meetings exists, an empty list is
     *     returned.
     *
     *     A check is made for all future meetings that have become past meetings since the last update to the internal
     *     list of meetings. Any future meetings whose dates is now in the past are updated accordingly. See internal
     *     method {@link #updateMeetingTypes() updateMeetingTypes}.
     *
     *     Furthermore, the list of meetings to be returned is sorted into chronological order. See method
     *     {@link #sortMeetingList(List) sortMeetingList}.
     * </p>
     *
     * @return list of future meetings based on contact, or an empty list.
     */
    @Override
    public List<Meeting> getFutureMeetingList(Contact contact) {

        // if the contact is unknown to the contact manager, throw an exception
        if (!checkContactExists(contact))
            throw new IllegalArgumentException();

        // update any future meetings that are now in the past
        updateMeetingTypes();

        // list of meetings to be returned
        List<Meeting> ret = new ArrayList<Meeting>();

        // scan internal list of meetings
        for (Object o : meetings) {

            // if the meeting is a future meeting
            if (o instanceof FutureMeeting) {

                Meeting m = (Meeting) o;            // cast to meeting to access contacts
                Set<Contact> cs = m.getContacts();  // get contacts

                // scan meeting contacts for given contact id
                for (Contact c : cs) {

                    if (contact.getId() == c.getId())   // if the contact is present in this meeting
                        ret.add(m);                     // add the meeting to the return list

                }

            }

        }

        // sort the return list in chronological order
        sortMeetingList(ret);

        // return list of meetings, or an empty list
        return ret;
    }

    /**
     * <code>getFutureMeetingList(Calendar)</code> by date
     * {@inheritDoc}
     * <p>
     *     This method returns a list of future meetings given a date. If no meetings exists, an empty list is
     *     returned.
     *
     *     A check is made for all future meetings that have become past meetings since the last update to the internal
     *     list of meetings. Any future meetings whose dates is now in the past are updated accordingly. See internal
     *     method {@link #updateMeetingTypes() updateMeetingTypes}.
     *
     *     Furthermore, the list of meetings to be returned is sorted into chronological order. See method
     *     {@link #sortMeetingList(List) sortMeetingList}.
     * </p>
     *
     * @return list of future meetings based on contact, or an empty list.
     */
    @Override
    public List<Meeting> getFutureMeetingList(Calendar date) {

        // update any future meetings
        updateMeetingTypes();

        // return list of meetings
        List<Meeting> ret = new ArrayList<Meeting>();

        // scan internal list of meetings
        for (Object o : meetings) {

            Meeting m = (Meeting) o;                // cast to meeting to access date
            if (m.getDate().compareTo(date) == 0)   // if dates are the same
                ret.add(m);                         // add to return list

        }

        // sort return list chronologically
        sortMeetingList(ret);

        // return list of meetings, or empty list
        return ret;

    }

    /**
     * <code>getPastMeetingList(Contact)</code> by contact
     * {@inheritDoc}
     * <p>
     *     This method returns a list of past meetings given a contact. If no meetings exists, an empty list is
     *     returned.
     *
     *     A check is made for all future meetings that have become past meetings since the last update to the internal
     *     list of meetings. Any future meetings whose dates is now in the past are updated accordingly. See internal
     *     method {@link #updateMeetingTypes() updateMeetingTypes}.
     *
     *     Furthermore, the list of meetings to be returned is sorted into chronological order. See method
     *     {@link #sortMeetingList(List) sortMeetingList}.
     * </p>
     *
     * @return list of past meetings based on contact, or an empty list.
     */
    @Override
    public List<PastMeeting> getPastMeetingList(Contact contact) {

        // if the contact is unknown to the contact manager, throw an exception
        if (!checkContactExists(contact))
            throw new IllegalArgumentException();

        // update any future meetings
        updateMeetingTypes();

        // return list of meetings
        List<PastMeeting> ret = new ArrayList<PastMeeting>();

        // scan internal list of meetings
        for (Object o : meetings) {

            // if the meeting is a past meeting
            if (o instanceof PastMeeting) {

                PastMeeting m = (PastMeeting) o;        // cast to meeting to access contacts
                Set<Contact> cs = m.getContacts();      // get contacts

                // scan meeting contacts for given contact id
                for (Contact c : cs) {
                    if (contact.getId() == c.getId())   // if the contact is present in this meeting
                        ret.add(m);                     // add the meeting to the return list
                }

            }

        }

        // sort the return list in chronological order
        sortPastMeetingList(ret);

        // return list of meetings, or an empty list
        return ret;

    }

    /**
     * <code>addNewPastMeeting()</code>
     * {@inheritDoc}
     * <p>
     *     After checking that none of the arguments are null, the contacts are known
     *     to the contact manager, and that the contact collection is not empty,
     *     this method adds a new past meeting to the internal list of meetings.
     * </p>
     */
    @Override
    public void addNewPastMeeting(Set<Contact> contacts, Calendar date, String text) {

        // check none of the arguments are null
        if (contacts == null || date == null || text == null)
            throw new NullPointerException();

        // check the contacts collection is not empty
        if (contacts.isEmpty())
            throw new IllegalArgumentException();

        // check that all contacts are known to the contact manager
        if (!checkContactsExist(contacts))
            throw new IllegalArgumentException();

        // add a new past meeting to the internal list of meetings
        this.meetings.add(new PastMeetingImpl(uniqueId(), date, contacts, text));

    }

    /**
     * <code>addMeetingNotes()</code>
     * {@inheritDoc}
     * <p>
     *     After checking that the notes passed are not null and that the meeting
     *     exists and was in the past, this methods adds notes to a past meeting.
     *
     *     Since there is no API on the meeting class to add notes directly, a
     *     copy constructor is used, creating a new meeting with the details of the
     *     given meeting, but with the notes added.
     * </p>
     */
    @Override
    public void addMeetingNotes(int id, String text) {

        // check that the notes are not null
        if (text == null)
            throw new NullPointerException();

        // check that the meeting exists
        Meeting m = getMeeting(id);
        if (m == null)
            throw new IllegalArgumentException();

        // check that the meeting was in the past
        Calendar now = Calendar.getInstance();
        if (now.compareTo(m.getDate()) < 0)
            throw new IllegalStateException();

        // create a new meeting using the copy constructor
        PastMeeting pm = new PastMeetingImpl(m, text);

        // update the internal list of meetings
        meetings.remove(m);     // remove existing meeting
        meetings.add(pm);       // add new past meeting with notes

    }

    /**
     * <code>addNewContact()</code>
     * {@inheritDoc}
     * <p>
     *     After checking that none of the arguments are null, this method creates
     *     a new contact and adds it to the internal collection of contacts.
     * </p>
     */
    @Override
    public void addNewContact(String name, String notes) {

        // check that the arguments are of null
        if (name == null || notes == null)
            throw new NullPointerException();

        // create a new contact and add them to the collection of contacts
        contacts.add(new ContactImpl(uniqueId(), name, notes));

    }

    /**
     * <code>getContacts()</code> by id(s)
     * {@inheritDoc}
     * <p>
     *     After checking that the requested contacts exists, this method compiles
     *     and returns a set of contacts based on the ids supplied.
     * </p>
     */
    @Override
    public Set<Contact> getContacts(int... ids) {

        // check that each of the given ids exists
        for (int i : ids) {

            // iterate through set of contacts to check for id
            boolean exists = false;         // existence flag
            for (Contact c : contacts) {
                if (i == c.getId())         // if the contact exists
                    exists = true;          // set the flag to true
            }

            // if the contact id was not found in the set of contacts
            if (!exists)
                throw new IllegalArgumentException();   // throw exception

        }

        // return collection
        Set<Contact> ret = new HashSet<Contact>();

        // go through internal set of contacts
        for (Contact c : contacts) {

            // for each id passed to the method
            for (int i : ids) {
                if (i == c.getId())
                    ret.add(c);         // add the contact to the return set
            }

        }

        // return set of contacts
        return ret;
    }

    /**
     * <code>getContacts()</code> by name
     * {@inheritDoc}
     * <p>
     *     After checking that the given name is not null, this method finds all contacts
     *     with that name.
     * </p>
     */
    @Override
    public Set<Contact> getContacts(String name) {

        // check the name is not null
        if (name == null)
            throw new NullPointerException();

        // return set
        Set<Contact> ret = new HashSet<Contact>();

        // iterate thought contacts
        for (Contact c : contacts) {
            if (c.getName().contains(name))     // if the name matches
                ret.add(c);                     // add the contact to the return set
        }

        // return set of contacts with given name
        return ret;
    }

    /**
     * <code>flush()</code>
     * {@inheritDoc}
     * <p>
     *     Save details of contacts, meetings and unique ids to file.
     * </p>
     */
    @Override
    public void flush() {

        // file write setup
        Document output;                                    // document to be output to file
        Element managerRoot, contactRoot, meetingRoot;      // xml elements
        DocumentBuilderFactory docFactory;                  // for xml output
        DocumentBuilder docBuilder;                         // for xml output

        // file write
        try {

            docFactory = DocumentBuilderFactory.newInstance();
            docBuilder = docFactory.newDocumentBuilder();

            // 1. root element - contactmanager
            output = docBuilder.newDocument();
            Element rootElement = output.createElement("contactmanager");
            output.appendChild(rootElement);

            // 2. manager element - manager
            managerRoot = output.createElement("manager");
            rootElement.appendChild(managerRoot);

            // output unique id seed
            Element eId = output.createElement("CM_ID");
            eId.appendChild(output.createTextNode("" + CM_ID));
            managerRoot.appendChild(eId);

            // 3. contacts element - contact
            contactRoot = output.createElement("contacts");
            rootElement.appendChild(contactRoot);

            // 4. meetings element - meeting
            meetingRoot = output.createElement("meetings");
            rootElement.appendChild(meetingRoot);

            // write contacts to document
            for (Contact c : contacts) {
                // call internal method to setup contact elements
                addContactElement(output, contactRoot, "" + c.getId(), c.getName(), c.getNotes());
            }

            // write meetings to output
            for (Object m : meetings) {

                // check meeting type
                if (m instanceof PastMeeting) {

                    // call internal method to setup meeting element
                    PastMeeting pm = (PastMeeting) m;
                    addMeetingElement(output,
                            meetingRoot,
                            "" + pm.getId(),
                            MeetingType.PAST.toString(),
                            pm.getDate(),
                            pm.getContacts(),
                            pm.getNotes());

                } else if (m instanceof FutureMeeting) {

                    // call internal method to setup meeting element
                    FutureMeeting fm = (FutureMeeting) m;
                    addMeetingElement(output,
                            meetingRoot,
                            "" + fm.getId(),
                            MeetingType.FUTURE.toString(),
                            fm.getDate(),
                            fm.getContacts(),
                            "");

                }

            }

            // write the output into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(output);
            StreamResult result = new StreamResult(new File(filePath));

            // Output to console for testing
            //StreamResult result = new StreamResult(System.out);

            // write
            transformer.transform(source, result);

        // handle exceptions
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }

    }

    /* P R I V A T E   M E T H O D S */

    /**
     * <code>addContactElement()</code>
     * <p>
     *     Creates an xml element for a contact
     * </p>
     */
    private void addContactElement(Document output, Element root, String id, String name, String notes) {

        Element contact, eName, eNotes;     // elements

        // contact root
        contact = output.createElement("contact");
        root.appendChild(contact);

        // set contact id attribute
        contact.setAttribute("id", id);

        // add contact name
        eName = output.createElement("name");
        eName.appendChild(output.createTextNode(name));
        contact.appendChild(eName);

        // add contact notes
        eNotes = output.createElement("notes");
        eNotes.appendChild(output.createTextNode(notes));
        contact.appendChild(eNotes);

    }

    /**
     * <code>addMeetingElement()</code>
     * <p>
     *     Creates an xml element for a meeting
     * </p>
     */
    private void addMeetingElement(Document output,
                                   Element root,
                                   String id,
                                   String type,
                                   Calendar date,
                                   Set<Contact> meetingContacts,
                                   String notes) {

        Element meeting, eDate, eMeetingContacts, eNotes;       // elements

        // meeting root
        meeting = output.createElement("meeting");
        root.appendChild(meeting);

        // set meeting id attribute
        meeting.setAttribute("id", id);
        meeting.setAttribute("type", type);

        // meeting date
        eDate = output.createElement("date");
        eDate.appendChild(output.createTextNode(format.format(date.getTime())));
        meeting.appendChild(eDate);

        // meeting contacts
        eMeetingContacts = output.createElement("meetingContacts");
        meeting.appendChild(eMeetingContacts);

        // iterate through set of contacts
        for (Contact c : meetingContacts) {

            Element contact = output.createElement("meetingContact");
            contact.setAttribute("id", "" + c.getId());
            eMeetingContacts.appendChild(contact);

        }

        // meeting notes
        eNotes = output.createElement("notes");
        eNotes.appendChild(output.createTextNode(notes));
        meeting.appendChild(eNotes);

    }


    /**
     * <code>sortMeetingList()</code>
     * <p>
     *     Rearranges the given list of meetings into chronological order
     * </p>
     */
    private void sortMeetingList(List<Meeting> toSort) {

        // setup comparator class to check meetings based on their meeting date
        Comparator<? super Meeting> comp = new Comparator<Meeting>() {
            @Override
            public int compare(Meeting o1, Meeting o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        };

        // apply comparator to the given list
        Collections.sort(toSort, (Comparator) comp);

    }

    /**
     * <code>sortPastMeetingList()</code>
     * <p>
     *     Rearranges the given list of past meetings into chronological order
     * </p>
     */
    private void sortPastMeetingList(List<PastMeeting> toSort) {

        // setup comparator class to check meetings based on their meeting date
        Comparator<? super Meeting> comp = new Comparator<Meeting>() {
            @Override
            public int compare(Meeting o1, Meeting o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        };

        // apply comparator to the given list
        Collections.sort(toSort, (Comparator) comp);

    }

    /**
     * <code>checkContactExists()</code>
     * <p>
     *     Checks a given contact to see if it exists in the internal set of contacts. Calls method checker
     *     for sets of contacts {@link #checkContactsExist(Set) checkContactsExist}.
     * </p>
     *
     * @return true if contact exists
     */
    private boolean checkContactExists(Contact c) {

        // create set with the given contact
        Set<Contact> cs = new HashSet<Contact>();
        cs.add(c);

        // call internal method to check existence of contacts
        // and return value
        return checkContactsExist(cs);

    }

    /**
     * <code>checkContactsExist()</code>
     * <p>
     *     Checks a given set of contact to see if each contact contains exists in the internal set of contacts
     * </p>
     *
     * @return true if all contact exists
     */
    private boolean checkContactsExist(Set<Contact> checkContacts) {

        // return value
        boolean ret = true;

        // iterate through given set of contacts
        for (Contact c : checkContacts) {

            boolean exists = false;             // existence flag

            // iterate through contact manager's set of contacts
            for (Contact known : this.contacts) {

                if (known.getId() == c.getId()) // if id matches
                    exists = true;              // set flag to true

            }

            // if any contact is not found
            if (!exists)
                ret = false;    // set return to false

        }

        // return boolean
        return ret;

    }

    /**
     * <code>updateMeetingTypes()</code>
     * <p>
     *     This method checks the date of each scheduled meeting and converts any future meetings
     *     to past meetings if the date has now past.
     * </p>
     */
    private void updateMeetingTypes() {

        // Initialise list to populate with updated meetings
        List<Meeting> updatedMeetings = new ArrayList<Meeting>();

        // for each meeting
        for (Object m : meetings) {

            // check the meeting type
            if (m instanceof FutureMeeting) {

                // for all future meetings
                FutureMeeting fm = (FutureMeeting) m;

                // compare the meeting date against the current time
                Calendar now = Calendar.getInstance();
                if (now.compareTo(fm.getDate()) > 0) {              // if the time has past

                    PastMeeting pm = new PastMeetingImpl(fm, "");   // use the copy constructor to create new past meeting
                    updatedMeetings.add(pm);                        // add past meeting to list of meetings

                } else {

                    updatedMeetings.add((Meeting) m);               // add unchanged future meetings to list

                }

            } else {

                updatedMeetings.add((Meeting) m);   // add unchanged past meetings to list

            }

        }

        // point internal meeting list to updated list of meetings
        meetings = updatedMeetings;

    }
}
