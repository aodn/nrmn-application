package au.org.aodn.nrmn.restapi.service;

import au.org.aodn.nrmn.restapi.model.db.Diver;
import au.org.aodn.nrmn.restapi.repository.DiverRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class CsvService {
    private static final CSVFormat DIVERS_FORMAT = CSVFormat.DEFAULT.withHeader("INITIALS", "FULL NAME");

    @Autowired
    DiverRepository diverRepository;

    public void getDiversCsv(PrintWriter writer) throws IOException {
        CSVPrinter csvPrinter = DIVERS_FORMAT.print(writer);
        List<Diver> divers = diverRepository.findAll();
        List<List<String>> records = divers.stream()
                .filter(d -> d.getInitials().matches("[A-Z]*"))
                .map(this::getDiverAsCsvRecord).collect(toList());
        csvPrinter.printRecords(records);
    }

    private List<String> getDiverAsCsvRecord(Diver diver) {
        return Arrays.asList(diver.getInitials(), diver.getFullName());
    }
}
