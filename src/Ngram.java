/**
 * Class: Ngram
 * Author : UCL CHIME  (found from https://blogs.ucl.ac.uk/chime/2010/06/28/java-example-code-of-common-similarity-algorithms-used-in-data-mining/)
 * Cite date: 2016/04/16
 * Description (by UCL CHIME):
 * An algorithm to calculate the probability of the next term based on the previous n terms.
 * It is used in speech recognition, phonemes, language recognition etc.
 * PS: the code has been modified by Lindong Li according to the needs. The implementation is referred to the N-gram algorithm
 * taught in the lectures.
 */
import java.util.ArrayList;
import java.util.List;

public class Ngram
{
    private class result
    {
        private String theWord;
        private int theCount;

        public result(String w, int c)
        {
            theWord = w;
            theCount = c;
        }

        public void setTheCount(int c)
        {
            theCount = c;
        }

        public String getTheWord()
        {
            return theWord;
        }

        public int getTheCount()
        {
            return theCount;
        }
    }

    private List<result> results;

    public Ngram()
    {
        results = new ArrayList<result>();
    }
    public Ngram(String str, int n)
    {

    }

    public int getSimilarity(String wordOne, String wordTwo, int n)
    {
        List<result> res1 = processString(wordOne, n);

        List<result> res2 = processString(wordTwo, n);
//        pln();

        int c = common(res1,res2);
//        pln(c);
//        pln(res1.size() + res2.size());
        //displayResult(res1);
        //pln();
        //displayResult(res2);
        //pln(c);
//        int u = union(res1,res2);
//        pln(u);

//        int sim = (double)c/(double)u;
        int sim = res1.size() + res2.size() - 2 * c;
        return sim;
    }

    private int common(List<result> One, List<result> Two)
    {
        List<result> temp = null;
        if (One.size() > Two.size()) {
            temp = One;
            One = Two;
            Two = temp;
        }
        int res = 0;

        for (int i = 0; i < One.size(); i++)
        {
            for (int j = 0; j < Two.size(); j++)
            {
                if (One.get(i).theWord.equalsIgnoreCase(Two.get(j).theWord)) {
                    res++;
                    break;
                }
            }
        }

        return res;
    }

    private int union(List<result> One, List<result> Two)
    {
        List<result> t = One;

        for (int i = 0; i < Two.size(); i++)
        {
            int pos = -1;
            boolean found = false;
            for (int j = 0; j < t.size() && !found; j++)
            {
                if (Two.get(i).theWord.equalsIgnoreCase(t.get(j).theWord))
                {
                    found = true;
                }
                pos = j;
            }

            if (!found)
            {
                result r = Two.get(i);
                t.add(r);
            }
        }

        return t.size();
    }

    private List<result> processString(String c, int n)
    {
        List<result> t = new ArrayList<result>();

        String spacer = "";
        for (int i = 0; i < n-1; i++)
        {
            spacer = spacer + "%";
        }
        c = spacer + c + spacer;

        for (int i = 0; i < c.length(); i++)
        {
            if (i <= (c.length()-n))
            {
                if (contains(c.substring(i, n+i)) > 0)
                {
                    t.get(i).setTheCount(results.get(i).getTheCount()+1);
                }
                else
                {
                    t.add(new result(c.substring(i,n+i),1));
                }
            }
        }
        return t;
    }

    private int contains(String c)
    {
        for (int i = 0; i < results.size(); i++)
        {
            if (results.get(i).theWord.equalsIgnoreCase(c))
                return i;
        }
        return 0;
    }

    private void displayResult(List<result> d)
    {
        for (int i = 0; i < d.size(); i++)
        {
            System.out.println(d.get(i).theWord+" occurred "+d.get(i).theCount+" times");
        }
    }

    public static void pln(){System.out.println();}
    public static void pln(String s){System.out.println(s);}
    public static void pln(int s){System.out.println(s);}
    public static void pln(Object o){System.out.println(o);}

    public static void main(String args[]) {
        Ngram nGram = new Ngram();
        pln(nGram.getSimilarity("abcd", "\"abcd\"", 2));
    }
}