package ebi.ensembl.ftpsearchapi;

import ebi.ensembl.ftpsearchapi.utils.InvalidFilterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * REST controller that accepts search requests and responds with the needed values.
 */
@RestController
@CrossOrigin
public class SearchRequestController {

    private final LinkRepository linkRepository;
    private final Environment env;
    Logger logger = LoggerFactory.getLogger(SearchRequestController.class);

    @Autowired
    public SearchRequestController(final LinkRepository linkRepository, final Environment env) {
        this.linkRepository = linkRepository;
        this.env = env;
    }

    @RequestMapping("/search")
    @ResponseBody
    public List<String> search(@RequestParam final Map<String, String> paramMap) {
        logger.debug("Search request with following parameters came: " + paramMap);
        final LinkSpecificationsIntersector filtersIntersector = new LinkSpecificationsIntersector();
        final List<String> errorsList = new LinkedList<>();

        for (final Map.Entry<String, String> filterEntry : paramMap.entrySet()) {
            final SearchFilter parsedFilter;

            try {
                parsedFilter = new SearchFilter(filterEntry.getKey(), filterEntry.getValue());
            } catch (final InvalidFilterException e) {
                errorsList.add(e.getParamName() + ' ' + env.getProperty("invalid_filter_msg_postfix"));
                continue;
            }

            filtersIntersector.with(parsedFilter);
        }

        final Specification<Link> producedSpec = filtersIntersector.produce();
        final List<String> linkUrlsList = new LinkedList<>();

        //Append errors list to the begin of the response.
        linkUrlsList.addAll(errorsList);

        for (final Link link : linkRepository.findAll(producedSpec)) {
            linkUrlsList.add(String.valueOf(link.getLinkUrl()));
        }
        logger.debug("Sending following found links ");
        return linkUrlsList;
    }

    //FIXME: refactor to be used by update job only/delete!
    @RequestMapping("/addNew")
    public String addNew(@RequestParam final Map<String, String> paramMap) {
        final Link ftpLink = new Link();
        ftpLink.setLinkUrl(paramMap.get("link_url"));
        ftpLink.setFileType(paramMap.get("file_type"));
        ftpLink.setOrganismName(paramMap.get("organism_name"));
        linkRepository.save(ftpLink);
        return "Successfully written.\n";
    }

    @RequestMapping("/findAll")
    @ResponseBody
    public Iterable<Link> findAll() {
        return linkRepository.findAll();
    }

    @RequestMapping("/hello")
    public String helloWorld() {
        logger.debug("Saying hello!");
        return env.getProperty("hello_content");
    }
}