group entity;

importdef(imports) ::=<<
import <imports> ;

>>

entitydef(package,imports,entitytype,table,
		fnames,fNames,ftypes,
		pknames,pkNames, pktypes,
		cols,pkcols) ::=<<
package <package> ;

<imports:importdef()>

public class <entitytype>{
    <ftypes,fnames:{t,n| private final <t> <n>;
}>

    public <entitytype>(<ftypes, fnames:{t,n|<t> <n>}; separator = ",">){
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

