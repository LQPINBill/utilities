import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class VariableCollection implements Cloneable{
    /**
     * designates sequence of variables 
     */
    final private ConcurrentHashMap<Integer,String>map_index_name;
    /**
     * designates value for each name
     */
    final private ConcurrentHashMap<String ,String>map_name_value;

    /**
     * construct and return a new empty VC with initial expected variable count 
     * (capacity) of 16
     */
    public VariableCollection(){
        map_index_name=new ConcurrentHashMap<>();
        map_name_value=new ConcurrentHashMap<>();
    }
    /**
     * construct and return a new empty VC with initial expected variable count 
     * (capacity) of<code>expectedVariableCount</code>
     * @param expectedVariableCount the initiail expected variable cound 
     * (capacity)
     * @throws IllegalArgumentException iff<code>expectedVariableCount</code>is 
     * negative
     */
    public VariableCollection(int expectedVariableCount){
        map_index_name=new ConcurrentHashMap<>(expectedVariableCount);
        map_name_value=new ConcurrentHashMap<>(expectedVariableCount);
    }

    /**
     * declare a new variable with default value "" the String with length 0
     * @param name variable's name
     * @throws NullPointerException iff<code>name</code>is<code>null</code>
     * @throws RuntimeException iff<code>this</code>already contains a variable 
     * with name<code>name</code>
     */
    public void add   (String name){
        map_index_name.put(Cache.Integer_valueOf(map_index_name.size()),name);
        if(map_name_value.put(name,"")!=null)throw new RuntimeException();
    }
    /**
     * remove a variable
     * @param name variable's name
     * @throws NullPointerException iff<code>name</code>is<code>null</code>
     * @throws RuntimeException iff<code>this</code>does not contain a variable 
     * with name<code>name</code>
     */
    public void remove(String name){
        if(map_name_value.remove(name)==null)throw new RuntimeException();
        Integer i=Cache.Integer_valueOf(map_name_value.size());
        while(!map_index_name.get(i).equals(name))i=Cache.Integer_valueOf(i-1);
        for(Integer k=Cache.Integer_valueOf(i+1);
            k<map_index_name.size();
            k=Cache.Integer_valueOf((i=k)+1)
        )map_index_name.put(i,map_index_name.get(k));
        map_index_name.remove(i);
    }
    /**
     * @param name variable's name
     * @return the index of variable in this VC
     */
    public int indexOfVariable(String name){
        int i=map_index_name.size();
        while(i>0)
            if(map_index_name.get(Cache.Integer_valueOf(--i)).equals(name))
                return i;
        return-1;
    }
    /**
     * @param name variable's name to find
     * @return<code>true</code>iff<code>this</code>contains a variable with name
     * <code>name</code>
     */
    public boolean containsVariable(String name ){
        return map_name_value.containsKey(name);
    }
    /**
     * @param value variable's value to find
     * @return<code>true</code>iff<code>this</code>contains a variable with 
     * value<code>value<code>
     */
    public boolean containsValue   (String value){
        return map_name_value.containsValue(value);
    }
    /**
     * @return the quantity of variables in<code>this</code>
     */
    public int     size   (){
        return map_name_value.size();
    }
    /**
     * remove all variables
     */
    public void    clear  (){
        map_index_name.clear();
        map_name_value.clear();
    }
    /**
     * @return<code>size()==0</code>
     */
    public boolean isEmpty(){
        return map_name_value.isEmpty();
    }

    /**
     * set value of a variable
     * @param name  variable's name
     * @param value value to be assigned to the variable
     * @throws NullPointerException iff<code>name</code>or<code>value</code>is
     * <code>null</code>
     * @throws RuntimeException iff<code>this</code>does not contain a variable 
     * with name<code>name</code>
     */
    public void   set(String name,String value){
        if(map_name_value.replace(name,value)==null)throw new RuntimeException();
    }
    /**
     * get value of a variable
     * @param name variable's name
     * @return the variable's value
     */
    public String get(String name             ){
        String v=map_name_value.get(name);
        if(v==null)throw new RuntimeException();
        return v;
    }

    /**
     * a VC object x is equal to a VC object y iff all of the followings are 
     * true:
     * 
     * y is not<code>null</code>
     * 
     * y contains variables as much as x and no more than x
     * 
     * for every index<code>i</code>s.t.<code>i</code>is smaller than<code>
     * x.size()</code>, the<code>i</code>th variable of x and y have the same 
     * name and value
     */
    public boolean equals(VariableCollection y){
        if(y==null)return false;
        Integer I       =Cache.Integer_valueOf(map_name_value.size());
        if(I.intValue()!=                    y.map_name_value.size())return false;
        String name  ;
        String name_y;
        while(I.intValue()>0){
            I=Cache.Integer_valueOf(I.intValue()-1);
            if( !(  (name=map_index_name.get(I))  .equals(name_y=y.map_index_name.get(I))
                    &&    map_name_value.get(name).equals(       y.map_name_value.get(name_y))
                )
            )return false;
        }
        return true;
    }
    @Override
    public boolean equals(Object y){
        return y instanceof VariableCollection vc?equals(vc):false;
    }
    /**
     * a VC object x is structurally equal to a VC object y iff all of the 
     * followings are true:
     * 
     * y is not<code>null</code>
     * 
     * y contains variables as much as x and no more than x
     * 
     * for every index<code>i</code>s.t.<code>i</code>is smaller than<code>
     * x.size()</code>, the<code>i</code>th variable of x and y have the same 
     * name
     */
    public boolean structurallyEqual(VariableCollection y){
        if(y==null)return false;
        Integer I       =Cache.Integer_valueOf(map_name_value.size());
        if(I.intValue()!=                    y.map_name_value.size())return false;
        while(I.intValue()>0){
            I=Cache.Integer_valueOf(I.intValue()-1);
            if(!map_index_name.get(I).equals(y.map_index_name.get(I)))return false;
        }
        return true;
    }
    @Override
    public int hashCode(){
        int[]hash=new int[1];
        map_name_value.forEach(
            (n,v)->{hash[0]+=n.hashCode()+v.hashCode();}
        );
        return hash[0];
    }

    public VariableCollection(VariableCollection vc){
        int l=vc.map_name_value.size();
        map_index_name=new ConcurrentHashMap<>(l);
        map_name_value=new ConcurrentHashMap<>(l);

        vc.map_index_name.forEach(
            (i,n)->{map_index_name.put(i,n);}
        );
        vc.map_name_value.forEach(
            (n,v)->{map_name_value.put(n,v);}
        );

        /*vc.map_index_name.forEach(
            (i,n)->{
                map_index_name.put(i,n);
                map_name_value.put(n,vc.map_name_value.get(n));
            }
        );*/

    }
    public void setto        (VariableCollection vc){
        if(vc!=this){
            map_index_name.clear();
            map_name_value.clear();

            vc.map_index_name.forEach(
                (i,n)->{map_index_name.put(i,n);}
            );
            vc.map_name_value.forEach(
                (n,v)->{map_name_value.put(n,v);}
            );

            /*vc.map_index_name.forEach(
                (i,n)->{
                    map_index_name.put(i,n);
                    map_name_value.put(n,vc.map_name_value.get(n));
                }
            );*/

        }
    }
    @Override
    public VariableCollection clone(){
        return new VariableCollection(this);
    }

    /**
     * an export function
     * @return a new String ArrayList s.t. it contains this VC's all variable's 
     * name and value, where for every non negative integer<code>i</code>s.t.
     * <code>i</code>is smaller than<code>size()</code>, index<code>2*i</code>
     * stores the<code>i</code>th variable's name, and index<code>2*i+1</code>
     * stores the value of that variable
     */
    public ArrayList<String>NamesValues(){
        int variables=map_index_name.size();
        ArrayList<String>list=new ArrayList<>(variables*2);
        String name;
        for(int i=0;i<variables;++i){
            list.add(name=map_index_name.get(Cache.Integer_valueOf(i)));
            list.add(map_name_value.get(name));
        }
        return list;
    }
    /**
     * an export function
     * @return a new String ArrayList s.t. it contains this VC's all variable's 
     * name, where for every non negative integer<code>i</code>s.t.<code>i
     * </code>is smaller than<code>size()</code>, index<code>i</code>stores the
     * <code>i</code>th variable's name
     */
    public ArrayList<String>names      (){
        int variables=map_index_name.size();
        ArrayList<String>list=new ArrayList<>(variables);
        for(int i=0;i<variables;++i)list.add(map_index_name.get(Cache.Integer_valueOf(i)));
        return list;
    }
    /**
     * an export function
     * @return a new String ArrayList s.t. it contains this VC's all variable's 
     * value, where for every non negative integer<code>i</code>s.t.<code>i
     * </code>is smaller than<code>size()</code>, index<code>i</code>stores the
     * <code>i</code>th variable's value
     */
    public ArrayList<String>     values(){
        int variables=map_index_name.size();
        ArrayList<String>list=new ArrayList<>(variables);
        for(int i=0;i<variables;++i)list.add(map_name_value.get(map_index_name.get(Cache.Integer_valueOf(i))));
        return list;
    }

    public VariableCollection(ArrayList<String>NamesValues                  ){
        int k=NamesValues.size();
        int i=k/2;
        if(i*2!=k)throw new RuntimeException();
        map_index_name=new ConcurrentHashMap<>(i);
        map_name_value=new ConcurrentHashMap<>(i);
        String name,value;
        while(k>0){
            value=NamesValues.get(--k);
            map_index_name.put(Cache.Integer_valueOf(--i),name=NamesValues.get(--k));
            if(map_name_value.put(name,value)!=null)throw new RuntimeException();
        }
    }
    public VariableCollection(ArrayList<String>names,ArrayList<String>values){
        int k=names.size();
        map_index_name=new ConcurrentHashMap<>(k);
        map_name_value=new ConcurrentHashMap<>(k);
        String name;
        while(k>0){
            name=names.get(--k);
            map_index_name.put(Cache.Integer_valueOf(k),name);
            if(map_name_value.put(name,values.get(k))!=null)throw new RuntimeException();
        }
    }
    public void setto        (ArrayList<String>NamesValues                  ){
        int k=NamesValues.size();
        int i=k/2;
        map_index_name.clear();
        map_name_value.clear();
        String name,value;
        while(k>0){
            value=NamesValues.get(--k);
            map_index_name.put(Cache.Integer_valueOf(--i),name=NamesValues.get(--k));
            if(map_name_value.put(name,value)!=null)throw new RuntimeException();
        }
    }
    public void setto        (ArrayList<String>names,ArrayList<String>values){
        int k=names.size();
        map_index_name.clear();
        map_name_value.clear();
        String name;
        while(k>0){
            name=names.get(--k);
            map_index_name.put(Cache.Integer_valueOf(k),name);
            if(map_name_value.put(name,values.get(k))!=null)throw new RuntimeException();
        }
    }

    public static String storageTOmemory (String storage){
        char[]value=new char[storage.length()];
        int i_storage=0,
            i_value  =0;
        char c;
        while(i_storage<value.length){
            value[i_value++]=switch(c=storage.charAt(i_storage++)){
                case'\n'->throw new RuntimeException();
                case'\\'->switch(storage.charAt(i_storage++)){
                    case'\\'->'\\';
                    case 'n'->'\n';
                    case '"'-> '"';
                    default ->throw new RuntimeException();
                };
                default ->c;
            };
        }
        return new String(value,0,i_value);
    }
    public static String  memoryTOstorage(String value  ){
        int l=value.length();
        StringBuilder storage=new StringBuilder((int)(1.03D*(double)l));
        char c;
        for(int k=0;k<l;++k){
            if('"'==(c=value.charAt(k))
            ||'\\'== c
            ||'\n'== c)storage.append('\\');
            storage.append(c=='\n'?'n':c);
        }
        return storage.toString();
    }

    @Override
    public String toString(){
        return toString(1);
    }
    public String toString(int nextLines){
        if(nextLines<0||8<nextLines)throw new IllegalArgumentException();
        String nextLine="\";";
        while(nextLines>0){
            nextLine+='\n';
            --nextLines;
        }
        int[]ints=new int[2];
        map_name_value.forEach(
            (n,v)->{
                ints[0]+=n.length();
                ints[0]+=v.length();
            }
        );
        int l=map_name_value.size();
        StringBuilder str=new StringBuilder(
            ints[0]+l*(2+nextLine.length())+
            (int)(1.03d*(double)ints[1])
        );
        String n;
        while(nextLines<l){
            str.append(n=map_index_name.get(Cache.Integer_valueOf(nextLines++)));
            str.append("=\"");
            str.append(memoryTOstorage(map_name_value.get(n)));
            str.append(nextLine);
        }
        return str.toString();
    }
    public byte[] toBytes (){
        return toString(0).getBytes(StandardCharsets.UTF_8);
    }

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
        int begin_name;
        String name;
        StringBuilder value;
        do{ while(' ' ==(c=content.charAt(i))
                ||'\n'== c)++i;
            if(
                !(
                    Character.isAlphabetic(c=content.charAt(begin_name=i))
                    ||'_'==c||'$'==c
                )
            )throw new IllegalArgumentException();
            while(' ' !=(c=content.charAt(++i))
                &&'=' != c
                &&'\n'!= c)
                if(
                    !(
                        Character.isAlphabetic(c)
                        ||'_'==c||'$'==c
                        ||'0'==c||'1'==c||'2'==c||'3'==c||'4'==c
                        ||'5'==c||'6'==c||'7'==c||'8'==c||'9'==c
                    )
                )throw new IllegalArgumentException();
            map_index_name.put(Cache.Integer_valueOf(index++),name=content.substring(begin_name,i));
            while('='!=(c=content.charAt(i))){
                if(' '!=c&&'\n'!=c)throw new RuntimeException();
                ++i;
            }
            while('"'!=(c=content.charAt(++i))){
                if(' '!=c&&'\n'!=c)throw new RuntimeException();
            }
            value=new StringBuilder(64);
            while('"'!=(c=content.charAt(++i))){
                value.append(
                    switch(c){
                        case'\n'->throw new RuntimeException();
                        case'\\'->switch(content.charAt(++i)){
                            case'\\'->'\\';
                            case 'n'->'\n';
                            case '"'-> '"';
                            default->throw new RuntimeException();
                        };
                        default ->c;
                    }
                );
            }
            if(map_name_value.put(name,value.toString())!=null)throw new RuntimeException();
            value.trimToSize();
            while(';'!=(c=content.charAt(++i))){
                if(' '!=c&&'\n'!=c)throw new RuntimeException();
            }
        }while(++i<content_l);
    }
    public void setto        (byte[] bytes  ){
        setto(new String(bytes,StandardCharsets.UTF_8));
    }
}