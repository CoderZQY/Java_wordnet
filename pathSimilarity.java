package 数据库wordnet;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static jdk.nashorn.internal.objects.Global.Infinity;

public class pathSimilarity {
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
    public static POS minHopPos = null;
    public static Double lchSimilarity(String word1,String word2) {
        int distance = getMinHop(word1, word2);
        Double lchSimilarity = null;
        if (distance < 0) {
            throw new IllegalArgumentException("Distance value is negative!");
        }
        if (distance == 0) {
            distance = 1;
        }
        if(minHopPos==POS.NOUN || minHopPos==POS.VERB){
            int MAX_DEPTH_OF_HIERARCHY = 18;
            switch (minHopPos){
                case NOUN:MAX_DEPTH_OF_HIERARCHY = 18;break;
                case VERB:MAX_DEPTH_OF_HIERARCHY = 12;break;
            }
            lchSimilarity = Math.log((2 * MAX_DEPTH_OF_HIERARCHY)
                    / ((double) distance));
            return lchSimilarity;
        }else {
            return 1 / ((double) distance + 1);
        }
    }
    public static double similarity(String word1,String word2) {
        int distance = getMinHop(word1,word2);
        return  1 / ((double) distance + 1);
    }
    private static double getSentenceSimilary(String[] words1, String[] words2){
        double[][] matrix = new double[words1.length][words2.length];
        boolean[][] matrix_bool = new boolean[words1.length][words2.length];
        for (int i = 0; i < words1.length; i++) {
            for (int j = 0; j < words2.length; j++) {
                matrix[i][j] = lchSimilarity(words1[i],words2[j]);
            }
        }
        for (int i = 0; i < words1.length; i++) {   //words1长度作为行，words2长度作为列
            for (int j = 0; j < words2.length; j++) {
                System.out.print(String.format("%.2f",matrix[i][j])+" ");
            }
            System.out.println();
        }
        matrix = NormalizeMatrix.normalize4Scale(matrix);   //对原矩阵进行归一化
        System.out.println("==============归一化以后的结果===========");
        for (int i = 0; i < words1.length; i++) {   //words1长度作为行，words2长度作为列
            for (int j = 0; j < words2.length; j++) {
                System.out.print(String.format("%.2f",matrix[i][j])+" ");
            }
            System.out.println();
        }
        double[] maxElements = new double[Math.min(words1.length,words2.length)];
        for (int i = 0; i < maxElements.length; i++) {
            double max = 0;
            int max_row = 0,max_col=0;
            for (int j = 0; j < words1.length; j++) {
                for (int k = 0; k < words2.length; k++) {
                    if(!matrix_bool[j][k] && max<matrix[j][k] && matrix[j][k] != (double)1){
                        max = matrix[j][k];
                        max_row = j;
                        max_col = k;
                    }
                }
            }
            maxElements[i] = max;
            for (int j = 0; j < words2.length; j++) {
                matrix_bool[max_row][j] = true;
            }
            for (int j = 0; j < words1.length; j++) {
                matrix_bool[j][max_col] = true;
            }
        }
        double result = 0;
        System.out.print("相似度矩阵的每组最大值：");
        for (int i = 0; i < maxElements.length; i++) {
            result += maxElements[i];
            System.out.print(String.format("%.2f",maxElements[i])+" ");
        }
        result = result*(words1.length+words2.length)/(2*words1.length*words2.length);
        return result;
    }
    private static double getSentenceSimilary2(String[] words1, String[] words2){
        double[][] matrix = new double[words1.length][words2.length];
        boolean[][] matrix_bool = new boolean[words1.length][words2.length];
        for (int i = 0; i < words1.length; i++) {
            for (int j = 0; j < words2.length; j++) {
                matrix[i][j] = similarity(words1[i],words2[j]);
            }
        }
        for (int i = 0; i < words1.length; i++) {   //words1长度作为行，words2长度作为列
            for (int j = 0; j < words2.length; j++) {
                System.out.print(String.format("%.2f",matrix[i][j])+" ");
            }
            System.out.println();
        }
        double[] maxElements = new double[Math.min(words1.length,words2.length)];
        for (int i = 0; i < maxElements.length; i++) {
            double max = 0;
            int max_row = 0,max_col=0;
            for (int j = 0; j < words1.length; j++) {
                for (int k = 0; k < words2.length; k++) {
                    if(!matrix_bool[j][k] && max<matrix[j][k] && matrix[j][k] != (double)1){
                        max = matrix[j][k];
                        max_row = j;
                        max_col = k;
                    }
                }
            }
            maxElements[i] = max;
            for (int j = 0; j < words2.length; j++) {
                matrix_bool[max_row][j] = true;
            }
            for (int j = 0; j < words1.length; j++) {
                matrix_bool[j][max_col] = true;
            }
        }
        double result = 0;
        System.out.print("相似度矩阵的每组最大值：");
        for (int i = 0; i < maxElements.length; i++) {
            result += maxElements[i];
            System.out.print(String.format("%.2f",maxElements[i])+" ");
        }
        result = result*(words1.length+words2.length)/(2*words1.length*words2.length);
        return result;
    }
//jewel："jewel","precious","stone","use","decorate","valuable","thing","wear","ring","necklace"
//monk："monk","member","male","religious","community","separate","outside","world"
//slave："slave","someone","property","another","person","work"
//asylum；"asylum","psychiatric","hospital"
//cemetery："cemetery","place","dead","people","body","ash","bury"
//coast："coast","an","area","land","sea"
//forest："forest","large","area","tree","grow","close","together"
//grim："grin","broad","smile"
//lad："lad","young","man","boy"
//shore："shore","sea","lake","wide","river","land","edge","it"
//woodland："woodland","land","tree"
//mound："mound","something","large","pile","it"
//wizard："legend","fairy","story","wizard","man","magic","power"
//graeyard："graveyard","area","land","sometimes","near","church","dead","people","bury"
//food："food","what","people","animal","eat"
//rooster："rooster","adult","male","chicken"
//voyage："voyage","a","long","journey","ship","spacecraft"
//bird："bird","a","creature","feather","wing","female","lay","egg","fly"
//hill："hill","an","area","land","high","surround"
//furnace："furnace","container","enclose","space","hot","fire","make","example","melt","metal","burn","rubbish","produce","steam"
//implement："implement","a","tool","piece","equipment"
//crane："crane","a","large","machine","move","heavy","thing","lift","them","air"
//car："a","car","motor","vehicle","room","small","number","passenger"
//journey："when","you","make","a","journey","travel","one","place","another"
//glass："glass","a","hard","transparent","substance","use","make","thing","window","bottle"
//magician："magician","a","person","entertain","people","do","magic","trick"
//oracle："in","ancient","time","an","oracle","priest","priestess","make","statement","future","event","truth"
//brother："your","brother","a","boy","man","same","parent","you"
//sage："sage","a","person","regard","wise"
    public static void main(String[] args) {
        String[] words1 = new String[]{"legend","fairy","story","wizard","man","magic","power"};
        String[] words2 = new String[]{"sage","a","person","regard","wise"};
        double similaryResult = getSentenceSimilary(words1,words2);
        System.out.println();
        System.out.println("词组的相似度结果为："+similaryResult);
    }

    public static int getMinHop(String word1,String word2){
        int minHop = Integer.MAX_VALUE;
        for(POS pos : POS.values()){
            IIndexWord idxWord1 = dict.getIndexWord(word1,pos);
            IIndexWord idxWord2 = dict.getIndexWord(word2,pos);
            if(null != idxWord1 && null != idxWord2) {
                int temp = getHop(word1, pos, word2, pos);
                if (minHop > temp) {
                    minHop = temp;
                    minHopPos = pos;
                }
            }
        }
        return minHop;
    }
    public static int getHop(String word1, POS pos1, String word2, POS pos2) {

        IIndexWord idxWord1 = dict.getIndexWord(word1, pos1);
        IIndexWord idxWord2 = dict.getIndexWord(word2, pos2);

        int word1MeanNum = idxWord1.getWordIDs().size();
        int word2MeanNum = idxWord2.getWordIDs().size();
        //获取单词1的同义词集合
        for (int i = 0; i < idxWord1.getWordIDs().size(); i++) {
            IWordID wordId = idxWord1.getWordIDs().get(i);
            IWord word = dict.getWord(wordId);
            ISynset synset = word.getSynset();
            for (IWord w : synset.getWords()) {
                //如果单词2出现在单词1的同义词集合里面，那么hop数为0
                if (w.getLemma().equals(idxWord2.getLemma())) {
                    //System.out.println("hop数为："+0);
                    return 0;
                }
            }
        }
        //若单词1和单词2不是同义词的关系，则需要找上级词（即父类）
        int[] hop = new int[word1MeanNum * word2MeanNum];//hop数组，大小为：word1的sense个数和word2的乘积
        int num = 0;

        //循环去算word1的每一个意思去和word2的每一个意思进行匹配，算出对应hop数，存入hop数组
        for (int i = 0; i < word1MeanNum; i++) {
            HashMap<String, Integer> hm = new HashMap<>();      //存放单词1的所有上级词（key）和对应的hop数（value）
            // 获取hypernyms
            IWordID wordID1 = idxWord1.getWordIDs().get(i); //取出第j个词义的词的ID号
            IWord wd1 = dict.getWord(wordID1); //获取词1
            ISynset synset1 = wd1.getSynset(); //获取该词所在的Synset1
            List<ISynsetID> hypernyms1 = synset1.getRelatedSynsets(Pointer.HYPERNYM);//通过指针类型来获取相关的词集，其中Pointer类型为HYPERNYM
            List<IWord> words1;
            int hop1 = 0;       //单词1的hop数重置为0
            while (!hypernyms1.isEmpty()) {
                ISynset temp = null;
                hop1++;
                for (ISynsetID sid : hypernyms1) {
                    words1 = dict.getSynset(sid).getWords(); //从synset中获取一个Word的list
                    for (Iterator<IWord> it = words1.iterator(); it.hasNext(); ) {
                        IWord word = it.next();
                        hm.put(word.getLemma(), hop1);
                        temp = word.getSynset();
                    }
                }
                if (null != temp) {
                    hypernyms1 = temp.getRelatedSynsets(Pointer.HYPERNYM);
                }
            }
            if (hm.containsKey(idxWord2.getLemma())) {
                return hm.get(idxWord2.getLemma());
            }
            //当计算完word1的上级词和对应hop数后，开始计算word2的，若匹配则结束，计算得到hop2与hop1相加得到hop
            for (int j = 0; j < word2MeanNum; j++) {
                // 获取hypernyms
                IWordID wordID2 = idxWord2.getWordIDs().get(j); //取出第j个词义的词的ID号
                IWord wd2 = dict.getWord(wordID2); //获取词2
                ISynset synset2 = wd2.getSynset(); //获取该词所在的Synset2
                List<ISynsetID> hypernyms2 = synset2.getRelatedSynsets(Pointer.HYPERNYM);//通过指针类型来获取相关的词集，其中Pointer类型为HYPERNYM
                List<IWord> words2;
                int hop2 = 0;
                boolean find = false;
                while (!hypernyms2.isEmpty() && !find) {
                    ISynset temp = null;
                    hop2++;
                    for (ISynsetID sid : hypernyms2) {
                        words2 = dict.getSynset(sid).getWords();      //从synset中获取一个Word的list
                        for (Iterator<IWord> it = words2.iterator(); it.hasNext(); ) {
                            IWord word = it.next();
                            if (hm.containsKey(word.getLemma())) {
                                //System.out.println("hop1："+hm.get(word.getLemma()));
                                //System.out.println("hop2："+hop2);
                                hop[num++] = hm.get(word.getLemma()) + hop2;
                                find = true;       //表示找到了
                                break;
                            }
                            if (idxWord1.getLemma().equals(word.getLemma())) {
                                return hop2;
                            }
                            temp = word.getSynset();
                        }
                    }
                    if (null != temp) {
                        hypernyms2 = temp.getRelatedSynsets(Pointer.HYPERNYM);
                    }
                }
                //若找不到，则hop数赋予无穷大
                if (!find) {
                    hop[num++] = (int) Infinity;
                }

            }
        }
        return getMin(hop);
    }
    private static int getMin(int[] arr) {
        int index = 0;
        for (int i = 1; i < arr.length; i++) {
            if(arr[index] > arr[i]){
                index = i;
            }
        }
        return arr[index];
    }
}
