package au.org.aodn.nrmn.restapi.model.db;

import javax.persistence.*;

@Entity
@Table(name = "observable_item_ref", schema = "nrmn", catalog = "nrmn")
public class ObservableItemRefEntity {
    private int observableItemId;
    private String observableItemName;
    private String obsItemAttribute;

    @Id
    @Column(name = "observable_item_id")
    public int getObservableItemId() {
        return observableItemId;
    }

    public void setObservableItemId(int observableItemId) {
        this.observableItemId = observableItemId;
    }

    @Basic
    @Column(name = "observable_item_name")
    public String getObservableItemName() {
        return observableItemName;
    }

    public void setObservableItemName(String observableItemName) {
        this.observableItemName = observableItemName;
    }

    @Basic
    @Column(name = "obs_item_attribute")
    public String getObsItemAttribute() {
        return obsItemAttribute;
    }

    public void setObsItemAttribute(String obsItemAttribute) {
        this.obsItemAttribute = obsItemAttribute;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ObservableItemRefEntity that = (ObservableItemRefEntity) o;

        if (observableItemId != that.observableItemId) return false;
        if (observableItemName != null ? !observableItemName.equals(that.observableItemName) : that.observableItemName != null)
            return false;
        if (obsItemAttribute != null ? !obsItemAttribute.equals(that.obsItemAttribute) : that.obsItemAttribute != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = observableItemId;
        result = 31 * result + (observableItemName != null ? observableItemName.hashCode() : 0);
        result = 31 * result + (obsItemAttribute != null ? obsItemAttribute.hashCode() : 0);
        return result;
    }
}
