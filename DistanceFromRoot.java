package 数据库wordnet;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 计算根节点到单词所在的位置的最小距离
 */
public class DistanceFromRoot {
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
        String word = "me";
        for(POS pos : POS.values()){
            int hop = Integer.MAX_VALUE;
            IIndexWord wd  = dict.getIndexWord(word,pos);
            if(null != wd){
                for(IWordID wid : wd.getWordIDs()){
                    if(maxDepth(dict.getWord(wid).getSynset()) < hop)
                        hop = maxDepth(dict.getWord(wid).getSynset());
                }
            }
            if(hop < Integer.MAX_VALUE){
                System.out.println(word+" "+pos.toString()+"---->"+hop);
            }else {
                System.out.println(word+" "+pos.toString()+"----> Inf");//表示对应的词性不存在
            }
        }
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
    public static List<ISynsetID> getAncestors(ISynset synset) {
        List<ISynsetID> list = new ArrayList<ISynsetID>();
        list.addAll(synset.getRelatedSynsets(Pointer.HYPERNYM));
        list.addAll(synset.getRelatedSynsets(Pointer.HYPERNYM_INSTANCE));
        return list;
    }
}
