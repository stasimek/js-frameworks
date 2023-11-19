package cz.eg.hr.service;

import cz.eg.hr.data.JavascriptFramework;
import cz.eg.hr.repository.JavascriptFrameworkRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JavascriptFrameworkService {

    private final JavascriptFrameworkRepository repository;

    private final EntityManager em;

    @Autowired
    public JavascriptFrameworkService(
            EntityManager entityManager,
            JavascriptFrameworkRepository javascriptFrameworkRepository
    ) {
        this.em = entityManager;
        this.repository = javascriptFrameworkRepository;
    }

    @Transactional(readOnly = true)
    public Iterable<JavascriptFramework> listAll() {
        return repository.findAll();
    }

    @Transactional
    public JavascriptFramework create(JavascriptFramework framework) {
        if (repository.existsByName(framework.getName())) {
            throw new IllegalArgumentException(
                    "Framework with name " + framework.getName() + " already exists."
            );
        }
        return repository.save(framework);
    }

    @Transactional
    public JavascriptFramework update(Long id, JavascriptFramework framework) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Framework with ID " + id + " not found.");
        }
        framework.setId(id);
        return repository.save(framework);
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<JavascriptFramework> fulltextSearch(String text) {
        SearchSession searchSession = Search.session(em);
        return searchSession.search(JavascriptFramework.class)
                .where(f -> f.match().fields("name", "description").matching(text))
                .fetchAllHits();
    }

}
