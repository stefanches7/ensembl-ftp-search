package ebi.ensembl.ftpsearchapi;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SearchRequestController {

    @RequestMapping("/search")
    public List<String> search(@RequestParam(name="${organism_name_param}", required=false) String organism_name,
                       @RequestParam(name="${file_type_param}", required=false) String fileType) {
        return null;
    }

    @RequestMapping("/hello")
    public String helloWorld() {
        return "Hello World!";
    }
}