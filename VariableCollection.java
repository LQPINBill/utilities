import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class VariableCollection implements Cloneable{
    final private ConcurrentHashMap<Integer,String>map_index_name;
    final private ConcurrentHashMap<String ,String>map_name_value;

    /**
     * construct a new empty VC with expected variable count(initial capacity) of 16
     */
    public VariableCollection(){
        map_index_name=new ConcurrentHashMap<>();
        map_name_value=new ConcurrentHashMap<>();
    }
    /**
     * construct a new empty VC with designated expected variable count (initial capacity)
     * @param expectedVariableCount expected variable count (initial capacity)
     * @throws IllegalArgumentException if<code>expectedVariableCount</code>is negative
     */
    public VariableCollection(int expectedVariableCount){
        map_index_name=new ConcurrentHashMap<>(expectedVariableCount);
        map_name_value=new ConcurrentHashMap<>(expectedVariableCount);
    }

    public void clear(){
        map_index_name.clear();
        map_name_value.clear();
    }
    public boolean isEmpty(){
        return map_name_value.isEmpty();
    }

    /**
     * @param name variable's name
     * @return true iff this VC contains a variable with name given by parameter
     * @throws NullPointerException if<code>name==null</code>
     */
    public boolean containsVariable(String name ){
        return map_name_value.containsKey(name);
    }
    /**
     * @param value variable's value
     * @return true iff this VC contains a variable with value given by parameter
     * @throws NullPointerException if<code>value==null</code>
     */
    public boolean containsValue   (String value){
        return map_name_value.containsValue(value);
    }

    /**
     * 
     * @param name  variable's name
     * @param value variable's new value
     * @return      variable's value before this assignment
     * @throws NullPointerException if <code>name</code>or<code>value</code>is<code>null</code>
     * @throws RuntimeException if this VC does not contain a variable with name given by parameter
     */
    public String set(String name,String value){
        String v=map_name_value.put(name,value);
        if(v==null)throw new RuntimeException();
        return v;
    }
    /**
     * @param name variable's name
     * @return     variable's value
     * @throws NullPointerException if<code>name==null</code>
     * @throws RuntimeException if this VC does not contain a variable with name given by parameter
     */
    public String get(String name){
        String v=map_name_value.get(name);
        if(v==null)throw new RuntimeException();
        return v;
    }
    public int variableCount(){
        return map_name_value.size();
    }

    public ArrayList<String>getNameValues(){
        ArrayList<String>l=new ArrayList<>(map_index_name.size()*2);
        map_index_name.forEach(
            (i,n)->{l.add(n);l.add(map_name_value.get(n));}
        );
        return l;
    }
    public ArrayList<String>getNames     (){
        ArrayList<String>l=new ArrayList<>(map_index_name.size());
        map_index_name.forEach(
            (i,n)->{l.add(n);}
        );
        return l;
    }
    public ArrayList<String>getValues    (){
        ArrayList<String>l=new ArrayList<>(map_index_name.size());
        map_index_name.forEach(
            (i,n)->{l.add(map_name_value.get(n));}
        );
        return l;
    }

    public VariableCollection(ArrayList<String>NameValues                   ){
        int k=NameValues.size();
        int i=k/2;
        //if(i*2!=k)throw new IllegalArgumentException();
        map_index_name=new ConcurrentHashMap<>(i);
        map_name_value=new ConcurrentHashMap<>(i);
        String name,value;
        while(k>0){
            value=NameValues.get(--k);
            if(map_name_value.put(name=NameValues.get(--k),value)!=null)throw new RuntimeException();
            map_index_name.put(--i,name);
        }
    }
    public VariableCollection(ArrayList<String>names,ArrayList<String>values){
        int k=names .size();
        if(k!=values.size())throw new IllegalArgumentException();
        map_index_name=new ConcurrentHashMap<>(k);
        map_name_value=new ConcurrentHashMap<>(k);
        String name;
        while(k>0){
            name=names.get(--k);
            if(map_name_value.put(name,values.get(k))!=null)throw new RuntimeException();
            map_index_name.put(k,name);
        }
    }
    public void setto        (ArrayList<String>NameValues                   ){
        map_index_name.clear();
        map_name_value.clear();
        int k=NameValues.size();
        int i=k/2;
        //if(i*2!=k)throw new IllegalArgumentException();
        String name,value;
        while(k>0){
            value=NameValues.get(--k);
            if(map_name_value.put(name=NameValues.get(--k),value)!=null)throw new RuntimeException();
            map_index_name.put(--i,name);
        }
    }
    public void setto        (ArrayList<String>names,ArrayList<String>values){
        int k=names .size();
        if(k!=values.size())throw new IllegalArgumentException();
        map_index_name.clear();
        map_name_value.clear();
        String name;
        while(k>0){
            name=names.get(--k);
            if(map_name_value.put(name,values.get(k))!=null)throw new RuntimeException();
            map_index_name.put(k,name);
        }
    }

    public static String storageTOmemory (String storage){
        char[]value=new char[storage.length()];
        int i=0;
        char c;
        for(int k=0;k<value.length;++i,++k)value[i]='\\'==(c=storage.charAt(k))?storage.charAt(++k):c;
        return new String(value,0,i);
    }
    public static String  memoryTOstorage(String value  ){
        int l=value.length();
        StringBuilder storage=new StringBuilder((int)(l*1.02));
        char c;
        for(int k=0;k<l;++k){
            if('"'==(c=value.charAt(k))
            ||'\\'== c)storage.append('\\');
            storage.append(c);
        }
        return storage.toString();
    }

    // change bytes-String conversion to Huffman tree ????
    // ????
    @Override
    public String toString(){
        return toString(1);
    }

    public String toString(int nextLines){
        if(nextLines<0)throw new IllegalArgumentException();
        int k=map_name_value.size()*2;
        ArrayList<String>NameValues=new ArrayList<>(k);
        map_name_value.forEach(
            (n,v)->{NameValues.add(n);NameValues.add(v);}
        );
        int l_names=0,l_values=0;
        while(k>0){
            l_values+=NameValues.get(--k).length();
            l_names +=NameValues.get(--k).length();
        }
        StringBuilder str=new StringBuilder(l_names+(4+nextLines)*map_name_value.size()+(int)(l_values*1.02));
        map_index_name.forEach(
            (i,n)->{
                str.append(n);
                str.append("=\"");
                String value=map_name_value.get(n);
                char c;
                int j=0;
                for(int l=value.length();j<l;++j){
                    if('"'==(c=value.charAt(j))
                    ||'\\'== c)str.append('\\');
                    str.append(c);
                }
                str.append("\";");
                for(j=0;j<nextLines;++j)str.append('\n');
            }
        );
        return str.toString();
    }
    public byte[] toBytes (){
        return toString(0).getBytes(StandardCharsets.UTF_8);
    }

    // ????
    public VariableCollection(String content){
        map_index_name=new ConcurrentHashMap<>();
        map_name_value=new ConcurrentHashMap<>();
        setto(content);
    }
    public VariableCollection(byte[] bytes  ){
        map_index_name=new ConcurrentHashMap<>();
        map_name_value=new ConcurrentHashMap<>();
        setto(new String(bytes,StandardCharsets.UTF_8));
    }
    public void setto        (String content){
        int content_l=content.length();
        int i=content_l;
        boolean all_empty=true;
        char c;
        while(all_empty&&i>0)all_empty=' '==(c=content.charAt(--i))
                                    ||'\n'== c;
        map_index_name.clear();
        map_name_value.clear();
        if(all_empty)return;
        if(++i!=content_l)content=content.substring(0,content_l=i);
        int index=i=0;
        int begin;
        String name;
        StringBuilder value;
        do{ while(' ' ==(c=content.charAt(i))
                ||'\n'== c)++i;
            begin=i++;
            while(' ' !=(c=content.charAt(i))
                &&'=' != c
                &&'\n'!= c)++i;
            map_index_name.put(index++,name=content.substring(begin,i));
            //while('='!=content.charAt(i))++i;
            do++i;while('"'!=content.charAt(i));
            value=new StringBuilder();
            while('"'!=(c=content.charAt(++i)))value.append(c=='\\'?content.charAt(++i):c);
            if(map_name_value.put(name,value.toString())!=null)throw new RuntimeException();
            //value.trimToSize();
            do++i;while(';'!=content.charAt(i));
        }while(++i<content_l);
    }
    public void setto        (byte[] bytes  ){
        setto(new String(bytes,StandardCharsets.UTF_8));
    }

    // ????
    public VariableCollection(VariableCollection vc){
        int l=vc.map_name_value.size();
        map_index_name=new ConcurrentHashMap<>(l);
        map_name_value=new ConcurrentHashMap<>(l);

        /*vc.map_index_name.forEach(
            (i,n)->{map_index_name.put(i,n);}
        );
        vc.map_name_value.forEach(
            (n,v)->{map_name_value.put(n,v);}
        );*/

        vc.map_index_name.forEach(
            (i,n)->{map_index_name.put(i,n);map_name_value.put(n,vc.map_name_value.get(n));}
        );

    }
    public void setto        (VariableCollection vc){
        map_index_name.clear();
        map_name_value.clear();

        /*vc.map_index_name.forEach(
            (i,n)->{map_index_name.put(i,n);}
        );
        vc.map_name_value.forEach(
            (n,v)->{map_name_value.put(n,v);}
        );*/

        vc.map_index_name.forEach(
            (i,n)->{map_index_name.put(i,n);map_name_value.put(n,vc.map_name_value.get(n));}
        );

    }
}