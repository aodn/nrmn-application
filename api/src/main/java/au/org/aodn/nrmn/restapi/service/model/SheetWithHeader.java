package au.org.aodn.nrmn.restapi.service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.List;

@Getter
@AllArgsConstructor
public class SheetWithHeader {
    private String fileId;
    private List<HeaderCellIndex> header;
    private Sheet sheet;
}
