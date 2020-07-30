package 数据库wordnet;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * 计算wordnet里面各个词性的树的最大深度。
 */
public class Depth {
    private static IDictionary dict;
    static {
        //建立指向WordNet词典目录的URL。
        String wnhome = System.getenv("WNHOME");
        String path = wnhome + File.separator + "dict";
        URL url = null;
        try {
            url = new URL("file", null, path);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        //建立词典对象并打开它。
        dict = new Dictionary(url);
        try {
            dict.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        //findMaxDepth();
        trek(dict);
    }
    public static void trek(IDictionary dict) {
        int tickNext = 0;
        int tickSize = 20000;
        int seen = 0;
        System.out.print("Treking across Wordnet");
        long t = System.currentTimeMillis();
        for (POS pos : POS.values()) { //遍历所有词性
            for (Iterator<IIndexWord> i = dict.getIndexWordIterator(pos); i.hasNext(); ) {
                //遍历某一个词性的所有索引
                for (IWordID wid : i.next().getWordIDs()) {
                    //遍历每一个词的所有义项
                    seen += dict.getWord(wid).getSynset().getWords().size();//获取某一个synsets所具有的词
                    if (seen > tickNext) {
                        System.out.print(".");
                        tickNext = seen + tickSize;
                    }
                }
            }
        }
        System.out.printf("done (%1d msec)\n", System.currentTimeMillis() - t);
        System.out.println("In my trek I saw " + seen + " words");
    }
    private static void findMaxDepth() {
        System.out.println("Search across Wordnet");
        long t = System.currentTimeMillis();
        for (POS pos : POS.values()) { //遍历所有词性
            int max = 0;
            for (Iterator<IIndexWord> i = dict.getIndexWordIterator(pos); i.hasNext(); ) {
                //遍历某一个词性的所有索引
                for (IWordID wid : i.next().getWordIDs()) {
                    //遍历每一个词的所有义项
                    //System.out.println(wid.getLemma());
                    if(max < maxDepth(dict.getWord(wid).getSynset())){
                        max = maxDepth(dict.getWord(wid).getSynset());
                    }
                }
            }
            System.out.println(pos.toString() + "------>" + max);
        }
        System.out.printf("done (%1d msec)\n", System.currentTimeMillis() - t);
    }
    public static List<ISynsetID> getAncestors(ISynset synset) {
        List<ISynsetID> list = new ArrayList<ISynsetID>();
        list.addAll(synset.getRelatedSynsets(Pointer.HYPERNYM));
        list.addAll(synset.getRelatedSynsets(Pointer.HYPERNYM_INSTANCE));
        return list;
    }
    public static int maxDepth(ISynset synset) {
        if (synset == null) {
            return 0;
        }

        List<ISynsetID> ancestors = getAncestors(synset);
        if (ancestors.isEmpty()) {
            return 0;
        }

        int i = 0;
        for (ISynsetID ancestor : ancestors) {
            ISynset ancestorSynset = dict.getSynset(ancestor);
            int j = maxDepth(ancestorSynset);
            i = Math.max(i, j);
        }
        return i + 1;
    }

}
