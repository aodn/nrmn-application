package au.org.aodn.nrmn.restapi.service;

import au.org.aodn.nrmn.restapi.data.model.ObservableItem;
import au.org.aodn.nrmn.restapi.data.repository.ObservableItemRepository;
import au.org.aodn.nrmn.restapi.dto.observableitem.ObservableItemNodeDto;
import au.org.aodn.nrmn.restapi.dto.observableitem.ObservableItemPutDto;
import au.org.aodn.nrmn.restapi.test.PostgresqlContainerExtension;
import au.org.aodn.nrmn.restapi.test.annotations.WithTestData;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.transaction.Transactional;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@Testcontainers
@SpringBootTest
@Transactional
@WithTestData
@ExtendWith(PostgresqlContainerExtension.class)
public class ObservableItemServiceIT {

    @Autowired
    protected ObservableItemService observableItemService;

    @Autowired
    protected ObservableItemRepository observableItemRepository;

    @Test
    public void verifyCreateTreeSingleNode() {
        Optional<ObservableItem> observableItem = observableItemRepository.findById(331);
        ObservableItemNodeDto root = observableItemService.createForestOf(observableItem.get());

        assertNull("Parent is null", root.parent);
        assertEquals("No child", 0, root.children.size());
        assertEquals("Point to self correct", observableItem.get().getObservableItemId(), root.self.getObservableItemId());
    }

    @Test
    public void verifyCreateTreeMultipleNode() {
        Optional<ObservableItem> observableItem = observableItemRepository.findById(333);
        ObservableItemNodeDto root = observableItemService.createForestOf(observableItem.get());

        assertNull("Root no parent is null", root.parent);
        assertEquals("Root 1 child", 1, root.children.size());
        assertEquals("Root id correct", Integer.valueOf(330), root.self.getObservableItemId());

        ObservableItemNodeDto child0 = root.children.get(0);
        assertEquals("Root child id correct", Integer.valueOf(332), child0.self.getObservableItemId());
        assertEquals("Child 0 has 2 child", 2, child0.children.size());

        ObservableItemNodeDto child00 = child0.children.get(0);
        ObservableItemNodeDto child01 = child0.children.get(1);

        assertTrue("Child00 id correct", Arrays.asList(333,334).contains(child00.self.getObservableItemId()));
        assertTrue("Child01 id correct", Arrays.asList(333,334).contains(child01.self.getObservableItemId()));
    }

    @Test
    public void verifyFindRootOfWithInvalidSupersededBySpecies() {
        // This test required some knowledge of internal behavior, we test a case where the item with supersededby
        // is an invalid item, that cause the same item return.
        ObservableItem i = new ObservableItem();
        i.setSupersededBy("bird");

        assertEquals("Root node with invalid superseded by works", i, observableItemService.findRootOf(i));
    }
}
