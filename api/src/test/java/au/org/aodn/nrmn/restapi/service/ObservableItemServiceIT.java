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
    public void verifyUpdateSupersededCorrect() throws InvocationTargetException, IllegalAccessException {
        Optional<ObservableItem> observableItem = observableItemRepository.findById(333);

        ObservableItemPutDto putDto = new ObservableItemPutDto();
        putDto.setLengthWeightA(10.0);
        putDto.setLengthWeightB(11.0);
        putDto.setLengthWeightCf(12.0);

        Integer ss = observableItemService.updateSupersededByObservableItem(333, putDto);

        Optional<ObservableItem> reloadObservableItem = observableItemRepository.findById(333);
        Optional<ObservableItem> reloadSupersededItem = observableItemRepository.findById(ss);

        // This is the value in test pack
        assertNull("Length Weight A not match", reloadObservableItem.get().getLengthWeight());
        assertNull("Length Weight B not match", reloadObservableItem.get().getLengthWeight());
        assertNull("Length Weight Cf not match", reloadObservableItem.get().getLengthWeight());

        // No change in value for 333 as you just want to update superseded
        assertTrue(EqualsBuilder.reflectionEquals(observableItem,reloadObservableItem));

        assertEquals("Length Weight A updated", putDto.getLengthWeightA(), reloadSupersededItem.get().getLengthWeight().getA());
        assertEquals("Length Weight B updated", putDto.getLengthWeightB(), reloadSupersededItem.get().getLengthWeight().getB());
        assertEquals("Length Weight Cf updated", putDto.getLengthWeightCf(), reloadSupersededItem.get().getLengthWeight().getCf());
    }
}
