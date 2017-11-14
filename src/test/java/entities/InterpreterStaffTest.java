package entities;

import org.junit.Test;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by jakepardue on 11/11/17.
 */
public class InterpreterStaffTest {
    List<String> languages = Arrays.asList("English","French","Spanish");
    InterpreterStaff staff1 = new InterpreterStaff("John","Smith","+16666666666",
            true, languages);

    @Test
    public void getFirstName() throws Exception {
        assertEquals("John", staff1.getFirstName());
    }

    @Test
    public void getLastName() throws Exception {
        assertEquals("Smith",staff1.getLastName());
    }

    @Test
    public void getPhoneNumber() throws Exception {
        assertEquals("+16666666666", staff1.getPhoneNumber());
    }

    @Test
    public void isAvailable() throws Exception {
        assertEquals(true,staff1.getIsAvailable());
    }

    @Test
    public void getSpokenLanguages() throws Exception {
        assertEquals(languages, staff1.getSpokenLanguages());
    }

    @Test
    public void getServiceType() throws Exception {
        assertEquals(ServiceStaff.INTR, staff1.getServiceType());
    }

    @Test
    public void setFirstName() throws Exception {
        staff1.setFirstName("Jake");
        assertEquals("Jake", staff1.getFirstName());
    }

    @Test
    public void setLastName() throws Exception {
        staff1.setLastName("Shields");
        assertEquals("Shields", staff1.getLastName());
    }

    @Test
    public void setPhoneNumber() throws Exception {
       staff1.setPhoneNumber("1234");
       assertEquals("1234", staff1.getPhoneNumber());
    }

    @Test
    public void setIsAvailable() throws Exception {
        staff1.setIsAvailable(false);
        assertEquals(false, staff1.getIsAvailable());
    }

    @Test
    public void setSpokenLanguages() throws Exception {
        List<String> languages2 = Arrays.asList("English");
        staff1.setSpokenLanguages(languages2);
        assertEquals(languages2, staff1.getSpokenLanguages());
    }

}