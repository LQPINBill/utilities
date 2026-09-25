import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

public class VariableCollection{
    final private ConcurrentHashMap<Integer,String>index_to_name=new ConcurrentHashMap<>(); // sequence to value
    final private ConcurrentHashMap<String,String>name_to_value=new ConcurrentHashMap<>(); // name to value
    @SuppressWarnings({"CallToPrintStackTrace","UseSpecificCatch"})
    public VariableCollection(String content){
        //if(content==null)throw new NullPointerException();
        if(content.length()!=0){

            boolean found_nonblank;
            char c;
            int begin=0,content_l=content.length();
            do found_nonblank=' ' !=(c=content.charAt(begin))
                            &&'\n'!= c;
            while((!found_nonblank)&&++begin<content_l);

            if(found_nonblank){

                begin=0;
                while(' ' ==(c=content.charAt(begin))
                    ||'\n'== c)++begin;
                int end=content.length()-1;
                while(' ' ==(c=content.charAt(end  ))
                    ||'\n'== c)--end  ;
                content_l=(content=content.substring(begin,end+1)).length();

                String name,value;
                end=0;
                int i=0;
                try{do{ while(' ' ==(c=content.charAt(end))
                            ||'\n'== c)++end;

                        begin=end++;
                        while(' ' !=(c=content.charAt(end))
                            &&'=' != c
                            &&'\n'!= c)++end;
                        /*begin=end;
                        do++end;while(' ' !=(c=content.charAt(end))
                                    &&'=' != c
                                    &&'\n'!= c);*/

                        name=content.substring(begin,end);

                        /*
                         * might be redundant, refer to the following table:
                         *
                         * assignment symbol| this loop | effect
                         *    not missing   |uncommented|explain and remember content   correctly
                         *        missing   |uncommented|explain and remember content incorrectly due to strange parsing, or index out of bound exception
                         *    not missing   |  commented|explain and remember content   correctly
                         *        missing   |  commented|explain and remember content   correctly
                         */
                        //while('='!=content.charAt(end))++end;

                        do++end;while(content.charAt(end)!='"');

                        value="";
                        for(++end;(c=content.charAt(end))!='"';++end)value+=c=='\\'?content.charAt(++end):c;

                        /*
                         * if not null, name must have been assigned before this 
                         * assignment, which implies that content contains two 
                         * sentences that assigns a value to the variable with 
                         * this name, this is invalid.
                         */
                        if(name_to_value.put(name,value)!=null)throw new RuntimeException();
                        index_to_name.put(i++,name);

                        do++end;while(content.charAt(end)!=';');
                    }while(++end<content_l);
                }catch(Exception e){
                    e.printStackTrace();
                    throw e;
                }
            }
        }
    }

    /**
     * @param name variable name
     * @return the value of variable with name<code>name</code>
     * @throws NullPointerException if<code>name</code>is null
     * @throws RuntimeException if this collection of variables does not contain a variable with name<code>name</code>
     */
    public String getValueOf(String name){
        String value=name_to_value.get(name);
        if(value==null)throw new RuntimeException();
        return value;
    }
    /**
     * @param name  variable name
     * @param value variable's new value
     * @throws NullPointerException if<code>name</code>or<code>value</code>is null
     * @throws RuntimeException if this collection of variables does not contain a variable with name<code>name</code>
     */
    public void   setValueOf(String name,String value){
        if(name_to_value.put(name,value)==null)throw new RuntimeException();
    }

    @SuppressWarnings("CollectionsToArray")
    public String[]          toNameValue_StringArray     (){
        ArrayList<String>NameValue=toNameValue_StringArrayList();
        return NameValue.toArray(new String[NameValue.size()]);
    }
    public  ArrayList<String>toNameValue_StringArrayList (){
        ArrayList<String>NameValue=new ArrayList<>(name_to_value.size()*2);
        index_to_name.forEach(
            (i,name)->{
                NameValue.add(name);
                NameValue.add(name_to_value.get(name));
            }
        );
        return NameValue;
    }
    public LinkedList<String>toNameValue_StringLinkedList(){
        LinkedList<String>NameValue=new LinkedList<>();
        index_to_name.forEach(
            (i,name)->{
                NameValue.add(name);
                NameValue.add(name_to_value.get(name));
            }
        );
        return NameValue;
    }

    public static String memory_to_storage(String value){
        String storage="";
        char c;
        for(int k=0,l=value.length();k<l;++k)storage+=
            (c=value.charAt(k))=='\\'?"\\\\":
             c                 =='"' ?"\\\"":c;
        return storage;
    }
    public static String memory_to_storage(char[] value){
        String storage="";
        for(char c:value)storage+=
            c=='\\'?"\\\\":
            c=='"' ?"\\\"":c;
        return storage;
    }
    public static String storage_to_memory(String storage){
        char c;
        int str_l=0;
        char[]str=new char[storage.length()];
        for(int k=0;k<str.length;++k)str[str_l++]=(c=storage.charAt(k))=='\\'?storage.charAt(++k):c;
        return new String(str,0,str_l);
    }
    public static String storage_to_memory(char[] storage){
        char c;
        int str_l=0;
        char[]str=new char[storage.length];
        for(int k=0;k<storage.length;++k)str[str_l++]=(c=storage[k])=='\\'?storage[++k]:c;
        return new String(str,0,str_l);
    }

    @Override public String toString(){
        String str="";
        ArrayList<String>NameValue=toNameValue_StringArrayList();
        for(int k=0,l=NameValue.size();k<l;)
            str+=NameValue.get(k++)+"=\""+memory_to_storage(NameValue.get(k++))+"\";\n";
        return str;
    }
}