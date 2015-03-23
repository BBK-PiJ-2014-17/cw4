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
import java.util.concurrent.FutureTask;


/**
 * Created by Basil on 07/03/2015.
 *
 * Implementation of ContactManager interface
 */
public class ContactManagerImpl implements ContactManager {

    private final String filePath = "contacts.xml"; // contact manager output file
    private Set<Contact> contacts;                  // collection of contacts
    private List<? super Meeting> meetings;         // list of meetings (Past or Future)
    private static int CM_ID = 0;                              // unique ID for meeting and contact creation
    SimpleDateFormat format;                        // format for dates in file

    private Object obj = new Object();

    // meeting type enum

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

    public static int uniqueId() {
        CM_ID++;
        return CM_ID;
    }

    /* C O N S T R U C T O R */

    public ContactManagerImpl() {

        File contactsXml = new File(filePath);                      // set xml file path
        NodeList root, managerNodes, contactNodes, meetingNodes, meetingContactNodes;   // node lists for xml read
        DocumentBuilderFactory dbFactory;                           // for xml output
        DocumentBuilder dBuilder;                                   // for xml output
        Document doc;                                               // for xml output

        contacts = new HashSet<Contact>();                          // initialise contacts set
        meetings = new ArrayList<Meeting>();                        // initialise meetings list
        format = new SimpleDateFormat("dd-MM-yyyy");                // initialise date format

        // setup file read

        try {

            if(!contactsXml.exists()) {             // if file doesn't already exist
                contactsXml.createNewFile();        // create the file
            } else {                                // else read file into values

                dbFactory = DocumentBuilderFactory.newInstance();   // setup xml read
                dBuilder = dbFactory.newDocumentBuilder();          // setup xml read
                doc = dBuilder.parse(contactsXml);                  // read in file
                doc.getDocumentElement().normalize();               // normalise xml

                // start at root

                //root = doc.getElementsByTagName("contactManager");

                // read in current unique ID

                managerNodes = doc.getElementsByTagName("manager");     // get manager node
                CM_ID = Integer.parseInt(managerNodes.item(0).getTextContent());

                // read in contacts

                contactNodes = doc.getElementsByTagName("contact");     // get list of contact nodes

                for (int i = 0; i < contactNodes.getLength(); i++) {    // read each node into contacts set

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

                // read in meetings

                meetingNodes = doc.getElementsByTagName("meeting");     // get list of meeting nodes

                for (int i = 0; i < meetingNodes.getLength(); i++) {    // read each node into meeting list

                    Node nMeeting = meetingNodes.item(i);      // current node

                    if (nMeeting.getNodeType() == Node.ELEMENT_NODE) {     // read only element type nodes

                        Element eElement = (Element) nMeeting; // current element node

                        // meeting date from file as calendar object

                        Date xmlDate = format.parse(eElement.getElementsByTagName("date").item(0).getTextContent());    // read date from string
                        Calendar meetingDate = Calendar.getInstance();  // create calendar object
                        meetingDate.setTime(xmlDate);                   // initialise calendar object with date from file

                        // meeting notes

                        String meetingNotes = eElement.getElementsByTagName("notes").item(0).getTextContent();

                        // meeting contacts collection from file
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
     * <code>getFutureMeetingList(Contact)</code>
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
     * <code>getFutureMeetingList(Calendar)</code>
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

    @Override
    public List<PastMeeting> getPastMeetingList(Contact contact) {

        if (!checkContactExists(contact))
            throw new IllegalArgumentException();

        updateMeetingTypes();   // update any future meetings

        List<PastMeeting> ret = new ArrayList<PastMeeting>();

        for (Object o : meetings) {

            if (o instanceof PastMeeting) {

                PastMeeting m = (PastMeeting) o;
                Set<Contact> cs = m.getContacts();

                for (Contact c : cs) {

                    if (contact.getId() == c.getId())
                        ret.add(m);

                }

            }

        }

        return ret;

    }

    /**
     * <code>addNewPastMeeting()</code>
     * {@inheritDoc}
     * <p>
     *     After checking that none of the arguments are null, the contacts are known
     *     to the contact manager, and that the contact collection is not empty,
     *     this method adds a new past meeting to the internal list of meetings.
     *
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

    @Override
    public void addMeetingNotes(int id, String text) {

        if (text == null)
            throw new NullPointerException();

        Meeting m = getMeeting(id);

        if (m == null)
            throw new IllegalArgumentException();

        Calendar now = Calendar.getInstance();
        if (now.compareTo(m.getDate()) < 0)
            throw new IllegalStateException();

        PastMeeting pm = new PastMeetingImpl(m, text);

        // add all meeting updates into synchronised method

        meetings.remove(m);     // remove existing meeting
        meetings.add(pm);       // add new past meeting with notes

    }

    @Override
    public void addNewContact(String name, String notes) {

        if (name == null || notes == null)
            throw new NullPointerException();

        contacts.add(new ContactImpl(uniqueId(), name, notes));

    }

    @Override
    public Set<Contact> getContacts(int... ids) {

        for (int i : ids) {

            boolean exists = false;

            for (Contact c : contacts) {

                if (i == c.getId()) {

                    exists = true;

                }

            }

            if (!exists)
                throw new IllegalArgumentException();

        }

        Set<Contact> ret = new HashSet<Contact>();

        for (Contact c : contacts) {

            for (int i : ids) {

                if (i == c.getId()) {
                    ret.add(c);
                }

            }

            //if (Arrays.asList(ids).contains(c.getId()))
            //    ret.add(c);

        }

        return ret;
    }

    @Override
    public Set<Contact> getContacts(String name) {

        if (name == null)
            throw new NullPointerException();

        Set<Contact> ret = new HashSet<Contact>();

        for (Contact c : contacts) {

            if (c.getName().equals(name))
                ret.add(c);

        }

        return ret;
    }

    @Override
    public void flush() {

        //File contactsXml = new File(filePath);
        Document output;
        Element managerRoot, contactRoot, meetingRoot;
        DocumentBuilderFactory docFactory;
        DocumentBuilder docBuilder;

        try {
/*

            if(!contactsXml.exists())            // if file doesn't already exist
                contactsXml.createNewFile();        // create the file
*/

            docFactory = DocumentBuilderFactory.newInstance();
            docBuilder = docFactory.newDocumentBuilder();

            // root element - contactmanager
            output = docBuilder.newDocument();
            Element rootElement = output.createElement("contactmanager");
            output.appendChild(rootElement);

            // manager element - manager
            managerRoot = output.createElement("manager");
            rootElement.appendChild(managerRoot);

            Element eId = output.createElement("CM_ID");
            eId.appendChild(output.createTextNode("" + CM_ID));
            managerRoot.appendChild(eId);

            // contacts element - contact
            contactRoot = output.createElement("contacts");
            rootElement.appendChild(contactRoot);

            // meetings element - meeting
            meetingRoot = output.createElement("meetings");
            rootElement.appendChild(meetingRoot);

            // add contacts

            for (Contact c : contacts) {
                addContactElement(output, contactRoot, "" + c.getId(), c.getName(), c.getNotes());
            }

            for (Object m : meetings) {

                if (m instanceof PastMeeting) {

                    PastMeeting pm = (PastMeeting) m;
                    addMeetingElement(output,
                            meetingRoot,
                            "" + pm.getId(),
                            MeetingType.PAST.toString(),
                            pm.getDate(),
                            pm.getContacts(),
                            pm.getNotes());

                } else if (m instanceof FutureMeeting) {

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

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(output);
            StreamResult result = new StreamResult(new File(filePath));

            // Output to console for testing
            //StreamResult result = new StreamResult(System.out);

            transformer.transform(source, result);

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }/* catch (IOException ioe) {
            ioe.printStackTrace();
        }*/

    }

    // internal methods for xml construction

    private void addContactElement(Document output, Element root, String id, String name, String notes) {

        Element contact, eName, eNotes;

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

    private void addMeetingElement(Document output,
                                   Element root,
                                   String id,
                                   String type,
                                   Calendar date,
                                   Set<Contact> meetingContacts,
                                   String notes) {

        Element meeting, eDate, eMeetingContacts, eNotes;

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



    public void sortMeetingList(List<Meeting> toSort) {

        Comparator<? super Meeting> comp = new Comparator<Meeting>() {
            @Override
            public int compare(Meeting o1, Meeting o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        };

        Collections.sort(toSort, (Comparator) comp);

    }

    private boolean checkContactExists(Contact c) {

        Set<Contact> cs = new HashSet<Contact>();
        cs.add(c);

        return checkContactsExist(cs);

    }

    private boolean checkContactsExist(Set<Contact> checkContacts) {

        boolean ret = true;

        for (Contact c : checkContacts) {

            boolean exists = false;

            for (Contact known : this.contacts) {

                if (known.getId() == c.getId())
                    exists = true;

            }

            if (!exists)
                ret = false;

        }

        return ret;

    }

    private synchronized void updateMeetingTypes() {

        List<Meeting> updatedMeetings = new ArrayList<Meeting>();

        for (Object m : meetings) {

            if (m instanceof FutureMeeting) {

                FutureMeeting fm = (FutureMeeting) m;

                Calendar now = Calendar.getInstance();
                if (now.compareTo(fm.getDate()) > 0) {

                    PastMeeting pm = new PastMeetingImpl(fm, "");
                    updatedMeetings.add(pm);

                } else {

                    updatedMeetings.add((Meeting) m);

                }

            } else {

                updatedMeetings.add((Meeting) m);

            }

        }

        meetings = updatedMeetings;

    }
}
