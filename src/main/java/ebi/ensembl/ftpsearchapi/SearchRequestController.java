package ebi.ensembl.ftpsearchapi;

import ebi.ensembl.ftpsearchapi.utils.InvalidFilterException;
import ebi.ensembl.ftpsearchapi.utils.ParamsHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
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
        final List<TaxaSearchFilterContainer> taxaSearchFilterContainers = new LinkedList<>();

        for (final Map.Entry<String, String> filterEntry : paramMap.entrySet()) {

            final String camelCasifiedParam = ParamsHelper.camelCasify(filterEntry.getKey());

            if ("taxaBranch".equals(camelCasifiedParam)) {
                try {
                    taxaSearchFilterContainers.add(new TaxaSearchFilterContainer(Integer.valueOf(filterEntry.getValue())));
                } catch (final InvalidFilterException e) {
                    e.printStackTrace();
                    logger.error("Blame the coder of messing the TaxaSearchFilterContainer up.");
                }
                continue;
            }

            final SearchFilter parsedFilter;

            try {
                parsedFilter = new SearchFilter(camelCasifiedParam, filterEntry.getValue());
            } catch (final InvalidFilterException e) {
                errorsList.add(e.getParamName() + ' ' + env.getProperty("invalid_filter_msg_postfix"));
                continue;
            }

            filtersIntersector.with(parsedFilter);
        }

        Specification<Link> producedSpec = filtersIntersector.produce();

        for (final TaxaSearchFilterContainer filterContainer : taxaSearchFilterContainers) {
            //Unite all the organism name filters
            final LinkSpecificationsUnifier linkSpecificationsUnifier = new LinkSpecificationsUnifier();
            for (final SearchFilter organismNameSearchFilter : filterContainer.getChildrenNamesSearchFilters()) {
                linkSpecificationsUnifier.with(organismNameSearchFilter);
            }
            final Specification<Link> unionSpec = linkSpecificationsUnifier.produce();
            //Intersect the union with already existing spec
            producedSpec = Specifications.where(producedSpec).and(unionSpec);
        }

        final List<String> linkUrlsList = new LinkedList<>();

        //Append errors list to the begin of the response.
        linkUrlsList.addAll(errorsList);

        Pageable paging = null;

        if (paramMap.containsKey("page") && paramMap.containsKey("size")) {
            paging = new PageRequest(Integer.parseInt(paramMap.get("page")), Integer.parseInt(paramMap.get
                    ("size")));
        } else if (paramMap.containsKey("size")) {
            //request "size"-many records from the very beginning
            paging = new PageRequest(0, Integer.parseInt(paramMap.get("size")));
        }

        List<Link> linksList = new LinkedList<>();
        if (paging == null) {
            //load all of the results
            linksList = linkRepository.findAll(producedSpec);
        } else {
            //load the specified page's content
            linksList = linkRepository.findAll(producedSpec, paging).getContent();
        }

        for (final Link link : linksList) {
            linkUrlsList.add(link.getLinkUrl());
        }
        logger.debug("Sending following found links " + linksList);
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