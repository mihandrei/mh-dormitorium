group entity;

importdef(imports) ::=<<
import <imports> ;

>>

entitydef(package,imports,class,ftypes,fnames,fNames) ::=<<
package <package> ;

<imports:importdef()>

public class <class>{
    <ftypes,fnames:{t,n| private final <t> <n>;
}>

    public <class>(<ftypes, fnames:{t,n|<t> <n>}; separator = ",">){
        <fnames:{n|this.<n> = <n>;
}>
    }

    <ftypes,fnames,fNames:{t,n,N| <getterdef(type=t,name=n,Name=N)>}>
        
}
>>

getterdef(type,name,Name) ::=<<
public <type> get<Name>() {
    return this.<name> ;
}

>>

