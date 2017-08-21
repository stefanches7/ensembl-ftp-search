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

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @Autowired
    private OrganismNameSuggestionRepository organismNameSuggestionRepository;

    @Autowired
    private FileTypeSuggestionRepository fileTypeSuggestionRepository;

    private Link testEntity1;
    private Link testEntity2;

    private String searchPath;
    private String addNewPath;
    private String orgNameParam;
    private String fileTypeParam;
    private String linkUrlParam;
    private String orgNameSuggPath;
    private String fileTypeSuggPath;

    @Before
    public void setup() throws ClassNotFoundException, MalformedURLException {
        this.mockSearchReqController = standaloneSetup(new SearchRequestController(organismNameSuggestionRepository,
                linkRepository, env, fileTypeSuggestionRepository)).build();
        fillUpTestEntities();
        fileTypeSuggPath = env.getProperty("filetype_sugg_path");
        orgNameSuggPath = env.getProperty("orgname_sugg_path");
        searchPath = env.getProperty("search_path");
        orgNameParam = env.getProperty("organism_name_param");
        fileTypeParam = env.getProperty("file_type_param");
        linkUrlParam = env.getProperty("link_url_param");

    }

    private void fillUpTestEntities() throws MalformedURLException {
        testEntity1 = new Link();
        testEntity1.setOrganismName("facebookaddict");
        testEntity1.setFileType("fb");
        testEntity1.setLinkUrl("http://www.facebook.com/");


        testEntity2 = new Link();
        testEntity2.setLinkUrl("http://web.org");
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

    @Test
    public void searchReqController_suggestsOrganismName() throws Exception {
        this.mockSearchReqController.perform(get(orgNameSuggPath).param("value","f")).andExpect(status().isOk())
                .andExpect(content().string(containsString(testEntity1.getOrganismName())));
    }

    @Test
    public void searchReqController_suggestsFileType() throws Exception {
        this.mockSearchReqController.perform(get(fileTypeSuggPath).param("value","f")).andExpect(status().isOk())
                .andExpect(content().string(containsString(testEntity1.getFileType())));
    }
}
