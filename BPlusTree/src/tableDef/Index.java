package tableDef;

import java.io.Serializable;

public class Index implements Comparable<Index>, Serializable {
    String indexS;
    Integer indexI;
    public Index(String index){
        indexS = index;
    }
    public Index(Integer index){
        indexI = index;
    }


    @Override
    public int compareTo(Index index) {
        if(indexI!=null){
            return this.indexI.compareTo(index.indexI);
        }else{
            return this.indexS.compareTo(index.indexS);
        }
    }
}
