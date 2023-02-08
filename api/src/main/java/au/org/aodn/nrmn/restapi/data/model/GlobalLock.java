package au.org.aodn.nrmn.restapi.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "global_lock")
public class GlobalLock {

    @Id
    private Long id;

    @Column(nullable = false)
    private boolean locked;

    public GlobalLock() {
        this.id = 1L;
        this.locked = true;
    }
}
