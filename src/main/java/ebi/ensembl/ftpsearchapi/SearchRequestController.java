package ebi.ensembl.ftpsearchapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

@RestController
public class SearchRequestController {

    private final LinkRepository linkRepository;

    private final Environment env;

    @Autowired
    public SearchRequestController(final LinkRepository linkRepository, final Environment env) {
        this.linkRepository = linkRepository;
        this.env = env;
    }

    @RequestMapping("/search")
    public @ResponseBody
    List<Link> search(@RequestParam final Map<String, String> paramMap) {
        final LinkSpecificationsIntersector filtersIntersector = new LinkSpecificationsIntersector();
        for (final Map.Entry<String, String> filterEntry : paramMap.entrySet()) {
            filtersIntersector.with(new SearchFilter(filterEntry.getKey(), filterEntry.getValue()));
        }
        final Specification<Link> producedSpec = filtersIntersector.produce();
        return linkRepository.findAll(producedSpec);
    }

    //FIXME: refactor to be used by update job only/delete!
    @RequestMapping("/addNew")
    public String addNew(@RequestParam final Map<String, String> paramMap) {
        final Link ftpLink = new Link();
        try {
            ftpLink.setLinkUrl(new URL(paramMap.get("link_url")));
        } catch (final MalformedURLException e) {
            return "Specified url is not valid. Please, retry your request.";
        }
        ftpLink.setFileType(paramMap.get("file_type"));
        ftpLink.setOrganismName(paramMap.get("organism_name"));
        linkRepository.save(ftpLink);
        return "Successfully written.";
    }

    @RequestMapping("/getAll")
    public @ResponseBody
    Iterable<Link> findAll() {
        return linkRepository.findAll();
    }

    @RequestMapping("/hello")
    private String helloWorld() {
        return env.getProperty("hello_content");
    }
}