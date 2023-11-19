package cz.eg.hr.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.eg.hr.data.FrameworkVersion;
import cz.eg.hr.data.JavascriptFramework;
import cz.eg.hr.repository.TestJavascriptFrameworkRepository;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class JavascriptFrameworkControllerTest {

    private static final MediaType JSON = MediaType.APPLICATION_JSON;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager em;

    @Autowired
    private TestJavascriptFrameworkRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        // Clean up before every test
        repository.deleteAll();
        em.clear();

        // Put some data to database
        JavascriptFramework angular = new JavascriptFramework("Angular", "Angular is desent", 3);
        angular.addVersion(new FrameworkVersion("1.0", LocalDate.of(2010, 10, 10)));
        angular.addVersion(new FrameworkVersion("2.0", LocalDate.of(2011, 11, 11)));
        angular.addVersion(new FrameworkVersion("7.2", null));
        repository.save(angular);

        JavascriptFramework react = new JavascriptFramework("React", "React is better", 4);
        react.addVersion(new FrameworkVersion("1.0", LocalDate.of(2015, 10, 10)));
        react.addVersion(new FrameworkVersion("2.0", LocalDate.of(2016, 11, 11)));
        react.addVersion(new FrameworkVersion("18.2", null));
        repository.save(react);
    }

    @Test
    public void listFrameworks() throws Exception {
        mockMvc.perform(get("/frameworks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", hasItems("Angular", "React")));
    }

    @Test
    public void createFramework() throws Exception {
        JavascriptFramework htmx = new JavascriptFramework("HTMX", "HTMX is the best", 5);
        htmx.addVersion(new FrameworkVersion("1.9", null));
        String jsonBody = objectMapper.writeValueAsString(htmx);

        mockMvc.perform(post("/frameworks").contentType(JSON).content(jsonBody))
                .andExpect(status().isOk());

        JavascriptFramework createdHMTX = repository.getByName("HTMX");
        assertThat(createdHMTX, notNullValue());
        assertThat(createdHMTX.getName(), is("HTMX"));
        assertThat(createdHMTX.getDescription(), is("HTMX is the best"));
        assertThat(createdHMTX.getRating(), is(5));
        List<FrameworkVersion> versions = createdHMTX.getVersions();
        assertThat(versions, hasSize(1));
        assertThat(versions.get(0).getVersion(), is("1.9"));
        assertThat(versions.get(0).getDeprecationDate(), nullValue());
    }

    @Test
    public void createFramework_duplicity() throws Exception {
        JavascriptFramework angular = new JavascriptFramework("Angular", "Duplicity of Angular", 5);
        String jsonBody = objectMapper.writeValueAsString(angular);

        mockMvc.perform(post("/frameworks").contentType(JSON).content(jsonBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(
                        "{\"errors\":[{\"field\":null,\"message\":\"Framework with name Angular already exists.\"}]}"
                ));
    }

    @Test
    public void createFramework_withoutName() throws Exception {
        JavascriptFramework noName = new JavascriptFramework(null, "Framework without name", 1);
        String jsonBody = objectMapper.writeValueAsString(noName);

        mockMvc.perform(post("/frameworks").contentType(JSON).content(jsonBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(
                        "{\"errors\":[{\"field\":\"name\",\"message\":\"must not be null\"}]}"
                ));
    }

    @Test
    public void createFramework_withoutRating() throws Exception {
        JavascriptFramework noRating = new JavascriptFramework("Framework", "Framework without rating", null);
        String jsonBody = objectMapper.writeValueAsString(noRating);

        mockMvc.perform(post("/frameworks").contentType(JSON).content(jsonBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(
                        "{\"errors\":[{\"field\":\"rating\",\"message\":\"must not be null\"}]}"
                ));
    }

    @Test
    public void createFramework_zeroRating() throws Exception {
        JavascriptFramework zeroRating = new JavascriptFramework("Framework", "Framework with 0 rating", 0);
        String jsonBody = objectMapper.writeValueAsString(zeroRating);

        mockMvc.perform(post("/frameworks").contentType(JSON).content(jsonBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(
                        "{\"errors\":[{\"field\":\"rating\",\"message\":\"must be between 1 and 5\"}]}"
                ));
    }

    @Test
    public void createFramework_sixRating() throws Exception {
        JavascriptFramework sixRating = new JavascriptFramework("Framework", "Framework with 6 rating", 6);
        String jsonBody = objectMapper.writeValueAsString(sixRating);

        mockMvc.perform(post("/frameworks").contentType(JSON).content(jsonBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(
                        "{\"errors\":[{\"field\":\"rating\",\"message\":\"must be between 1 and 5\"}]}"
                ));
    }

    @Test
    public void updateFramework_notExisting() throws Exception {
        JavascriptFramework angular = new JavascriptFramework("NotExisting", "NotExisting description", 1);
        String jsonBody = objectMapper.writeValueAsString(angular);
        Long id = 0L; // Not existing ID

        mockMvc.perform(put("/frameworks/{id}", id).contentType(JSON).content(jsonBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(
                        "{\"errors\":[{\"field\":null,\"message\":\"Framework with ID 0 not found.\"}]}"
                ));
    }

    @Test
    public void updateFramework_updateDescriptionAndRating() throws Exception {
        JavascriptFramework angular = new JavascriptFramework("Angular", "Angular is no longer decent", 1);
        angular.addVersion(new FrameworkVersion("1.0", LocalDate.of(2010, 10, 10)));
        angular.addVersion(new FrameworkVersion("2.0", LocalDate.of(2011, 11, 11)));
        angular.addVersion(new FrameworkVersion("7.2", null));
        String jsonBody = objectMapper.writeValueAsString(angular);
        Long id = repository.getByName("Angular").getId();

        mockMvc.perform(put("/frameworks/{id}", id).contentType(JSON).content(jsonBody))
                .andExpect(status().isOk());

        JavascriptFramework updatedAngular = repository.getByName("Angular");
        assertThat(updatedAngular, notNullValue());
        assertThat(updatedAngular.getDescription(), is("Angular is no longer decent"));
        assertThat(updatedAngular.getRating(), is(1));
        assertThat(updatedAngular.getVersions(), hasSize(3));
    }

    @Test
    public void updateFramework_updateDeprecationDates() throws Exception {
        JavascriptFramework angular = new JavascriptFramework("Angular", "Angular is desent", 3);
        angular.addVersion(new FrameworkVersion("1.0", LocalDate.of(2010, 1, 1)));
        angular.addVersion(new FrameworkVersion("2.0", null));
        angular.addVersion(new FrameworkVersion("7.2", LocalDate.of(2020, 12, 20)));
        String jsonBody = objectMapper.writeValueAsString(angular);
        Long id = repository.getByName("Angular").getId();

        mockMvc.perform(put("/frameworks/{id}", id).contentType(JSON).content(jsonBody))
                .andExpect(status().isOk());

        JavascriptFramework updatedAngular = repository.getByName("Angular");
        assertThat(updatedAngular, notNullValue());
        assertThat(updatedAngular.getVersions(), hasSize(3));
        assertThat(
                updatedAngular.getVersions(),
                hasItems(
                        allOf(
                                hasProperty("version", is("1.0")),
                                hasProperty("deprecationDate", is(LocalDate.of(2010, 1, 1)))
                        ),
                        allOf(
                                hasProperty("version", is("2.0")),
                                hasProperty("deprecationDate", nullValue())
                        ),
                        allOf(
                                hasProperty("version", is("7.2")),
                                hasProperty("deprecationDate", is(LocalDate.of(2020, 12, 20)))
                        )
                )
        );
    }

    @Test
    public void updateFramework_addVersion() throws Exception {
        JavascriptFramework angular = new JavascriptFramework("Angular", "Angular is desent", 3);
        angular.addVersion(new FrameworkVersion("1.0", LocalDate.of(2010, 10, 10)));
        angular.addVersion(new FrameworkVersion("2.0", LocalDate.of(2011, 11, 11)));
        angular.addVersion(new FrameworkVersion("7.2", null));
        angular.addVersion(new FrameworkVersion("7.3", null));
        String jsonBody = objectMapper.writeValueAsString(angular);
        Long id = repository.getByName("Angular").getId();

        mockMvc.perform(put("/frameworks/{id}", id).contentType(JSON).content(jsonBody))
                .andExpect(status().isOk());

        JavascriptFramework updatedAngular = repository.getByName("Angular");
        assertThat(updatedAngular, notNullValue());
        assertThat(updatedAngular.getVersions(), hasSize(4));
        assertThat(updatedAngular.getVersions(), hasItem(hasProperty("version", is("7.3"))));
    }

    @Test
    public void updateFramework_removeVersion() throws Exception {
        JavascriptFramework angular = new JavascriptFramework("Angular", "Angular is desent", 3);
        angular.addVersion(new FrameworkVersion("1.0", LocalDate.of(2010, 10, 10)));
        angular.addVersion(new FrameworkVersion("2.0", LocalDate.of(2011, 11, 11)));
        String jsonBody = objectMapper.writeValueAsString(angular);
        Long id = repository.getByName("Angular").getId();

        mockMvc.perform(put("/frameworks/{id}", id).contentType(JSON).content(jsonBody))
                .andExpect(status().isOk());

        JavascriptFramework updatedAngular = repository.getByName("Angular");
        assertThat(updatedAngular, notNullValue());
        assertThat(updatedAngular.getVersions(), hasSize(2));
        assertThat(updatedAngular.getVersions(), not(hasItem(hasProperty("version", is("7.2")))));
    }

    @Test
    public void deleteFramework() throws Exception {
        Long id = repository.getByName("Angular").getId();

        mockMvc.perform(delete("/frameworks/{id}", id))
                .andExpect(status().isOk());

        JavascriptFramework angular = repository.getByName("Angular");
        assertThat(angular, nullValue());
    }

    @Test
    public void fulltextSearch_textIs() throws Exception {
        String text = "is";

        mockMvc.perform(get("/frameworks/search").param("text", text))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", hasItems("Angular", "React")));
    }

    @Test
    public void fulltextSearch_textBetter() throws Exception {
        String text = "better";

        mockMvc.perform(get("/frameworks/search").param("text", text))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[*].name", hasItem("React")));
    }

    @Test
    public void fulltextSearch_textReact() throws Exception {
        String text = "react";

        mockMvc.perform(get("/frameworks/search").param("text", text))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[*].name", hasItem("React")));
    }

    @Test
    public void fulltextSearch_textBSJDKFLS() throws Exception {
        String text = "BSJDKFLS";

        mockMvc.perform(get("/frameworks/search").param("text", text))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void fulltextSearch_textAngularReact() throws Exception {
        String text = "angular react";

        mockMvc.perform(get("/frameworks/search").param("text", text))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", hasItems("Angular", "React")));
    }

}
