package 数据库wordnet;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static edu.mit.jwi.item.POS.*;

/**
 * 寻找单词的所有可能的词性
 */
public class Test1 {
    public static POS[] character = {
            NOUN,
            VERB,
            ADVERB,
            ADJECTIVE
    };
    public static void main(String[] args) throws IOException {
        //建立指向WordNet词典目录的URL。
        String wnhome = System.getenv("WNHOME");
        String path = wnhome + File.separator + "dict";
        URL url = new URL("file", null, path);

        //建立词典对象并打开它。
        IDictionary dict = new Dictionary(url);
        dict.open();
        String word1 = "desert";
        int num = 0;
        for (int i = 0; i < character.length; i++) {
            IIndexWord idxWord1 = dict.getIndexWord(word1, character[i]);
            if(idxWord1!=null){
                num++;
                for (IWordID wordID : idxWord1.getWordIDs()) {
                    Word word = (Word) dict.getWord(wordID);
                    System.out.println(word.getSynset().getGloss());
                }
            }
        }
        System.out.println("词性的个数："+num);
    }
}
