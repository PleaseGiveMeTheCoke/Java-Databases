package executor;

import bplusTree.Tuple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ResultSet {
    //在parse层进行赋值,为显示的名称,形式为"姓名-年龄-id"
    String attributes;

    public ArrayList<Tuple> getDatas() {
        return datas;
    }

    public void setDatas(ArrayList<Tuple> datas) {
        this.datas = datas;
    }

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    ArrayList<Tuple> datas;
    public ResultSet(){
        datas = new ArrayList<>();
    }
    public void add(Tuple e){
        datas.add(e.getData());
    }
    public void addAll(Collection<Tuple> ts){
        List<Tuple> tmp = new ArrayList<>();
        for (Tuple t : ts) {
            tmp.add(t.getData());
        }
        datas.addAll(tmp);
    }
}
