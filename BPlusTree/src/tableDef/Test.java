package tableDef;

import bplusTree.Tuple;
import bplusTree.value.IntValue;
import bplusTree.value.StringValue;
import bplusTree.value.Value;
import util.Seri;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Random;

public class Test implements Comparable<Test>{

    public static void main(String[] args) throws Exception {

        String[] colMsg ={"姓名-String","id-int","性别-String","age-int"};
        Table table = new Table("Person",colMsg,new String[]{"1"});
        ;

        for (int i = 5; i <100 ; i++) {
            StringValue s = new StringValue("机器人"+i+"号");
            IntValue ii = new IntValue(i);
            StringValue sad = null;
            if(Math.random()>0.5) {
                sad = new StringValue("男");
            }else{
                sad = new StringValue("女");
            }
            IntValue age = new IntValue(new Random().nextInt(100));
            Value[] v = new Value[]{s,ii,sad,age};
            Tuple tad = new Tuple(v);
            table.insertInTable(tad);
        }


       Seri.Serialize(table,"PersonDef.txt");


    }

    @Override
    public int compareTo(Test o) {
        return 0;
    }
}
