package 数据库wordnet;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 *  判断两个单词是否是同义词关系。
 */
public class Test2 {
    public static void main(String[] args) throws IOException {
        //建立指向WordNet词典目录的URL。
        String wnhome = System.getenv("WNHOME");
        String path = wnhome + File.separator + "dict";
        URL url = new URL("file", null, path);

        //建立词典对象并打开它。
        IDictionary dict = new Dictionary(url);
        dict.open();

        IIndexWord idxWord1 = dict.getIndexWord("abandon", POS.VERB);
        IIndexWord idxWord2 = dict.getIndexWord("desert", POS.VERB);

        //获取单词1的同义词集合
        for (int i = 0; i < idxWord1.getWordIDs().size(); i++) {
            IWordID wordId = idxWord1.getWordIDs().get(i);
            IWord word = dict.getWord(wordId);
            ISynset synset = word.getSynset();
            for (IWord w : synset.getWords()) {
                //如果单词2出现在单词1的同义词集合里面，那么hop数为0
                if (w.getLemma().equals(idxWord2.getLemma())) {
                    System.out.println("hop数为：" + 0);
                    break;
                }
            }
        }
    }
}
