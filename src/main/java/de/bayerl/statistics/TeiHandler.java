package de.bayerl.statistics;

import com.google.common.base.Stopwatch;
import de.bayerl.statistics.model.Table;
import de.bayerl.statistics.transformer.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TeiHandler {

    // TODO get metadata from TEI
    // TODO set column headers
    // TODO generate CUBE


    public static void handle() {
        Stopwatch stopwatch = Stopwatch.createStarted();
        Stopwatch singleStepWatch = Stopwatch.createStarted();

        // Load table
        List<Table> tables = TeiLoader.loadFiles();

        // only work with a small subset
        tables = tables.subList(0, 10);

        Table table = new Table();
        for (Table t : tables) {
            table.getRows().addAll(t.getRows());
        }

        System.out.println(tables.size() + " Table(s) loaded in " + singleStepWatch.elapsed(TimeUnit.MILLISECONDS) + " ms.");
        singleStepWatch.reset();
        singleStepWatch.start();

        // Prepare transformations
        List<Transformation> transformations = new ArrayList<>();
        transformations.add(new ResolveLinebreaks());
        transformations.add(new ResolveRowSpan());
        transformations.add(new ResolveColSpan());

        transformations.add(new DeleteRowCol(false, 5));
        int[] rows = {0, 1, 2};
        transformations.add(new DeleteMatchingRow("den freien Verkehr", rows));
        transformations.add(new DeleteMatchingRow("Nummern der Waarenverzeichnisse", rows));
        transformations.add(new DeleteMatchingRow("Die erste, laufende Nummer bezieht sich", rows));
        transformations.add(new DeleteMatchingRow("Zusammen Ctr.", rows));
        transformations.add(new DeleteMatchingRow("Soweit sie nicht unter", rows));
        transformations.add(new DeleteMatchingRow("Mit Ausn. der unter", rows));

        transformations.add(new SetValue("31. (274.) Pos. 5 a.", 239, 0));
        transformations.add(new SetValue("35. (218.) Pos. 5 c.", 268, 0));

        transformations.add(new SetType("data", 410, 4));
        transformations.add(new SetType("data", 268, 2));
        transformations.add(new SetType("data", 476, 4));
        transformations.add(new SetType("data", 628, 4));

        transformations.add(new ResolveLabelUnits());
        transformations.add(new DeleteRowCol(false, 4));
        transformations.add(new NormalizeCompoundTables());

        transformations.add(new SplitColumn("─", 4));

//        transformations.add(new DeleteRowCol(true, 41));
//        transformations.add(new DeleteRowCol(true, 21));
//        transformations.add(new DeleteRowCol(true, 20));




//        transformations.add(new DeleteRowCol(true, 52));
//        transformations.add(new DeleteRowCol(true, 7));
//        transformations.add(new DeleteRowCol(true, 6));
//        transformations.add(new DeleteRowCol(false, 1));
//        transformations.add(new DeleteRowCol(false, 0));
//        int[] rows = {1, 2, 3, 4, 5};
//        transformations.add(new CombineRows(rows));
//        transformations.add(new DeleteRowCol(true, 5));
//        transformations.add(new DeleteRowCol(true, 2));
//        transformations.add(new NormalizeTable());

        TablePrinter.printHTML(table, "0_original");

        int i = 0;
        // do transformations
        for (Transformation transformer : transformations) {
            table = transformer.transform(table);
            i++;
            System.out.println("Table processed in " + singleStepWatch.elapsed(TimeUnit.MILLISECONDS) + " ms. " + transformer.getName());
            singleStepWatch.reset();
            singleStepWatch.start();
            TablePrinter.printHTML(table, "" + i + "_" + transformer.getName());
        }

        // TODO write somewhere

        System.out.println("Done in " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + " ms.");
    }




}