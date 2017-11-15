package entities;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by jakepardue on 11/10/17.
 */
public class RequestTest {

    Request r1 = new Request(new Location(1,1, "1","Kent's Wing"),TypeOfRequest.FOOD, PriorityLevel.LOW, "kent is very hungry and he wants food now" );


    @Test
    public void testGetToLocation() throws Exception {
        assertTrue(r1.getToLocation().equals(new Location(1,1, "1","Kent's Wing")));
    }

    @Test
    public void testGetMessage() throws Exception {
        assertEquals("kent is very hungry and he wants food now",r1.getMessage());
    }
    @Test
    public void testSetToLocation() throws Exception {
        Location newLocation = new Location(2,2,"2","Jake's Wing");
        r1.setToLocation(newLocation);
        assertEquals(newLocation, r1.getToLocation());
    }

    @Test
    public void testSetMessage() throws Exception {
        String newMessage = "Kent and the patient in the next room Jake is also hungry";
        r1.setMessage(newMessage);
        assertEquals(newMessage, r1.getMessage());
    }

    @Test
    public void tetsGetPriorityLevel() throws Exception {
        assertEquals(PriorityLevel.LOW, r1.getPriorityLevel());
    }

    @Test
    public void testSetPriorityLevel() throws Exception {
        r1.setPriorityLevel(PriorityLevel.MED);
        assertEquals(PriorityLevel.MED, r1.getPriorityLevel());
    }

    @Test
    public void testGetTypeOfRequest() throws Exception {
        assertEquals(TypeOfRequest.FOOD, r1.getTypeOfRequest());
    }

    @Test
    public void testSetTypeOfRequest() throws Exception {
        r1.setTypeOfRequest(TypeOfRequest.SEC);
        assertEquals(TypeOfRequest.SEC, r1.getTypeOfRequest());
    }



}