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
 */
public class ContactManagerImpl implements ContactManager {

    private final String filePath = "contacts.txt"; // contact manager output file
    private Set<Contact> contacts;                  // collection of contacts
    private List<? super Meeting> meetings;         // list of meetings (Past or Future)
    private int CM_ID = 0;                              // unique ID for meeting and contact creation
    SimpleDateFormat format;                        // format for dates in file

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

    // constructor

    public ContactManagerImpl() {

        File contactsXml = new File(filePath);                      // set xml file path
        NodeList managerNodes, contactNodes, meetingNodes, meetingContactNodes;   // node lists for xml read
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

                    Node nNode = meetingNodes.item(i);      // current node

                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {     // read only element type nodes

                        Element eElement = (Element) nNode; // current element node

                        // meeting date from file as calendar object

                        Date xmlDate = format.parse(eElement.getElementsByTagName("date").item(0).getTextContent());    // read date from string
                        Calendar meetingDate = Calendar.getInstance();  // create calendar object
                        meetingDate.setTime(xmlDate);                   // initialise calendar object with date from file

                        // meeting contacts collection from file

                        Set<Contact> meetingContacts = new HashSet<Contact>();  // set for current meeting

                        meetingContactNodes = doc.getElementsByTagName("meetingContacts");  // get list of meeting contact nodes

                        for (int j = 0; j < meetingContactNodes.getLength(); j++) {     // read each node into current meeting contact set

                            Node nMeetingContactNode = meetingContactNodes.item(i);             // current node

                            if (nMeetingContactNode.getNodeType() == Node.ELEMENT_NODE) {       // read only element type nodes

                                Element eMeetingContactElement = (Element) nMeetingContactNode; // current element node
                                int id = Integer.parseInt(eMeetingContactElement.getAttribute("id"));   // get meeting ID

                                Set<Contact> cs = getContacts(id);      // find contact by ID
                                Contact c = (Contact) cs.toArray()[0];  // one contact return expected since only one unique ID searched
                                meetingContacts.add(c);                 // add contact to meeting contacts set

                            }
                        }

                        // determine meeting type and add meeting to meeting list

                        if (eElement.getAttribute("type").equals(MeetingType.PAST)) {

                            PastMeeting m = new PastMeetingImpl(Integer.parseInt(eElement.getAttribute("id")),
                                    meetingDate,
                                    meetingContacts,
                                    "");

                            meetings.add(m);

                        } else if (eElement.getAttribute("type").equals(MeetingType.FUTURE)) {

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

    @Override
    public int addFutureMeeting(Set<Contact> meetingContacts, Calendar date) {

        int id = uniqueId();

        FutureMeeting fm = new FutureMeetingImpl(id, date, meetingContacts);
        meetings.add(fm);

        return id;
    }

    @Override
    public PastMeeting getPastMeeting(int id) {

        PastMeeting ret = null;

        for (Object m : meetings) {

            if (m instanceof PastMeeting) {

                PastMeeting pm = (PastMeeting) m;

                if (pm.getId() == id) {
                    ret = pm;
                }
            }

        }

        return ret;
    }

    @Override
    public FutureMeeting getFutureMeeting(int id) {

        FutureMeeting ret = null;

        for (Object m : meetings) {

            if (m instanceof FutureMeeting) {

                FutureMeeting fm = (FutureMeeting) m;

                if (fm.getId() == id) {
                    ret = fm;
                }
            }

        }

        return ret;
    }

    @Override
    public Meeting getMeeting(int id) {

        Meeting ret = null, m;

        for (Object o : meetings) {

            m = (Meeting) o;

            if (m.getId() == id)
                ret = m;

        }

        return ret;
    }

    @Override
    public List<Meeting> getFutureMeetingList(Contact contact) {

        List<Meeting> ret = new ArrayList<Meeting>();

        for (Object o : meetings) {

            Meeting m = (Meeting) o;
            Set<Contact> cs = m.getContacts();

            for (Contact c : cs) {

                if (contact.getId() == c.getId())
                    ret.add(m);

            }

        }

        return ret;
    }

    @Override
    public List<Meeting> getFutureMeetingList(Calendar date) {
        return null;
    }

    @Override
    public List<PastMeeting> getPastMeetingList(Contact contact) {
        return null;
    }

    @Override
    public void addNewPastMeeting(Set<Contact> contacts, Calendar date, String text) {

    }

    @Override
    public void addMeetingNotes(int id, String text) {

        if (text == null)
            throw new IllegalArgumentException();

        Meeting m = getMeeting(id);

        if (m == null)
            throw new IllegalArgumentException();

        Calendar now = Calendar.getInstance();
        if (now.compareTo(m.getDate()) < 0)
            throw new IllegalStateException();

        PastMeeting pm = new PastMeetingImpl(m, text);

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

            if (Arrays.asList(ids).contains(c.getId()))
                ret.add(c);

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

        Document output;
        Element managerRoot, contactRoot, meetingRoot;
        DocumentBuilderFactory docFactory;
        DocumentBuilder docBuilder;

        try {

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
        }

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

    private int uniqueId() {
        CM_ID++;
        return CM_ID;
    }

}
