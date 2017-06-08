package ebi.ensembl.ftpsearchapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

@RestController
public class SearchRequestController {

    @Autowired
    private LinkRepository linkRepository;

    @RequestMapping("/search")
    public @ResponseBody List<String> search(@RequestParam(name="${organism_name_param}", required=false) String organism_name) {
        return null;
    }

    //DEBUG
    @RequestMapping("/addNew")
    public String addNew(@RequestParam(name="${organism_name_param}", required=true) String organism_name,
                         @RequestParam(name="${file_type_param}", required=false) String fileType, @RequestParam
                                     (name="${link_url_param}", required=false) String linkAdress) {
        Link ftpLink = new Link();
        try {
            ftpLink.setLinkUrl(new URL(linkAdress));
        } catch (MalformedURLException e) {
            return "Specified url is not valid. Please, retry your request.";
        }
        ftpLink.setFileType(fileType);
        ftpLink.setOrganismName(organism_name);
        linkRepository.save(ftpLink);
        return "Sucessfully written.";
    }

    @RequestMapping("/getAll")
    public @ResponseBody Iterable<Link> findAll() {
        return linkRepository.findAll();
    }

    @RequestMapping("/hello")
    public String helloWorld() {
        return "Hello World!";
    }
}