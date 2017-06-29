package ebi.ensembl.ftpsearchapi;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;

import java.net.MalformedURLException;
import java.net.URL;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

/**
 * FIXME: test data overwrites the real one each time tests are run.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@PropertySource("classpath:application.properties")
@DataJpaTest
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,DbUnitTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class})
@DatabaseSetup("classpath:test_link_table.xml")
public class FtpSearchApiApplicationTests {

    private MockMvc mockSearchReqController;

    @Autowired
    private Environment env;

    @Autowired
    private LinkRepository linkRepository;

    private Link testEntity1;
    private Link testEntity2;

    private String searchPath;
    private String addNewPath;
    private String orgNameParam;
    private String fileTypeParam;
    private String linkUrlParam;

    @Before
    public void setup() throws ClassNotFoundException, MalformedURLException {
        this.mockSearchReqController = standaloneSetup(new SearchRequestController(linkRepository, env)).build();
        fillUpTestEntities();
        searchPath = env.getProperty("search_path");
        addNewPath = env.getProperty("add_new_path");
        orgNameParam = env.getProperty("organism_name_param");
        fileTypeParam = env.getProperty("file_type_param");
        linkUrlParam = env.getProperty("link_url_param");

    }

    private void fillUpTestEntities() throws MalformedURLException {
        testEntity1 = new Link();
        testEntity1.setOrganismName("facebookaddict");
        testEntity1.setFileType("fb");
        testEntity1.setLinkUrl(new URL("http://www.facebook.com/"));


        testEntity2 = new Link();
        testEntity2.setLinkUrl(new URL("http://web.org"));
        testEntity2.setFileType("fb");
        testEntity2.setOrganismName("Silvesternacht");
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

    /**
     * Existence of this test and corresponding behavior is temporary.
     */
    @Test
    public void searchReqController_returnsAllLinksOnFindAll() throws Exception {
        //given
        final String findAllPath = env.getProperty("get_all_path");

        //when
        this.mockSearchReqController.perform(get(findAllPath)).andExpect
                (status().isOk());
    }

    /**
     * FIXME: replace with a more proper one/delete when update job is implemented.
     *
     */
    @Test
    public void searchReqController_writesOrganismToRepo() throws Exception {
        this.mockSearchReqController.perform(post(addNewPath).param(orgNameParam,
                testEntity1.getOrganismName()).param(fileTypeParam, testEntity1.getFileType()).param(linkUrlParam,
                String.valueOf(testEntity1.getLinkUrl())))
                .andExpect(status().isOk()).andExpect(content().string("Successfully written.\n"));
    }

    @Test
    public void searchReqController_returnsLinkGivenValidAndMatchingParam() throws Exception {

        //when-then
        this.mockSearchReqController.perform(get(searchPath).param(orgNameParam,testEntity1.getOrganismName())).andExpect
                (status().is2xxSuccessful()).andExpect(content().string(containsString(String.valueOf(testEntity1.getLinkUrl()))));
    }

    @Test
    public void searchReqController_ignoresInvalidFilterApplyingRightOneS() throws Exception {
        //when
        this.mockSearchReqController.perform(get(searchPath).param(fileTypeParam,"fb").param("sailorM00n","vodka"))
                .andExpect(status().isOk()).andExpect(content().string(containsString(String.valueOf
                (testEntity1.getLinkUrl
                ()))));
    }

    @Test
    public void searchReqController_warnsThatFilterWasInvalid() throws Exception {
        this.mockSearchReqController.perform(get(searchPath).param(fileTypeParam,"fb").param("sailorM00n","vodka"))
                .andExpect(status().isOk()).andExpect(content().string(containsString(env.getProperty
                ("invalid_filter_msg_postfix"))));

    }

    @Test
    public void searchReqController_intersectsFilters() throws Exception {
        //filetype is same, but organism name is different!
        this.mockSearchReqController.perform(get(searchPath).param(fileTypeParam,"fb").param(orgNameParam,
                "facebookaddict")).andExpect(status().is2xxSuccessful()).andExpect(content().string(containsString(String
                .valueOf(testEntity1.getLinkUrl()))));

    }
}
