package me.escoffier.spring.todo;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
public class TodoController {
    private final TodoRepository todoRepository;

    public TodoController(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Todo> getAll() {
        return todoRepository.findAll();
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Todo getOne(@PathVariable("id") Long id) {
        return todoRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo with id of " + id + " does not exist.")
        );
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<Todo> create(@Valid @RequestBody Todo item) throws URISyntaxException {
        Todo result = todoRepository.save(item);
        return ResponseEntity.created(new URI("/api/" + result.getId())).body(result);
    }

    @PatchMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public Todo update(@Valid @RequestBody Todo todo, @PathVariable("id") Long id) {
        Todo entity = todoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo with id of " + id + " does not exist."));
        entity.setId(id);
        entity.setCompleted(todo.isCompleted());
        entity.setOrder(todo.getOrder());
        entity.setTitle(todo.getTitle());
        Todo result = todoRepository.save(entity);
        return result;
    }

    @DeleteMapping
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompleted() {
        todoRepository.clearCompleted();
    }

    @DeleteMapping("/{id}")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOne(@PathVariable("id") Long id) {
        Todo entity = todoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo with id of " + id + " does not exist."));
        todoRepository.delete(entity);
    }
}