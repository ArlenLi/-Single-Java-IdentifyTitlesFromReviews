
import java.io.*;
import java.util.*;

/**
 * Created by lld on 16/4/17.
 */
public class TitleMatching {
    private ArrayList<String> titleList = new ArrayList<>();    // containing all titles
    private String reviewFileFolder = "";   // the folder of review files
    private String titleFile = "";  // the file containing all titles
    private String handCheckResultsFile ="";  // the file containing handCheck results
    private Ngram nGram = new Ngram();
//    private File nGramResult = null;    // the file containing results from the nGram
//    private File sWResult = null;       // the file containing results from the SmithWaterman
//    private FileWriter nGramfw = null;
//    private FileWriter smithWatermanfw = null;
//    private BufferedWriter nGrambw = null;
//    private BufferedWriter smithWatermanbw = null;
    private HashMap<String, String> handCheckResults = new HashMap<>();
    private File totalResult = null;
    private FileWriter totalResultfw = null;
    private BufferedWriter totalResultbw = null;

    public TitleMatching() {
        try {
//            nGramResult = new File("nGramResult.txt");
//            sWResult = new File("smithWaterResult.txt");
//            nGramfw = new FileWriter(nGramResult);
//            smithWatermanfw = new FileWriter(sWResult);
//            nGrambw = new BufferedWriter(nGramfw);
//            smithWatermanbw = new BufferedWriter(smithWatermanfw);

            // total result output
            totalResult = new File("totalResult3.csv");
            totalResultfw = new FileWriter(totalResult);
            totalResultbw = new BufferedWriter(totalResultfw);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public ArrayList<String> getTitleList() {
        return titleList;
    }

    public String getReviewFileFolder() {
        return reviewFileFolder;
    }

    public String getTitleFile() {
        return titleFile;
    }

    public Ngram getnGram() {
        return nGram;
    }

//    public File getnGramResult() {
//        return nGramResult;
//    }
//
//    public File getsWResult() {
//        return sWResult;
//    }
//
//    public FileWriter getnGramfw() {
//        return nGramfw;
//    }
//
//    public FileWriter getSmithWatermanfw() {
//        return smithWatermanfw;
//    }
//
//    public BufferedWriter getnGrambw() {
//        return nGrambw;
//    }
//
//    public BufferedWriter getSmithWatermanbw() {
//        return smithWatermanbw;
//    }

    // sorting hashmap
    private static class ValueComparator implements Comparator<Map.Entry<String,Integer>>
    {
        public int compare(Map.Entry<String,Integer> m,Map.Entry<String,Integer> n) {
            return n.getValue()-m.getValue();
        }
    }

    private static class ValueComparator2 implements Comparator<Map.Entry<String,Integer>>
    {
        public int compare(Map.Entry<String,Integer> m,Map.Entry<String,Integer> n) {
            return m.getValue()-n.getValue();
        }
    }

    // putting all titles in the title List
    public void processTitles() {
        try {
            File file = new File(titleFile);
            if (file.isFile() && file.exists()) {
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file));
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    if(!"".equals(lineTxt)){
                        titleList.add(lineTxt);
                    }
                }
                read.close();
                bufferedReader.close();
            } else {
                System.out.println("The file cannot be found");
            }
        } catch (Exception e) {
            System.out.println("Errors occur!");
            e.printStackTrace();
        }
    }

    public void receiveHandCheckTitles() {
        try {
            File file = new File(handCheckResultsFile);
            if (file.isFile() && file.exists()) {
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file));
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                String[] result = null;
                StringBuffer title = null;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    if(!"".equals(lineTxt)){
                        title = new StringBuffer();
                        result = lineTxt.split(" ");
                        for (int i = 1; i < result.length; i++) {
                            title.append(result[i] + " ");
                        }
                        title.deleteCharAt(title.length() - 1);
                        handCheckResults.put(result[0], title.toString());
                    }
                }
                read.close();
                bufferedReader.close();
            } else {
                System.out.println("The file cannot be found");
            }
        } catch (Exception e) {
            System.out.println("Errors occur!");
            e.printStackTrace();
        }

    }

    // return the review in one file
    public String getReviewContent(String filePath) {
        InputStream is = null;
        BufferedReader reader = null;
        StringBuffer buffer = new StringBuffer();
        try {
            is = new FileInputStream(filePath);
            String line;

            reader = new BufferedReader(new InputStreamReader(is));
            line = reader.readLine();
            while (line != null) {
                buffer.append(line);
                buffer.append("\n");
                line = reader.readLine();
            }
            reader.close();
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }

    // Using the SmithWaterman algorithm to match titles and reviews
    public void findTitleBySmithWaterman(String review, String filename) {
        try {
            // write the filename to the result file
//            smithWatermanbw.write(filename);
//            smithWatermanbw.newLine();
//            smithWatermanbw.write("Matching titles: ");

            // ouput the fileName and handCheck results
            totalResultbw.write(filename + ", ");
            String plainFileName = filename.substring(0, filename.length() - 4);
            pln(filename);
            pln(plainFileName);
            if (handCheckResults.get(plainFileName).equals("could not be determined")) {
                totalResultbw.write(0 + ", ");
                totalResultbw.write(", ");
            } else {
                totalResultbw.write(1 + ", ");
                totalResultbw.write(handCheckResults.get(plainFileName) + ", ");
            }

            // go through all titles for this review and put the results in titleScores hashmap
            HashMap<String, Integer> titleScores = new HashMap<>();
            for (int i = 0; i < titleList.size(); i++) {
                String title = titleList.get(i);
                // set threshold
                int SWThreshold = title.length() - 2;
                int score = new SmithWaterman(title, review).computeSmithWaterman();
                if (score > SWThreshold) {
                        titleScores.put(title, score);
                }

            }

            // sort titleScores hashmap and output the matching title with the highest scores
            List<Map.Entry<String, Integer>> list = new ArrayList<>();
            list.addAll(titleScores.entrySet());
            TitleMatching.ValueComparator vc = new ValueComparator();
            Collections.sort(list, vc);
            String tempTitle = "";
            int tempScore = 0;
            ArrayList<String> retrivedTitles = new ArrayList<>();
            for(Map.Entry<String,Integer> mapping:list){
                if (!tempTitle.equals("")) {
                    if (tempScore > mapping.getValue()) {
                        break;
                    } else {
                        //smithWatermanbw.write(", " + mapping.getKey());
                        retrivedTitles.add(mapping.getKey());
                    }
                } else {
//                    smithWatermanbw.write(mapping.getKey());
                    retrivedTitles.add(mapping.getKey());
                    tempScore = mapping.getValue();
                    tempTitle = mapping.getKey();
                }
            }

            StringBuffer sWTitle = new StringBuffer();
            String handCheckTitle = handCheckResults.get(plainFileName);
            boolean match = false;
            if (retrivedTitles.size() == 0) {
                totalResultbw.write(", ");
            } else {
                for (int i = 0; i < retrivedTitles.size(); i++) {
                    sWTitle.append(retrivedTitles.get(i) + ";");
                    if (retrivedTitles.get(i).equals(handCheckTitle)) {
                        match = true;
                    }
                }
                totalResultbw.write(sWTitle.deleteCharAt(sWTitle.length() - 1).append(", ").toString());
            }
            totalResultbw.write(retrivedTitles.size() + ", ");
            if(match) {
                totalResultbw.write(1 + ", ");
            } else {
                totalResultbw.write(0 + ", ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Using the Ngram algorithm to match titles and reviews
    public void findTitleByNgram(String review, String filename) {
        try {
            String plainFileName = filename.substring(0, filename.length() - 4);
            // write the filename to the result file
//            nGrambw.write(filename);
//            nGrambw.newLine();
//            nGrambw.write("Matching titles: ");

            HashMap<String, Integer> titleScores = new HashMap<>();
            // split the review by space
            String[] reviewArray = review.split(" ");
            StringBuffer search = new StringBuffer();
            int temp;
            String tempString = "";
            // according to the number of word in the title to assemble the words in the review and calculate their
            // similarity by Ngram, using the smallest score as the score of this title
            for (int i = 0; i < titleList.size(); i++) {
                temp = Integer.MAX_VALUE;
                String title = titleList.get(i);
                int titleLength = title.split(" ").length;
                for (int j = 0; j <= reviewArray.length - titleLength; j++) {
                    search = new StringBuffer();
                    for(int k = 0; k < titleLength; k++) {
                        if(k != titleLength - 1)
                            search.append(reviewArray[j + k] + " ");
                        else
                            search.append(reviewArray[j + k]);
                    }
                    int similarity = nGram.getSimilarity(title, search.toString(),2);
                    if (similarity < temp) {
                        temp = similarity;
                        tempString = search.toString();
                    }
                }
                // set threshold
                if (temp < 4) {
                    titleScores.put(title, temp);
                    pln(title);
                    pln(tempString);
                }
            }

            // sorting the titleScores and output the title with the shortest score
            List<Map.Entry<String, Integer>> list = new ArrayList<>();
            list.addAll(titleScores.entrySet());
            TitleMatching.ValueComparator2 vc = new ValueComparator2();
            Collections.sort(list, vc);

            String tempTitle = "";
            int tempScore = 0;
            ArrayList<String> retrivedTitles = new ArrayList<>();
            for(Map.Entry<String,Integer> mapping:list){
                if (!tempTitle.equals("")) {
                    if (tempScore < mapping.getValue()) {
                        break;
                    } else {
//                        nGrambw.write(", " + mapping.getKey());
                        retrivedTitles.add(mapping.getKey());
                    }
                } else {
//                    nGrambw.write(mapping.getKey());
                    retrivedTitles.add(mapping.getKey());
                    tempScore = mapping.getValue();
                    tempTitle = mapping.getKey();
                }
            }
//            nGrambw.newLine();
//            nGrambw.newLine();
            StringBuffer nGTitle = new StringBuffer();
            String handCheckTitle = handCheckResults.get(plainFileName);
            boolean match = false;
            if (retrivedTitles.size() == 0) {
             totalResultbw.write(", ");
            } else {
                for (int i = 0; i < retrivedTitles.size(); i++) {
                    nGTitle.append(retrivedTitles.get(i) + ";");
                    if (retrivedTitles.get(i).equals(handCheckTitle)) {
                        match = true;
                    }
                }
                totalResultbw.write(nGTitle.deleteCharAt(nGTitle.length() - 1).append(", ").toString());
            }
            totalResultbw.write(retrivedTitles.size() + ", ");
            if(match) {
                totalResultbw.write("1");
            } else {
                totalResultbw.write("0");
            }
            totalResultbw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void pln(){System.out.println();}
    public static void pln(String s){System.out.println(s);}
    public static void pln(int s){System.out.println(s);}
    public static void pln(Object o){System.out.println(o);}

    public static void main(String args[]) {
        try {
        // getting the review files folder and titles file
        TitleMatching tm = new TitleMatching();
        tm.titleFile = args[0];
        tm.reviewFileFolder = args[1];
        tm.handCheckResultsFile = args[2];
        tm.receiveHandCheckTitles();

//        int count = 0;
//        for (Map.Entry<String, String> entry : tm.handCheckResults.entrySet()) {
//            count ++;
//            pln(entry.getKey());
//            pln(entry.getValue());
//        }
//        pln(count);
        tm.processTitles();
        tm.totalResultbw.write("fileName, hasTitle, handCheckTitle, SWTitles, SWReturnNumber, SW match, NGTitiles, NGReturnNumber, NG match");
            tm.totalResultbw.newLine();
        File file = new File(tm.reviewFileFolder);
        if (!file.isDirectory()) {
            System.out.println("There is not folder of reviews");
        } else if (file.isDirectory()) {
            String[] filelist = file.list();
            for (int i = 0; i < filelist.length; i++) {
                // .DS_Store is a system file in mac
                if(filelist[i].equals(".DS_Store"))
                    continue;
                else {
                    String review = tm.getReviewContent(tm.reviewFileFolder + File.separator + filelist[i]);
                    tm.findTitleBySmithWaterman(review, filelist[i]);
                    tm.findTitleByNgram(review, filelist[i]);
                }
            }
        }

//            tm.getSmithWatermanbw().flush();
//            tm.getSmithWatermanbw().close();
//            tm.getSmithWatermanfw().close();
//            tm.getnGrambw().flush();
//            tm.getnGrambw().close();
//            tm.getnGramfw().close();
            tm.totalResultbw.flush();
            tm.totalResultbw.close();
            tm.totalResultfw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
