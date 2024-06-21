package me.escoffier.quarkus.todo;

import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping(value = "/api")
@RunOnVirtualThread
public class TodoController {


    @GetMapping
    public List<Todo> getAll() {
        return Todo.listAll();
    }

    @GetMapping(path = "/{id}")
    public Todo getOne(Long id) {
        Todo todo = Todo.findById(id);
        if (todo == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo with id of " + id + " does not exist.");
        }
        return todo;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<Todo> create(@Valid Todo item) throws URISyntaxException {
        item.persist();
        return ResponseEntity.created(new URI("/api/" + item.id)).body(item);
    }

    @PatchMapping(path = "/{id}")
    @Transactional
    public Todo update(@Valid @RequestBody Todo todo, @PathVariable("id") Long id) {
        Todo entity = Todo.<Todo>findByIdOptional(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo with id of " + id + " does not exist."));
        entity.completed = todo.completed;
        entity.order = todo.order;
        entity.title = todo.title;
        return entity;
    }

    @DeleteMapping
    @Transactional
    public ResponseEntity<Void> deleteCompleted() {
        Todo.clearCompleted();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteOne(Long id) {
        Todo entity = Todo.<Todo>findByIdOptional(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo with id of " + id + " does not exist."));
        entity.delete();
        return ResponseEntity.noContent().build();
    }
}