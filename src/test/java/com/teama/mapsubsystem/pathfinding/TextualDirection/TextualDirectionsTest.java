package com.teama.mapsubsystem.pathfinding.TextualDirection;

import com.teama.mapsubsystem.data.CSVDatabaseSource;
import com.teama.mapsubsystem.data.MapNode;
import com.teama.mapsubsystem.pathfinding.AStar.AStar;
import com.teama.mapsubsystem.pathfinding.Path;
import com.teama.translator.Translator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class TextualDirectionsTest {
    GenerateTextTestMap generater1 ;
    @org.junit.Before
    public void setUp() throws Exception {
         generater1 = new GenerateTextTestMap();

    }

    @org.junit.Test
    public void generateDirections() throws Exception {

        Translator.getInstance().setLang("en");

        AStar pathGen = new AStar();
        MapNode start = generater1.fullMap.get(0);
        MapNode end = generater1.fullMap.get(generater1.fullMap.size()-1);
        Path testPath = pathGen.generatePath(start,end);

        TextualDirections textGenerator = new TextualDirections();
        TextDirections output = textGenerator.generateDirections(testPath);
        ArrayList<Direction> directions = output.getDirections();
        printEverything(output.getDirections());

        assertTrue(directions.get(0).getDescription().contains("Start walking towards X:12 Y:87"));
        assertTrue(directions.get(directions.size()-1).getDescription().contains("you will reach your destination"));
        printEverything(output.getDirections());

    }





    private void printEverything(ArrayList<Direction> directions )
    {
        for (Direction dir : directions) {
            System.out.println(dir.getDescription());
        }
    }

}