package cz.eg.hr.controller;

import cz.eg.hr.data.JavascriptFramework;
import cz.eg.hr.service.JavascriptFrameworkService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JavascriptFrameworkController {

    private final JavascriptFrameworkService javascriptFrameworkService;

    @Autowired
    public JavascriptFrameworkController(
            JavascriptFrameworkService javascriptFrameworkService
    ) {
        this.javascriptFrameworkService = javascriptFrameworkService;
    }

    @GetMapping("/frameworks")
    public Iterable<JavascriptFramework> frameworks() {
        return javascriptFrameworkService.listAll();
    }

    @PostMapping("/frameworks")
    public JavascriptFramework createFramework(
            @Validated @RequestBody JavascriptFramework framework
    ) {
        return javascriptFrameworkService.create(framework);
    }

    @PutMapping("/frameworks/{id}")
    public JavascriptFramework updateFramework(
            @PathVariable Long id,
            @Validated @RequestBody JavascriptFramework framework
    ) {
        return javascriptFrameworkService.update(id, framework);
    }

    @DeleteMapping("/frameworks/{id}")
    public void deleteFramework(@PathVariable Long id) {
        javascriptFrameworkService.delete(id);
    }

    @GetMapping("/frameworks/search")
    public List<JavascriptFramework> fulltextSearch(@RequestParam String text) {
        return javascriptFrameworkService.fulltextSearch(text);
    }
}
