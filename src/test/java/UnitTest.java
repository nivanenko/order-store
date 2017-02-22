import com.odyssey.controller.FileProcessController;
import com.odyssey.controller.LookupController;
import com.odyssey.service.OrderService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.easymock.EasyMock.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

@RunWith(SpringRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/root-context.xml",
        "file:src/main/webapp/WEB-INF/appServlet/servlet-context.xml"})
@WebAppConfiguration
public class UnitTest {
    @Autowired
    private WebApplicationContext wac;

    FileProcessController uploadController;
    LookupController lookupController;
    OrderService mockService;
    MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        mockService = mock(OrderService.class);

        uploadController = mock(FileProcessController.class);
        lookupController = mock(LookupController.class);
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void testUpload() throws Exception {
        MockMultipartFile fmp = new MockMultipartFile("test",
                Files.readAllBytes(Paths.get(getClass().getResource("test.xml").toURI())));

        MvcResult mvcResult = mockMvc.perform(fileUpload("/upload")
                .file(fmp))
                .andExpect(request().asyncStarted())
                .andReturn();

        // java.lang.IllegalStateException: Async result for handler
        // [public org.springframework.web.context.request.async.DeferredResult<java.lang.String> com.odyssey.controller.FileProcessController.processFile()]
        // was not set during the specified timeToWait=250
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(request().asyncResult(""))
                .andReturn();
    }


    @Test
    public void testLookup() throws Exception {
        mockMvc.perform(get("/lookup").param("value", "798"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.from.city").value("Kiev"));
        mockMvc.perform(get("/lookup").param("value", "999999"))
                .andExpect(status().is(500));
    }
}