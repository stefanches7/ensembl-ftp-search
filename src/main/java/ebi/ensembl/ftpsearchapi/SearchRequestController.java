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
import java.util.stream.Collectors;

/**
 * REST controller that accepts search requests and responds with the needed values (incl. suggestions searches).
 */
@RestController
@CrossOrigin
public class SearchRequestController {

    private final FileTypeSuggestionRepository fileTypeSuggestionRepository;
    private final OrganismNameSuggestionRepository organismNameSuggestionRepository;
    private final LinkRepository linkRepository;
    private final Environment env;
    Logger logger = LoggerFactory.getLogger(SearchRequestController.class);

    @Autowired
    public SearchRequestController(final OrganismNameSuggestionRepository organismNameSuggestionRepository,
                                   final LinkRepository linkRepository, final Environment env,
                                   FileTypeSuggestionRepository fileTypeSuggestionRepository) {
        this.linkRepository = linkRepository;
        this.env = env;
        this.organismNameSuggestionRepository = organismNameSuggestionRepository;
        this.fileTypeSuggestionRepository = fileTypeSuggestionRepository;
    }

    @RequestMapping("/search")
    @ResponseBody
    public List<String> search(@RequestParam final Map<String, String> paramMap) {
        logger.debug("Search request with following parameters came: " + paramMap);
        final LinkSpecificationsIntersector filtersIntersector = new LinkSpecificationsIntersector();
        final List<String> errorsList = new LinkedList<>();
        final List<TaxaSearchFilterContainer> taxaSearchFilterContainers = new LinkedList<>();
        boolean isOrganismNameFilterSeen = true; //taxa filter adequacy flag

        for (final Map.Entry<String, String> filterEntry : paramMap.entrySet()) {

            if (ParamsHelper.isValidSupportParam(filterEntry.getKey())) { //paging params etc.
                continue;
            }
            final String camelCasifiedParam = ParamsHelper.camelCasify(filterEntry.getKey());

            if ("taxaBranch".equals(camelCasifiedParam)) {
                try {
                    isOrganismNameFilterSeen = false; //prepare to check this taxa filter's compliancy
                    taxaSearchFilterContainers.add(new TaxaSearchFilterContainer(Integer.valueOf(filterEntry.getValue())));
                } catch (final InvalidFilterException e) {
                    e.printStackTrace();
                    logger.error("Blame the coder for messing the TaxaSearchFilterContainer up.");
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
            List<SearchFilter> childrenNameFilters = filterContainer.getChildrenNamesSearchFilters();
            if (!childrenNameFilters.isEmpty()) {isOrganismNameFilterSeen = true;}
            for (final SearchFilter organismNameSearchFilter : childrenNameFilters) {
                linkSpecificationsUnifier.with(organismNameSearchFilter);
            }
            final Specification<Link> unionSpec = linkSpecificationsUnifier.produce();
            //Intersect the union with already existing spec
            producedSpec = Specifications.where(producedSpec).and(unionSpec);
        }
        if (!isOrganismNameFilterSeen) {
            errorsList.add("(Taxonomy branch that you've specified doesn't contain any organism from our database. " +
                    "Therefore " +
                    "this " +
                    "filter was suspended.)");
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

    @RequestMapping("/organismNameSuggestion")
    public List<String> suggestOrganismName(@RequestParam String value) {
        List<OrganismNameSuggestion> organismNameSuggestionList = organismNameSuggestionRepository
                .findByOrganismNameLimit20("%" + value + "%"); //append mysql wildcards
        return organismNameSuggestionList.stream().map(orgNameSugg -> orgNameSugg.getOrganismName()).collect(Collectors.toList());
    }

    @RequestMapping("/fileTypeSuggestion")
    public List<String> suggestFileType(@RequestParam String value) {
        List<FileTypeSuggestion> fileTypeSuggestionList = fileTypeSuggestionRepository
                .findByFileTypeLimit20("%" + value + "%"); //append mysql wildcards
        return fileTypeSuggestionList.stream().map(fileTypeSugg -> fileTypeSugg.getFileType()).collect(Collectors.toList
                ());
    }

    @RequestMapping("/hello")
    public String helloWorld() {
        logger.debug("Saying hello!");
        return env.getProperty("hello_content");
    }
}