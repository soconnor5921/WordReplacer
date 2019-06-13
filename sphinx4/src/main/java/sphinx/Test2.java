package sphinx;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import edu.cmu.sphinx.result.WordResult;
import edu.cmu.sphinx.util.TimeFrame;
import sample.Word;

public class Test2
{
    public static ArrayList<String> timeFrames = new ArrayList<>();
    public static ArrayList<TimeFrame> allTimeFrames = new ArrayList<>();

    private static String totalHypothesis = "";

    public static ArrayList<Double> startTimes = new ArrayList<>();
    public static ArrayList<Double> endTimes = new ArrayList<>();
    public static ArrayList<WordResult> allWordResults = new ArrayList<>();

    public static void recognize(String pathname, ArrayList<Word> words)throws Exception
    {
        Configuration configuration = new Configuration();

        configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
        configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
        configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");

        StreamSpeechRecognizer recognizer = new StreamSpeechRecognizer(configuration);
        InputStream stream = new FileInputStream(new File(pathname));

        recognizer.startRecognition(stream);
        SpeechResult result;
        while ((result = recognizer.getResult()) != null) {
            System.out.format("Hypothesis: %s\n", result.getHypothesis());
            

            ArrayList<Integer> indexes = findWords(words, result);
            getTimeFrames(indexes, result);
        }
        System.out.println(totalHypothesis);
    }

    public static ArrayList<Integer> findWords(ArrayList<Word> words, SpeechResult result)
    {
        String hypothesis = result.getHypothesis();
        totalHypothesis += hypothesis += " ";
        String[] allWords = totalHypothesis.split(" ");
        ArrayList<Integer> indexes = new ArrayList<>();
        for(int i = 0; i < words.size(); i++)
        {
            for(int j = 0; j < allWords.length; j++)
            {
                if(words.get(i).getWord().equalsIgnoreCase(allWords[j]))
                {
                    words.get(i).addOneToCount();
                    indexes.add(j);
                }
            }
            //System.out.println(totalHypothesis);
        }
        return indexes;
    }

    public static void getTimeFrames(ArrayList<Integer> indexes, SpeechResult result)
    {
        //ArrayList<TimeFrame> allTimeFrames = new ArrayList<>();
        /*for(WordResult r : result.getWords())
        {
            allTimeFrames.add(r.getTimeFrame());
        }
        for(int i = 0; i < indexes.size(); i++)
        {
            timeFrames.add(allTimeFrames.get(indexes.get(i)).toString());
        }*/
        List<WordResult> wordResultList = result.getWords();
        for(int i = 0; i < wordResultList.size(); i++)
        {
            if(wordResultList.get(i).toString().substring(1,6).equals("<sil>") || wordResultList.get(i).toString().substring(1,9).equals("[SPEECH]"))
            {
                wordResultList.remove(i);
                i--;
            }
            else
            {
                System.out.println(wordResultList.get(i));
                allWordResults.add(wordResultList.get(i));
            }
        }

        for(int i = 0; i < indexes.size(); i++)
        {
            System.out.println(indexes.get(i) + "TEST OF THE PROGRAM     " + allWordResults.get(indexes.get(i)));
        }
    }

    public static void printWordResults()
    {
        for(int i = 0; i < allWordResults.size(); i++)
        {
            System.out.println(allWordResults.get(i));
        }
    }

    public static void getTimeFrames2(ArrayList<Integer> indexes)
    {
        for(int i = 0; i < indexes.size(); i++)
        {
            String frame = allWordResults.get(indexes.get(i)).getTimeFrame().toString();
            String[] arr = frame.split(":");
            startTimes.add(new Double(arr[0]));
            endTimes.add(new Double(arr[1]));
            System.out.println(startTimes.get(i) + " ::: " + endTimes.get(i));
        }
    }
}
