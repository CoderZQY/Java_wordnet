package 数据库wordnet;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static jdk.nashorn.internal.objects.Global.Infinity;

public class GetHypernymsTest {
    public static void main(String[] args) throws IOException {
        //建立指向WordNet词典目录的URL。
        String wnhome = System.getenv("WNHOME");
        String path = wnhome + File.separator + "dict";
        URL url = new URL("file", null, path);

        //建立词典对象并打开它。
        IDictionary dict = new Dictionary(url);
        dict.open();
        getHypernyms(dict);//testing
    }
    public static void getHypernyms(IDictionary dict){
        IIndexWord idxWord1 = dict.getIndexWord("evil", POS.NOUN);
        IIndexWord idxWord2 = dict.getIndexWord("night", POS.NOUN);

        int word1MeanNum = idxWord1.getWordIDs().size();
        int word2MeanNum = idxWord2.getWordIDs().size();
        //获取单词1的同义词集合
        for (int i = 0; i < idxWord1.getWordIDs().size(); i++) {
            IWordID wordId = idxWord1.getWordIDs().get(i);
            IWord word = dict.getWord(wordId);
            ISynset synset = word.getSynset();
            for (IWord w : synset.getWords()) {
                //如果单词2出现在单词1的同义词集合里面，那么hop数为0
                if(w.getLemma().equals(idxWord2.getLemma())){
                    System.out.println("hop数为："+0);
                    return;
                }
            }
        }
        //若单词1和单词2不是同义词的关系，则需要找上级词（即父类）
        int[] hop = new int[word1MeanNum*word2MeanNum];//hop数组，大小为：word1的sense个数和word2的乘积
        int num = 0;

        //循环去算word1的每一个意思去和word2的每一个意思进行匹配，算出对应hop数，存入hop数组
        for (int i = 0; i < word1MeanNum ; i++) {
            HashMap<String ,Integer> hm = new HashMap<>();      //存放单词1的所有上级词（key）和对应的hop数（value）
            // 获取hypernyms
            IWordID wordID1 = idxWord1.getWordIDs().get(i); //取出第j个词义的词的ID号
            IWord word1 = dict.getWord(wordID1); //获取词1
            ISynset synset1 = word1.getSynset(); //获取该词所在的Synset1
            List<ISynsetID> hypernyms1 =synset1.getRelatedSynsets(Pointer.HYPERNYM );//通过指针类型来获取相关的词集，其中Pointer类型为HYPERNYM
            List <IWord > words1;
            int hop1 = 0;       //单词1的hop数重置为0
            while(!hypernyms1.isEmpty()){
                ISynset temp = null;
                hop1++;
                for( ISynsetID sid : hypernyms1 ){
                    words1 = dict.getSynset(sid).getWords(); //从synset中获取一个Word的list
                    for(Iterator<IWord > it = words1.iterator(); it.hasNext();){
                        IWord word = it.next();
                        hm.put(word.getLemma(),hop1);
                        temp = word.getSynset();
                    }
                }
                if(null != temp){
                    hypernyms1 = temp.getRelatedSynsets(Pointer.HYPERNYM);
                }
            }
            //当计算完word1的上级词和对应hop数后，开始计算word2的，若匹配则结束，计算得到hop2与hop1相加得到hop
            for (int j = 0; j < word2MeanNum; j++) {
                // 获取hypernyms
                IWordID wordID2 = idxWord2.getWordIDs().get(j); //取出第j个词义的词的ID号
                IWord word2 = dict.getWord(wordID2); //获取词2
                ISynset synset2 = word2.getSynset(); //获取该词所在的Synset2
                List<ISynsetID> hypernyms2 =synset2.getRelatedSynsets(Pointer.HYPERNYM );//通过指针类型来获取相关的词集，其中Pointer类型为HYPERNYM
                List <IWord > words2;
                int hop2 = 0;
                boolean find = false;
                while(!hypernyms2.isEmpty() && !find){
                    ISynset temp = null;
                    hop2++;
                    for(ISynsetID sid : hypernyms2 ){
                        words2 = dict.getSynset(sid).getWords();      //从synset中获取一个Word的list
                        for(Iterator<IWord > it = words2.iterator(); it.hasNext();){
                            IWord word = it.next();
                            if(hm.containsKey(word.getLemma())){
                                //System.out.println("hop1："+hm.get(word.getLemma()));
                                //System.out.println("hop2："+hop2);
                                hop[num++] = hm.get(word.getLemma())+hop2;
                                find = true;       //表示找到了
                                break;
                            }
                            temp = word.getSynset();
                        }
                    }
                    if(null != temp){
                        hypernyms2 = temp.getRelatedSynsets(Pointer.HYPERNYM);
                    }
                }
                //若找不到，则hop数赋予无穷大
                if(!find){
                    hop[num++]= (int) Infinity;
                }

            }
        }
        //对hop数组进行输出
        System.out.print("hop数集合为：");
        for (int i = 0; i < hop.length; i++) {
            if(hop[i]==Integer.MAX_VALUE){
                System.out.print("INF ");
            }
            else{
                System.out.print(hop[i]+" ");
            }
        }
        System.out.println();
        System.out.println("最小的hop数为："+getMin(hop));
        //对HashMap进行输出
        /*for(Map.Entry<String,Integer> entry : hm.entrySet()){
            System.out.println(entry.getKey()+'\t'+entry.getValue());
        }*/
    }
    private static int getMin(int []arr){
        int index = 0;
        for (int i = 1; i < arr.length; i++) {
            if(arr[index] > arr[i]){
                index = i;
            }
        }
        return arr[index];
    }
}