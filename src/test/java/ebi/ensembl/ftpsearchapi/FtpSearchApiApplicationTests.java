package ebi.ensembl.ftpsearchapi;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
@PropertySource("classpath:application.properties")
public class FtpSearchApiApplicationTests {

    private MockMvc mockSearchReqController;

    @Autowired
    private Environment env;

    @Autowired
    private LinkRepository linkRepository;

    @Before
    public void setup() throws ClassNotFoundException {
        this.mockSearchReqController = standaloneSetup(new SearchRequestController(linkRepository, env)).build();
    }

    @Test
    public void searchReqController_saysHello() throws Exception {
        //given
        final String helloPath = env.getProperty("hello_path");
        final String helloContent = env.getProperty("hello_content");

        //when-then
        this.mockSearchReqController.perform(get(helloPath)).andExpect
                (status().isOk()).andExpect(content().string(helloContent));
    }

    @Test
    public void searchReqController_writesOrganismToRepo() throws Exception {
        //given
        final String addNewPath = env.getProperty("add_new_path");
        final String orgNameParam = env.getProperty("organism_name_param");
        final String fileTypeParam = env.getProperty("file_type_param");
        final String linkUrlParam = env.getProperty("link_url_param");

        //when-then
        this.mockSearchReqController.perform(post(addNewPath).param(orgNameParam,
                "facebookaddict").param(fileTypeParam, "fb").param(linkUrlParam, "http://www.facebook.com/"))
                .andExpect(status().isOk()).andExpect(content().string("Successfully written."));

    }

    @Test
    public void searchReqController_respondsListOfAll() {

    }
}
