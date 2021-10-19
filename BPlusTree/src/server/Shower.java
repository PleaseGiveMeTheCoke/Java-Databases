package server;

import bplusTree.Tuple;
import bplusTree.value.Value;
import executor.ResultSet;

import java.util.ArrayList;

public class Shower {
    ResultSet resultSet;
    public Shower(ResultSet resultSet){
        this.resultSet = resultSet;
    }
    public String show(){
        ArrayList<Tuple> datas = resultSet.getDatas();
        String attributes = resultSet.getAttributes();
        String[] split = attributes.split("-");
        String res = "=====================\n";
        for (Tuple data : datas) {
            Value[] values = data.getValues();
            for (int i = 0; i < values.length; i++) {
                res+=split[i]+":"+" "+values[i]+"\n";

            }
            res+="=====================\n";
        }

        return res;
    }
}
