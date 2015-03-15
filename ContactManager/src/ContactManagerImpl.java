import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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
        NodeList contactNodes, meetingNodes, meetingContactNodes;   // node lists for xml read
        DocumentBuilderFactory dbFactory;                           // for xml output
        DocumentBuilder dBuilder;                                   // for xml output
        Document doc;                                               // for xml output

        contacts = new HashSet<Contact>();                          // initialise contacts set
        meetings = new ArrayList<Meeting>();                        // initialise meetings list

        // setup file read

        try {

            if(!contactsXml.exists()) {             // if file doesn't already exist
                contactsXml.createNewFile();        // create the file
            }

            dbFactory = DocumentBuilderFactory.newInstance();   // setup xml read
            dBuilder = dbFactory.newDocumentBuilder();          // setup xml read
            doc = dBuilder.parse(contactsXml);                  // read in file
            doc.getDocumentElement().normalize();               // normalise xml

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

                    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");   // set date format for read
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

                        FutureMeetingImpl m = new FutureMeetingImpl(Integer.parseInt(eElement.getAttribute("id")),
                                                                    meetingDate,
                                                                    meetingContacts);

                        meetings.add(m);

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
    public int addFutureMeeting(Set<Contact> contacts, Calendar date) {
        return 0;
    }

    @Override
    public PastMeeting getPastMeeting(int id) {
        return null;
    }

    @Override
    public FutureMeeting getFutureMeeting(int id) {
        return null;
    }

    @Override
    public Meeting getMeeting(int id) {
        return null;
    }

    @Override
    public List<Meeting> getFutureMeetingList(Contact contact) {
        return null;
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

    }

    @Override
    public void addNewContact(String name, String notes) {

    }

    @Override
    public Set<Contact> getContacts(int... ids) {
        return null;
    }

    @Override
    public Set<Contact> getContacts(String name) {
        return null;
    }

    @Override
    public void flush() {

    }
}
