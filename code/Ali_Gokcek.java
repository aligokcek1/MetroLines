//Ali Gökçek
//2021400012
//20.03.2023

/*
Briefly, my code starts with creating necessary lists and arrays
Then it fill them with by reading .txt file line by line
After that it gets the inputs from user and check whether they are valid or not
For the most important part which is finding the path, I used a recursive function that I explained below
Some inputs may give false output, so I add a double-check part that checks the output by taking the reversed direction as inputs
After finding the path, It starts to draw canvas and fixed elements such as lines and background
Finally adds the animation
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.awt.Font;
import java.util.ArrayList;
public class Ali_Gokcek {

    public static void main(String[] args) throws FileNotFoundException {
        //CREATING ARRAY OF LINE NAMES AND 2D ARRAY OF RGB VALUES:
        String[] linesArray = new String[]{"B1", "M1A", "M1B", "M2", "M4", "M5", "M6", "M7", "M9", "M11"};
        int[][] rgbArray = new int[][]{
                {101,102,106}, {225,30,28}, {141,31,22}, {0,152,66},
                {231,27,116}, {97,38,93}, {201,171,120}, {242,158,192},
                {254,211,0}, {165,90,150}};

        //CREATING BREAKPOINTS LIST:
        ArrayList<String> breakPoints = new ArrayList<String>();
        String[] dumlist = new String[]{"Yenikapi","Otogar","Kagithane","Levent","Sisli-Mecidiyekoy","Uskudar","AyrilikCesmesi"};

        //INDEX(linesArray) OF LINES THAT BREAKPOINTS CONNECT
        int[][] conIndex = new int[][]{{0, 1, 3},{1,2},{7,9},{3,6},{3,7},{0,5},{0,4}};
        for (int t = 0; t< dumlist.length;t++){
            breakPoints.add(dumlist[t]);
        }

        //READING INPUT FILE AND CREATING INFORMATION ARRAYS:
        //ARRAYS OF ARRAY LISTS: (10 IS THE NUMBER OF LINES)
        ArrayList<String>[] stationNames = new ArrayList[10];
        ArrayList<Integer>[] stationXCoo = new ArrayList[10];
        ArrayList<Integer>[] stationYCoo = new ArrayList[10];
        ArrayList<Boolean>[] stationIsStarred = new ArrayList[10];

        //stationConnectedLines[lineIndex].get(stationIndex)[0 to max of 2]
        ArrayList<int[]>[] stationConnectedLines = new ArrayList[10];

        String fileName = "coordinates.txt";
        File file = new File(fileName);
        Scanner inputFile = new Scanner(file);

        int j = 0;
        while (inputFile.hasNextLine()) {
            String line = inputFile.nextLine();
            String firstLetter = Character.toString(line.charAt(0));
            //SPLITTING LINES STARTING WITH STARS WHICH HAVE STATION NAMES AND COORDINATES
            if (firstLetter.equals("*")) {
                stationXCoo[j] = new ArrayList<Integer>();
                stationYCoo[j] = new ArrayList<Integer>();
                stationNames[j] = new ArrayList<String>();
                stationConnectedLines[j] = new ArrayList<int[]>();
                stationIsStarred[j] = new ArrayList<Boolean>();
                String[] splittedLine;
                splittedLine = line.split(" ");
                for (int i = 0; i < splittedLine.length; i++) {
                    String name;
                    int x;
                    int y;
                    boolean star = false;
                    if (i % 2 == 0) { //INDEXES OF LINES' NAMES ON EACH txt LINE
                        String first = Character.toString(splittedLine[i].charAt(0));
                        if (first.equals("*")){      //EXCLUDING STAR FROM THE BEGINNING OF STATION NAMES
                            name = splittedLine[i].substring(1);
                            star = true;  //IF IT WILL BE DISPLAYED ON THE CANVAS OR NOT
                        }
                        else {
                            name = splittedLine[i];
                        }
                        stationNames[j].add(name);
                        stationIsStarred[j].add(star);


                        //ADDING CONNECTING LINE INFOs
                        int[] dum;
                        if (breakPoints.contains(name)){ //IF IT'S A BREAKPOINT PUT THE INFO ON THE BREAKPOINT LIST
                            dum = new int[conIndex[breakPoints.indexOf(name)].length];
                            for(int l = 0; l<conIndex[breakPoints.indexOf(name)].length;l++){
                                dum[l] = conIndex[breakPoints.indexOf(name)][l];
                            }
                        }
                        else{
                            dum = new int[1];
                            dum[0] = j;
                        }
                        stationConnectedLines[j].add(dum);



                    } else if(i%2 ==1) {  //INDEXES OF COORDINATES ON EACH txt LINE
                        String[] coordinatesSplitted = splittedLine[i].split(",");
                        x = Integer.parseInt(coordinatesSplitted[0]);
                        y = Integer.parseInt(coordinatesSplitted[1]);
                        stationXCoo[j].add(x);
                        stationYCoo[j].add(y);
                    }
                }
                j++;
            }
        }
        inputFile.close();



        //TAKING STATION INPUT FROM USER:
        Scanner sc= new Scanner(System.in);
        String from = sc.nextLine();
        String to = sc.nextLine();
        sc.close();

        //CHECKING IF INPUTS ARE VALID:
        int isThereFrom = 0;
        int isThereTo = 0;
        int lineindfrom = 0;
        int statindfrom = 0;
        int lineindto = 0;
        int statindto = 0;

        for(int li = 0; li < stationNames.length; li++){   //CHECKING ALL THE STATION NAMES
            for (int si = 0; si < stationNames[li].size(); si++){

                if(stationNames[li].get(si).equals(from)){
                    isThereFrom = 1;
                    lineindfrom = li;
                    statindfrom = si;
                }
                else if(stationNames[li].get(si).equals(to)){
                    isThereTo = 1;
                    lineindto = li;
                    statindto = si;
                }
            }
        }

        //IF INPUTS ARE NOT VALID, PRINT OUT THE ERROR CODE AND EXIT:
        if (isThereFrom+isThereTo != 2) {
            System.out.println("The station names provided are not present in this map.");
            System.exit(1);
        }

        //FINDING THE PATH:
        ArrayList<String> outputPath = new ArrayList<>();

        ArrayList<String> emptyCheckedLinesList = new ArrayList<>();

        //pathFinder FUNCTION GIVES US A STRING OF CONCATENATED STATION NAMES
        //pathFinder takes "stationNames Array, stationConnectedLines Array,
        // from(which line) , to(which line),connectedlines (of from), checkedstationlist(empty at first)"
        String path = pathFinder(stationNames,stationConnectedLines,from,to,stationConnectedLines[lineindfrom].get(statindfrom),emptyCheckedLinesList);
        String[] splittedPath = path.split(" ");
        outputPath.add(from);
        for (int o = 0; o< splittedPath.length; o++){
            outputPath.add(splittedPath[o]);
        }

        //DOUBLE-CHECK THE PATH BY REVERSING THE DIRECTION
        emptyCheckedLinesList.clear();
        String reversedPath = pathFinder(stationNames,stationConnectedLines,to,from,stationConnectedLines[lineindto].get(statindto),emptyCheckedLinesList);
        ArrayList<String> reversedOutputPath = new ArrayList<>();
        String[] splittedReversedPath = reversedPath.split(" ");
        reversedOutputPath.add(to);
        for (int i = 0; i< splittedReversedPath.length; i++){
            reversedOutputPath.add(splittedReversedPath[i]);
        }
        for (int a = 0, b = reversedOutputPath.size() - 1; a < b; a++) {
            reversedOutputPath.add(a, reversedOutputPath.remove(b));
        }

        if((!splittedPath[splittedPath.length-1].equals(to)) && (reversedOutputPath.get(0).equals(from) && reversedOutputPath.get(reversedOutputPath.size()-1).equals(to))){
            outputPath.clear();
            outputPath = reversedOutputPath;
        }


        //IF pathFinder CANNOT REACH TO THE STATION THAT WE WANT TO GO, PRINT THE ERROR CODE AND EXIT
        if (!outputPath.get(outputPath.size()-1).equals(to)){
            System.out.println("These two stations are not connected");
            System.exit(1);
        }
        else{
            for(int m = 0;m<outputPath.size();m++){
                System.out.println(outputPath.get(m));
            }
        }

        //SETTING CANVAS:
        int canvas_width = 1024;
        int canvas_height = 482;
        double pen_radius_of_lines = 0.012;
        double pen_radius_of_stations = 2.0;

        int pauseDuration = 400;
        StdDraw.setFont(new Font("Helvetica Bold", Font.BOLD,8));
        StdDraw.setCanvasSize(canvas_width, canvas_height);
        StdDraw.setXscale(0, 1024);
        StdDraw.setYscale(0, 482);
        StdDraw.picture(512,241,"background.jpg");

        //DRAWING LINES:
        for (int lineind = 0; lineind < linesArray.length; lineind++){
            for(int stlocind = 1; stlocind < stationNames[lineind].size(); stlocind++){
                StdDraw.setPenColor(rgbArray[lineind][0],rgbArray[lineind][1],rgbArray[lineind][2]);
                StdDraw.setPenRadius(pen_radius_of_lines);
                StdDraw.line(stationXCoo[lineind].get(stlocind-1),stationYCoo[lineind].get(stlocind-1),
                        stationXCoo[lineind].get(stlocind), stationYCoo[lineind].get(stlocind));
            }
        }
        //DRAWING STATIONS AND WRITING THEIR NAMES:
        for (int lineind = 0; lineind < linesArray.length; lineind++){
            for(int stlocind = 0; stlocind < stationNames[lineind].size(); stlocind++){
                //DRAWING SMALL WHITE CIRCLES
                StdDraw.setPenColor(StdDraw.WHITE);
                StdDraw.filledCircle(stationXCoo[lineind].get(stlocind), stationYCoo[lineind].get(stlocind), pen_radius_of_stations);
                //WRITING NAMES OF STARRED STATIONS
                if (stationIsStarred[lineind].get(stlocind)){
                    StdDraw.setFont(new Font("Helvetica Bold", Font.BOLD,8));
                    StdDraw.setPenColor(StdDraw.BLACK);
                    StdDraw.text(stationXCoo[lineind].get(stlocind),stationYCoo[lineind].get(stlocind)+5,
                            stationNames[lineind].get(stlocind));
                }
            }
        }
        ArrayList<int[]> orangedStations = new ArrayList<>(); //PASSED STATIONS
        StdDraw.enableDoubleBuffering();

        //CURRENT STATION ANIMATION:
        for (int pathIndex = 0; pathIndex < outputPath.size();pathIndex++){
            String currentStation = outputPath.get(pathIndex);
            int[] lnstIndexes = new int[2];
            for (int line = 0; line < stationNames.length; line++){
                for (int st = 0; st < stationNames[line].size(); st++){
                    if (currentStation.equals(stationNames[line].get(st))){
                        for (int ind = 0; ind < orangedStations.size(); ind++){
                            StdDraw.setPenColor(StdDraw.PRINCETON_ORANGE);
                            int lineindex = orangedStations.get(ind)[0];
                            int statindex = orangedStations.get(ind)[1];
                            //IF A STATION PASSED, DRAW A SMALL ORANGE CIRCLE
                            StdDraw.filledCircle(stationXCoo[lineindex].get(statindex),stationYCoo[lineindex].get(statindex), 1.7);
                        }
                        //DRAWING BIG CURRENT STATION CIRCLE
                        StdDraw.setPenColor(StdDraw.PRINCETON_ORANGE);
                        StdDraw.filledCircle(stationXCoo[line].get(st),stationYCoo[line].get(st), 3.3);
                        StdDraw.show();
                        StdDraw.pause(pauseDuration);
                        lnstIndexes[0] = line;
                        lnstIndexes[1] = st;
                        orangedStations.add(lnstIndexes);
                        if (pathIndex != outputPath.size()-1){
                            //CLEAR THE BIG ORANGE CIRCLE TO DRAW SMALLER ORANGE CIRCLE ON ITS LOCATION
                            StdDraw.setPenColor(rgbArray[line][0],rgbArray[line][1],rgbArray[line][2]);
                            StdDraw.filledCircle(stationXCoo[line].get(st),stationYCoo[line].get(st), 3.3);
                        }
                    }
                }
            }

        }
    }

    private static String pathFinder(ArrayList<String>[] stationNames,ArrayList<int[]>[] stationConnectedLines, String from, String to, int[] fromLineIndexes,ArrayList<String> checked){

        ArrayList<String> checkedStations = checked;
        String pathPiece = "";
        boolean found = true;
        int fromind = 0;
        int toind = 0;
        //WE WANT TO GO FROM "FROM" STATION TO "TO" STATION

        //FIRSTLY CHECKING THE LINES THAT CONTAIN "FROM".
        //IF ONE OF IT HAS "TO", RETURN THE PATH BLOCK
        for (int a = 0; a< fromLineIndexes.length;a++){
            int fromline = (int) fromLineIndexes[a];
            for (int c = 0; c < stationNames[fromline].size(); c++){
                if (stationNames[fromline].get(c).equals(from)){
                    fromind = c;    //INDEX OF "FROM" ON LINES
                }
            }
            for (int b = 0; b < stationNames[fromline].size(); b++) {
                if (stationNames[fromline].get(b).equals(to)) {             //IF WE FOUND "TO"
                    found = false;
                    toind = b;
                }
            }
            if (!found) {
                if (toind < fromind) {
                    while (fromind != toind) {
                        pathPiece += stationNames[fromline].get(fromind-1) + " ";
                        fromind -= 1;
                    }
                } else {
                    while (fromind != toind) {
                        pathPiece += stationNames[fromline].get(fromind + 1) + " ";
                        fromind += 1;
                    }
                }
                return pathPiece;
            }
        }

        //IF WE COULD NOT FIND "TO" IN ONE OF CONNECTED LINES:
        //FIRSTLY WE WANT TO FIND AND GO TO A BREAKPOINT
        //THEN RECALL THE FUNCTION WITH NEW "FROM" WHICH IS THE BREAKPOINT
        if (found){
            for (int i = 0; i< fromLineIndexes.length;i++){
                if (!found){
                    break;
                }
                int fromline = (int) fromLineIndexes[i];
                fromind = 0;
                toind = 0;
                for (int c = 0; c < stationNames[fromline].size(); c++){
                    if (stationNames[fromline].get(c).equals(from)){
                        fromind = c;
                    }
                }
                for (int j = 0; j < stationNames[fromline].size(); j++){
                    if (j == fromind){    //IF WE WERE ON A BREAKPOINT BEFORE IN ORDER TO PREVENT INFINITE LOOP WE PASS "FROM"
                        continue;
                    }
                    if(checkedStations.contains(stationNames[fromline].get(j))){
                        continue;           //IF WE CHECKED THESE BREAKPOINTS BEFORE TO PREVENT INFINITE LOOP WE PASS THEM
                    }

                    if (stationConnectedLines[fromline].get(j).length <= 1) {
                        continue;           //IF IT IS NOT A BREAKPOINT PASS IT
                    }
                    toind = j;
                    checkedStations.add(stationNames[fromline].get(j));
                    if (toind < fromind){       //ADD THE STATIONS ON THE PATH
                        while (fromind != toind){
                            pathPiece += stationNames[fromline].get(fromind-1) + " ";
                            fromind -= 1;
                        }
                    }
                    else{
                        while (fromind != toind) {
                            pathPiece += stationNames[fromline].get(fromind + 1) + " ";
                            fromind += 1;
                        }
                    }
                    return pathPiece + pathFinder(stationNames,stationConnectedLines,stationNames[fromline].get(toind),to,stationConnectedLines[fromline].get(toind),checkedStations);
                }
            }
        }
        //IF WE CANNOT FIND "TO" ON ANY OF CONNECTED LINES OF "FROM", RETURN EMPTY STRING
        return pathPiece;
    }
}





