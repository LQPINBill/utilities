import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

final public class VariableCollection implements Cloneable{
    final private ConcurrentHashMap<Integer,String>map_index_name;
    final private ConcurrentHashMap<String ,String>map_name_value;

    public VariableCollection(){
        map_index_name=new ConcurrentHashMap<>();
        map_name_value=new ConcurrentHashMap<>();
    }
    public VariableCollection(int expectedVariableCount){
        map_index_name=new ConcurrentHashMap<>(expectedVariableCount);
        map_name_value=new ConcurrentHashMap<>(expectedVariableCount);
    }

    private static void checkValidityOfVariableName(String variableName){
        //if(variableName==null)throw new NullPointerException();
        int i=variableName.length();
        if(i==0)throw new IllegalArgumentException();
        char c=variableName.charAt(0);
        if( !(  Character.isLetter(c)
                ||'_'==c
                ||'$'==c
            )
        )throw new IllegalArgumentException();
        while(i>1)
            if( !(  Character.isLetterOrDigit(c=variableName.charAt(--i))
                    ||'_'==c
                    ||'$'==c
                )
            )throw new IllegalArgumentException();
    }
    public void add   (String variableName){
        checkValidityOfVariableName(variableName);
        if(map_name_value.putIfAbsent(variableName,"")!=null)throw new RuntimeException();
        map_index_name.put(Cache.Integer_valueOf(map_index_name.size()),variableName);
    }
    public void remove(String variableName){
        checkValidityOfVariableName(variableName);
        if(map_name_value.remove(variableName)==null)throw new RuntimeException();
        int i=map_name_value.size();
        while(!map_index_name.get(Cache.Integer_valueOf(i)).equals(variableName))--i;
        for(int k=i+1,l=map_index_name.size();k<l;k=(i=k)+1)
            map_index_name.put(Cache.Integer_valueOf(i),map_index_name.get(Cache.Integer_valueOf(k)));
        map_index_name.remove(Cache.Integer_valueOf(i));
    }
    public String variableAt(Integer index){
        String n=map_index_name.get(index);
        if(n==null)throw new IndexOutOfBoundsException();
        return n;
    }
    public String variableAt(int     index){
        String n=map_index_name.get(Cache.Integer_valueOf(index));
        if(n==null)throw new IndexOutOfBoundsException();
        return n;
    }
    public String    valueAt(Integer index){
        return map_name_value.get(variableAt(index));
    }
    public String    valueAt(int     index){
        return map_name_value.get(variableAt(index));
    }
    public int     indexOfVariable (String variableName ){
        checkValidityOfVariableName(variableName);
        int i=map_index_name.size();
        while(i>0)
            if(map_index_name.get(Cache.Integer_valueOf(--i)).equals(variableName))
                return i;
        return-1;
    }
    public int     indexOfValue    (String         value){
        int i=map_index_name.size();
        while(i>0)
            if(map_name_value.get(map_index_name.get(Cache.Integer_valueOf(--i))).equals(value))
                return i;
        return-1;
    }
    public boolean containsVariable(String variableName ){
        checkValidityOfVariableName(variableName);
        return map_name_value.containsKey(variableName);
    }
    public boolean containsValue   (String         value){
        return map_name_value.containsValue(value);
    }
    public int variableCount(){
        return map_index_name.size();
    }
    public void    clear    (){
        map_index_name.clear();
        map_name_value.clear();
    }
    public boolean isEmpty  (){
        return map_index_name.isEmpty();
    }

    public void   set(String variableName,String value){
        checkValidityOfVariableName(variableName);
        if(map_name_value.replace(variableName,value)==null)throw new RuntimeException();
    }
    public String get(String variableName             ){
        checkValidityOfVariableName(variableName);
        String v=map_name_value.get(variableName);
        if(v==null)throw new RuntimeException();
        return v;
    }

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
    public boolean equals(VariableCollection y){
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
    @Override
    public boolean equals(Object y){
        return y instanceof VariableCollection vc?equals(vc):false;
    }
    @Override
    public int hashCode(){
        int[]h=new int[1];
        map_index_name.forEach(
            (i,n)->{h[0]+=(i.intValue()+1)*n.hashCode()*map_name_value.get(n).hashCode();}
        );
        return h[0];
    }

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

    private static void checkValidityOfStorage(String storage){
        //if(storage==null)throw new NullPointerException();
        char c;
        int k=0,l=storage.length();
        while(k<l)
            if('\n'==(c=storage.charAt(k++))||
                '"'== c||(
                        '\\'== c
                    &&  '\\'!=(c=storage.charAt(k++))
                    &&   '"'!= c
                    &&   'n'!= c
                )
            )throw new IllegalArgumentException();
    }
    public static String storageTOmemory (String storage){
        char[]value=new char[storage.length()];
        int i_storage=0,
            i_value  =0;
        char c;
        while(i_storage<value.length){
            value[i_value++]=switch(c=storage.charAt(i_storage++)){
                case'\n'->throw new IllegalArgumentException();
                case '"'->throw new IllegalArgumentException();
                case'\\'->switch(storage.charAt(i_storage++)){
                    case'\\'->'\\';
                    case '"'-> '"';
                    case 'n'->'\n';
                    default ->throw new IllegalArgumentException();
                };
                default ->c;
            };
        }
        return new String(value,0,i_value);
    }
    public static String  memoryTOstorage(String value  ){
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
    /*public void setto        (String content){
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
    }*/
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
            )throw new IllegalArgumentException();
            while(Character.isLetterOrDigit(c=content.charAt(i))
                ||'_'==c
                ||'$'==c
            )++i;
            map_index_name.put(Cache.Integer_valueOf(line++),name=content.substring(name_begin,i));
            while( ' '==(c=content.charAt(i))
                ||'\n'== c
            )++i;
            if('='!=c)throw new IllegalArgumentException();
            do++i;while( ' '==(c=content.charAt(i))
                    ||  '\n'== c
            );
            if('"'!=c)throw new IllegalArgumentException();
            value=new StringBuilder(64);
            while('"'!=(c=content.charAt(++i))){
                value.append(
                    switch(c){
                        case'\n'->throw new IllegalArgumentException();
                        case'\\'->switch(content.charAt(++i)){
                            case'\\'->'\\';
                            case '"'-> '"';
                            case 'n'->'\n';
                            default ->throw new IllegalArgumentException();
                        };
                        default ->c;
                    }
                );
            }
            if(map_name_value.putIfAbsent(name,value.toString())!=null)throw new IllegalArgumentException();
            do++i;while( ' '==(c=content.charAt(i))
                    ||  '\n'== c
            );
            if(c!=';')throw new IllegalArgumentException();
        }while(++i<l);
    }
    public void setto        (byte[] bytes  ){
        setto(new String(bytes,StandardCharsets.UTF_8));
    }
}