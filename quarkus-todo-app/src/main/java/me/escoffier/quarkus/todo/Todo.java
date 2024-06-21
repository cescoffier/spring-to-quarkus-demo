package me.escoffier.quarkus.todo;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotBlank;


@Entity
public class Todo extends PanacheEntity {

    @NotBlank
    public String title;

    public boolean completed;

    @Column(name = "ordering")
    public int order;

    public static void clearCompleted() {
        delete("completed", true);
    }
}
