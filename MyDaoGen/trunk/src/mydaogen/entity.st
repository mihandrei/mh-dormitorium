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

    public <class>(<first(ftypes),first(fnames):{t,n|<t> <n>}><rest(ftypes),rest(fnames):{t,n|, <t> <n>}> ){
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

setterdef(type,name,Name) ::=<<
public void set<Name>(<type> <name>) {
    this.<name> = <name>;
}

>>