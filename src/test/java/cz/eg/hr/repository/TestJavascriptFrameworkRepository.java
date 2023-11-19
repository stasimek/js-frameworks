package cz.eg.hr.repository;

import cz.eg.hr.data.JavascriptFramework;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository just for tests.
 */
public interface TestJavascriptFrameworkRepository extends CrudRepository<JavascriptFramework, Long> {

    @Query(
            "SELECT f"
            + " FROM JavascriptFramework f"
            + " LEFT JOIN FETCH f.versions"
            + " WHERE f.name = :name"
    )
    JavascriptFramework getByName(String name);
}
