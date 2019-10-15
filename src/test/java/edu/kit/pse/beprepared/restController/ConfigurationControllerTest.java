package edu.kit.pse.beprepared.restController;

import edu.kit.pse.beprepared.json.ConfigurationJson;
import edu.kit.pse.beprepared.model.Configuration;
import edu.kit.pse.beprepared.services.ConfigurationManagementService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.HashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

public class ConfigurationControllerTest extends AbstractControllerTest {

    @MockBean
    private ConfigurationManagementService service;

    @Override
    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    public void testGetConfigurations() throws Exception {

        // preset

        final String URI = "/configs";
        final long pointInTime = System.currentTimeMillis();
        final String testKey = "bouncy";
        final String testVal = "cats";
        final HashMap<String, Object> additionalProperties = new HashMap<>() {{
            put(testKey, testVal);
        }};
        final Configuration configuration = new Configuration(pointInTime, additionalProperties);

        when(service.getAllConfigurations()).thenReturn(Collections.singletonList(configuration));

        // perform request

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(URI).accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        // check result

        assertThat(mvcResult.getResponse().getStatus(), is(200));

        ConfigurationJson[] configs = super.mapFromJson(mvcResult.getResponse().getContentAsString(),
                ConfigurationJson[].class);

        assertThat(configs.length, is(1));
        assertThat(configs[0].getScenarioStartTime(), is(pointInTime));
        assertThat(configs[0].getAdditionalProperties(), hasEntry(is(testKey), is(testVal)));
        assertThat(configs[0].getConfigurationID(), greaterThanOrEqualTo(0));

    }

}
