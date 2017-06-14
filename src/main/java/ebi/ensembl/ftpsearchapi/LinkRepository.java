package ebi.ensembl.ftpsearchapi;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

public interface LinkRepository extends CrudRepository<Link, Long>, JpaSpecificationExecutor{

}
