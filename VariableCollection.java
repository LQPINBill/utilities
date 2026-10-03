import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

final public class VariableCollection implements Cloneable{

    /**
     * construct a bijection from indexs to variable's name, and a mapping from 
     * variable's name to variable's value
     */

    /**
     * designates index for each variable (name)
     */
    final private ConcurrentHashMap<Integer,String>map_index_name;
    /**
     * designates value for each variable (name)
     */
    final private ConcurrentHashMap<String ,String>map_name_value;

    /**
     * constructors of empty VC
     */

    /**
     * create a new VC with initial expected variable count (capacity) of 16
     */
    public VariableCollection(){
        map_index_name=new ConcurrentHashMap<>();
        map_name_value=new ConcurrentHashMap<>();
    }
    /**
     * create a new VC with initial expected variable count (capacity) of{@code
     * expectedVariableCount}
     * @param expectedVariableCount expected variable count
     */
    public VariableCollection(int expectedVariableCount){
        map_index_name=new ConcurrentHashMap<>(expectedVariableCount);
        map_name_value=new ConcurrentHashMap<>(expectedVariableCount);
    }

    /**
     * utilities for operation on a VC's structure, these methods are rarely 
     * used in actual database or server, because the collection of VCs should 
     * have all its VCs the same structure, e.g. a variable collection roughly 
     * represents an instance with only String type fields (variables), if two 
     * VCs have a different structure, they should be considered instances of 
     * different classes, so it may be inappropriate to modify a VC's structure. 
     * therefore may also be inappropritate to create empty VCs because they can 
     * not store information, variables must be added to them to store 
     * information, but doing so is considered inappropriate
     */

    /**
     * does nothing if{@code variableName}is a valid name, otherwise throw 
     * Throwables
     * 
     * a String is a valid variable name iff it is not{@code null} and is not 
     * the empty String{@code ""}, and contains only the following chars: 
     * 
     *  a to z, A to Z (letters)
     * 
     *  0 to 9 (digits)
     * 
     *  _ and $
     * 
     * and begins with a non digit char
     * @param variableName name to check
     * @throws NullPointerException iff{@code variableName}is{@code null}
     * @throws IllegalArgumentException iff{@code variableName}is not valid
     */
    private static void checkValidityOfVariableName(String variableName){
        //if(variableName==null)throw new NullPointerException();
        int i=variableName.length();
        if(i==0)throw new IllegalArgumentException("invalid variable name, variable name can not be the empty String \"\"\nvariable name: "+variableName+'\n');
        char c=variableName.charAt(0);
        if( !(  Character.isLetter(c)
                ||'_'==c
                ||'$'==c
            )
        )throw new IllegalArgumentException("invalid variable name, variable name must begin with a letter, digit (a to z, A to Z, 0 to 9), '_' or '$'\nvariable name: "+variableName+'\n');
        while(i>1)
            if( !(  Character.isLetterOrDigit(c=variableName.charAt(--i))
                    ||'_'==c
                    ||'$'==c
                )
            )throw new IllegalArgumentException("invalid variable name, variable name must contain only letters, digits (a to z, A to Z, 0 to 9), '_' or '$'\nvariable name: "+variableName+'\n');
    }
    /**
     * declare a new variable at the tail of this VC
     * @param variableName variable's name
     * @throws RuntimeException iff this VC already declared a variable with 
     * name{@code variableName}
     * @throws NullPointerException iff{@code variableName}is{@code null}
     * @throws IllegalArgumentException iff{@code variableName}is not valid
     */
    public void add   (String variableName){
        checkValidityOfVariableName(variableName);
        if(map_name_value.putIfAbsent(variableName,"")!=null)throw new RuntimeException("repeated declaration: this VC already declared a variable with name: "+variableName);
        map_index_name.put(Cache.Integer_valueOf(map_index_name.size()),variableName);
    }
    /**
     * remove a variable from this VC
     * @param variableName variable's name
     * @throws RuntimeException iff this VC does not contain a variable with 
     * name{@code variableName}
     * @throws NullPointerException iff{@code variableName}is{@code null}
     * @throws IllegalArgumentException iff{@code variableName}is not valid
     */
    public void remove(String variableName){
        checkValidityOfVariableName(variableName);
        if(map_name_value.remove(variableName)==null)throw new RuntimeException("this VC does not contain a variable with name: "+variableName);
        int i=map_name_value.size();
        while(!map_index_name.get(Cache.Integer_valueOf(i)).equals(variableName))--i;
        for(int k=i+1,l=map_index_name.size();k<l;k=(i=k)+1)
            map_index_name.put(Cache.Integer_valueOf(i),map_index_name.get(Cache.Integer_valueOf(k)));
        map_index_name.remove(Cache.Integer_valueOf(i));
    }
    /**
     * @param index the index of variable in this VC
     * @return the name of the variable in this VC with index{@code index}
     * @throws IndexOutOfBoundsException iff this VC does not contain a variable 
     * with index{@code index}
     */
    public String variableAt(Integer index){
        String n=map_index_name.get(index);
        if(n==null)throw new IndexOutOfBoundsException("index "+index.intValue()+" not in [0,"+map_index_name.size()+')');
        return n;
    }
    /**
     * @param index the index of variable in this VC
     * @return the name of the variable in this VC with index{@code index}
     * @throws IndexOutOfBoundsException iff this VC does not contain a variable 
     * with index{@code index}
     */
    public String variableAt(int     index){
        String n=map_index_name.get(Cache.Integer_valueOf(index));
        if(n==null)throw new IndexOutOfBoundsException("index "+index+" not in [0,"+map_index_name.size()+')');
        return n;
    }
    /**
     * @param index the index of variable in this VC
     * @return the value of the variable in this VC with index{@code index}
     * @throws IndexOutOfBoundsException iff this VC does not contain a variable 
     * with index{@code index}
     */
    public String    valueAt(Integer index){
        return map_name_value.get(variableAt(index));
    }
    /**
     * @param index the index of variable in this VC
     * @return the value of the variable in this VC with index{@code index}
     * @throws IndexOutOfBoundsException iff this VC does not contain a variable 
     * with index{@code index}
     */
    public String    valueAt(int     index){
        return map_name_value.get(variableAt(index));
    }
    /**
     * @param variableName variable's name to find
     * @return{@code i}s.t.{@code variableAt(i).equals(variableName)}, or
     * {@code -1}if no such{@code i}
     * @throws NullPointerException iff{@code variableName}is{@code null}
     * @throws IllegalArgumentException iff{@code variableName}is not valid
     */
    public int     indexOfVariable (String variableName ){
        checkValidityOfVariableName(variableName);
        int i=map_index_name.size();
        while(i>0)
            if(map_index_name.get(Cache.Integer_valueOf(--i)).equals(variableName))
                return i;
        return-1;
    }
    /**
     * @param variableName variable's value to find
     * @return{@code i}s.t.{@code valueAt(i).equals(value)}, or{@code -1}if no 
     * such{@code i}
     */
    public int     indexOfValue    (String         value){
        int i=map_index_name.size();
        while(i>0)
            if(map_name_value.get(map_index_name.get(Cache.Integer_valueOf(--i))).equals(value))
                return i;
        return-1;
    }
    /**
     * @param variableName variable's name to find
     * @return{@code indexOfVariable(variableName)!=-1}
     * @throws NullPointerException iff{@code variableName}is{@code null}
     * @throws IllegalArgumentException iff{@code variableName}is not valid
     */
    public boolean containsVariable(String variableName ){
        checkValidityOfVariableName(variableName);
        return map_name_value.containsKey(variableName);
    }
    /**
     * @param value variable's value to find
     * @return{@code indexOfValue(value)!=-1}
     */
    public boolean containsValue   (String         value){
        return map_name_value.containsValue(value);
    }
    /**
     * @return the number of variables contained in this VC
     */
    public int variableCount(){
        return map_index_name.size();
    }
    /**
     * @return{@code variableCount()==0}
     */
    public boolean isEmpty  (){
        return map_index_name.isEmpty();
    }
    /**
     * remove all variables in this VC
     * 
     * after call,{@code isEmpty()}is{@code true}
     */
    public void    clear    (){
        map_index_name.clear();
        map_name_value.clear();
    }

    /**
     * most frequently used methods in actual database and server
     */

    /**
     * assign value to a variable in this VC
     * @param variableName variable's name
     * @param value value to assign
     * @throws RuntimeException iff this VC does not contain a variable with 
     * name{@code variableName}
     * @throws NullPointerException iff{@code variableName}is{@code null}
     * @throws IllegalArgumentException iff{@code variableName}is not valid
     */
    public void   set(String variableName,String value){
        checkValidityOfVariableName(variableName);
        if(map_name_value.replace(variableName,value)==null)throw new RuntimeException("this VC does not contain a variable with name: "+variableName);
    }
    /**
     * get the value of a variable in this VC
     * @param variableName variable's name
     * @return value of the variable with name{@code variableName} in this VC
     * @throws RuntimeException iff this VC does not contain a variable with 
     * name{@code variableName}
     * @throws NullPointerException iff{@code variableName}is{@code null}
     * @throws IllegalArgumentException iff{@code variableName}is not valid
     */
    public String get(String variableName             ){
        checkValidityOfVariableName(variableName);
        String v=map_name_value.get(variableName);
        if(v==null)throw new RuntimeException("this VC does not contain a variable with name: "+variableName);
        return v;
    }

    /**
     * methods defining the equivalence relations of VCs
     */

    /**
     * two VCs are structurally equal iff they satisfy all of the followings:
     * 
     * both are not{@code null}
     * 
     * {@code variableCount()}are equal
     * 
     * for every index{@code i}from{@code 0}to{@code variableCount()-1}, their 
     * {@code i}th variable have the same name
     * 
     * @param y the VC to compare
     * @return{@code true}iff this VC and y have the same structure
     */
    public boolean structurallyEqual (VariableCollection y){
        if(y==null)return false;
        Integer I       =Cache.Integer_valueOf(map_name_value.size()-1);
        if(I.intValue()!=                    y.map_name_value.size()-1)return false;
        while(I.intValue()>=0){
            if(!map_index_name.get(I).equals(y.map_index_name.get(I)))return false;
            I=Cache.Integer_valueOf(I.intValue()-1);
        }
        return true;
    }
    /**
     * two VCs are equal iff they are structurally equal, and for every index
     * {@code i}from{@code 0}to{@code variableCount()-1}, their {@code i}th 
     * variable have the same value
     * 
     * @param y the VC to compare
     * @return{@code true}iff this VC and y have the same content
     */
    public boolean             equals(VariableCollection y){
        if(y==null)return false;
        Integer I       =Cache.Integer_valueOf(map_name_value.size()-1);
        if(I.intValue()!=                    y.map_name_value.size()-1)return false;
        String name  ;
        String name_y;
        while(I.intValue()>=0){
            if( !(  (name=map_index_name.get(I))  .equals(name_y=y.map_index_name.get(I))
                    &&    map_name_value.get(name).equals(       y.map_name_value.get(name_y))
                )
            )return false;
            I=Cache.Integer_valueOf(I.intValue()-1);
        }
        return true;
    }
    /**
     * a VC equals to an Object, iff the latter is not{@code null}and is a VC, 
     * and both have the same content
     * @param y the Object to compare
     * @return{@code true}iff y is not{@code null}and is a VC, and have the same 
     * content as this VC
     */
    @Override
    public boolean             equals(Object             y){
        return y instanceof VariableCollection vc?equals(vc):false;
    }
    /**
     * @return the sum of
     * {@code (i+1)*variableAt(i).hashCode()*valueAt(i).hashCode}
     */
    @Override public int hashCode(){
        int[]h=new int[1];
        map_index_name.forEach(
            (i,n)->{h[0]+=(i.intValue()+1)*n.hashCode()*map_name_value.get(n).hashCode();}
        );
        return h[0];
    }

    /**
     * methods whose meaning rely on the definition of equivalence relations of 
     * VCs
     */

    /**
     * copy the content of{@code vc}to a new empty VC
     * 
     * {@code new VariableCollection(vc).equals(vc)}is always{@code true}
     * 
     * if{@code vc}is the VC to be constructed, the creation of new VC can be 
     * replaced by{@code new VariableCollection}
     * @param vc the VC to be copied
     */
    public VariableCollection(VariableCollection vc){
        if(vc==this){
            map_index_name=new ConcurrentHashMap<>();
            map_name_value=new ConcurrentHashMap<>();
        }else{
            int l=vc.map_index_name.size();
            map_index_name=new ConcurrentHashMap<>(l);
            map_name_value=new ConcurrentHashMap<>(l);
        }

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
    /**
     * copy and set the content of{@code vc}to this VC
     * 
     * after call,{@code equals(vc)}is{@code true}
     * 
     * if{@code vc}is this VC, this method does nothing
     * @param vc the VC to be copied
     */
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
    /**
     * @return a new VC{@code vc}s.t.{@code equals(clone())}is{@code true}
     */
    @Override public VariableCollection clone(){
        return new VariableCollection(this);
    }

    /**
     * utilities for IO on conversions
     */

    //
    private static void checkValidityOfStorage(String storage){
        //if(storage==null)throw new NullPointerException();
        int l=storage.length(),k=0;
        while(k<l)
            switch(storage.charAt(k++)){
                case'\n'->throw new IllegalArgumentException();
                case '"'->throw new IllegalArgumentException();
                case'\\'->{
                    if(k==l)throw new IllegalArgumentException();
                    switch(storage.charAt(k++)){
                        case'\\'->{}
                        case '"'->{}
                        case 'n'->{}
                        default ->throw new IllegalArgumentException();
                    }
                }
            }
    }
    //
    private static String storageTOmemory (String storage){
        char[]value=new char[storage.length()];
        int i_storage=0,
            i_value  =0;
        char c;
        while(i_storage<value.length)
            switch(c=storage.charAt(i_storage++)){
                case'\n'->throw new IllegalArgumentException();
                case '"'->throw new IllegalArgumentException();
                case'\\'->{
                    if(i_storage==value.length)throw new IllegalArgumentException();
                    value[i_value++]=switch(storage.charAt(i_storage++)){
                        case'\\'->'\\';
                        case '"'-> '"';
                        case 'n'->'\n';
                        default ->throw new IllegalArgumentException();
                    };
                }
                default ->value[i_value++]=c;
            }
        return new String(value,0,i_value);
    }
    private static String  memoryTOstorage(String value  ){
        int l=value.length();
        StringBuilder storage=new StringBuilder((int)(1.03d*(double)l));
        char c;
        for(int k=0;k<l;++k)
            if('\n'==(c=value.charAt(k)))
                storage.append("\\n");
            else{
                if('"'==c||'\\'==c)
                    storage.append('\\');
                storage.append(c);
            }
        return storage.toString();
    }

    /**
     * @param nextLines the number of lines between sentences
     * @return a String representation of this VC, with{@code nextLines}lines 
     * between sentences
     * @throws IllegalArgumentException iff{@code nextLines<0}
     */
    private String content(int nextLines){
        if(nextLines<0)throw new IllegalArgumentException("number of lines between sentences can not be negative");
        String nextLine="\";";
        while(nextLines>0){
            nextLine+='\n';
            --nextLines;
        }
        int[]ls=new int[2];
        map_name_value.forEach(
            (n,v)->{
                ls[0]+=n.length();
                ls[1]+=v.length();
            }
        );
        int l=map_index_name.size();
        StringBuilder str=new StringBuilder(
            ls[0]+l*(2+nextLine.length())+
            (int)(1.03d*(double)ls[1])
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

    /**
     * @return a minimal String representation of this VC
     */
    public String content(){
        return content(0);
    }
    /**
     * @return the encoding with UTF-8 of the minimal String representation of 
     * this VC
     */
    public byte[] bytes  (){
        return content(0).getBytes(StandardCharsets.UTF_8);
    }
    /**
     * create a new VC with content{@code content}
     * @param content the content of new VC
     */
    public VariableCollection(String content){
        map_index_name=new ConcurrentHashMap<>();
        map_name_value=new ConcurrentHashMap<>();
        setto(content);
    }
    /**
     * create a new VC with content{@code bytes}
     * @param bytes the content of new VC
     */
    public VariableCollection(byte[] bytes  ){
        map_index_name=new ConcurrentHashMap<>();
        map_name_value=new ConcurrentHashMap<>();
        setto(new String(bytes,StandardCharsets.UTF_8));
    }
    //
    /**
     * set the content of this VC to{@code content}
     * @param content the content to set
     */
    public void setto        (String content){
        //if(content==null)throw new NullPointerException();
        int l=content.length();
        int i=l;
        char c;
        boolean allEmpty=true;
        while(allEmpty&&i>0)
            allEmpty=
                   ' '==(c=content.charAt(--i))
                ||'\n'== c;
        map_index_name.clear();
        map_name_value.clear();
        if(allEmpty)return;
        if(++i<l)content=content.substring(0,l=i);
        int name_begin,line=i=0;
        String name;
        StringBuilder value;
        do{ while( ' '==(c=content.charAt(i))
                ||'\n'== c)++i;
            name_begin=i++;
            if((!Character.isLetter(c))
                &&'_'!=c
                &&'$'!=c
            )throw new IllegalArgumentException("invalid variable name, variable name must begin with a letter, digit (a to z, A to Z, 0 to 9), '_' or '$'\nunread content starting from this sentence:\n"+content.substring(name_begin));
            while(Character.isLetterOrDigit(c=content.charAt(i))
                ||'_'==c
                ||'$'==c
            )++i;
            map_index_name.put(Cache.Integer_valueOf(line++),name=content.substring(name_begin,i));
            while( ' '==(c=content.charAt(i))
                ||'\n'== c
            )++i;
            if('='!=c)throw new IllegalArgumentException("sentence incorrect grammar, missing assignment operator '='\nunread content starting from this sentence:\n"+content.substring(name_begin));
            do++i;while( ' '==(c=content.charAt(i))
                    ||  '\n'== c
            );
            if('"'!=c)throw new IllegalArgumentException("sentence incorrect grammar, value not wrapped in '\"' symbol\nunread content starting from this sentence:\n"+content.substring(name_begin));
            value=new StringBuilder(64);
            while('"'!=(c=content.charAt(++i))){
                switch(c){
                    case'\n'->throw new IllegalArgumentException();
                    case'\\'->{
                        if(++i==l)throw new IllegalArgumentException();
                        value.append(
                            switch(content.charAt(i)){
                                case'\\'->'\\';
                                case '"'-> '"';
                                case 'n'->'\n';
                                default ->throw new IllegalArgumentException();
                            }
                        );
                    }
                    default ->value.append(c);
                }
            }
            if(map_name_value.putIfAbsent(name,value.toString())!=null)throw new IllegalArgumentException("repeated declaration: this VC already declared a variable with name: "+name+"\nfail to create VC with content:\n"+content);
            do++i;while( ' '==(c=content.charAt(i))
                    ||  '\n'== c
            );
            if(c!=';')throw new IllegalArgumentException("sentence incorrect grammar, sentence does not end with ';'\nunread content starting from this sentence:\n"+content.substring(name_begin));
        }while(++i<l);
    }
    /**
     * set the content of this VC to{@code bytes}
     * @param bytes the content to set
     */
    public void setto        (byte[] bytes  ){
        setto(new String(bytes,StandardCharsets.UTF_8));
    }

    /**
     * @return a String representation of this VC, with one line between 
     * sentences
     */
    @Override public String toString(){
        return content(1);
    }
}