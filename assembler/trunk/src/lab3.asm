;autor: andrei mihai daniel math-inf 3
;testat tasm, td sub dosbox

;7. Se dã s1 un sir de octeti. Sã se construiascã sirul s2 astfel:
;-dacã s1[0] apartine [00h,0Fh] atunci s1:=s2
;-dacã s1[0] apartine [10h,1Fh] atunci s2 va contine sirul s1 în ordine inversã
;-dacã s1[0] apartine [20h,2Fh] atunci s2 va contine primii (n div 2) octeti din 
;s1 in ordinea lor, iar urmãtorii ((n div 2)+(n mod 2)) octeti în ordine inversã
;-altfel, s2 va contine primii (n div 2) octeti din s1 în ordine inversã, iar 
;restul respectând ordinea lor. 

ASSUME cs:_text, ds:_data, ss:_stack,es:_data

_data SEGMENT public                    ;public il lipeste de alte _data
  ;  s1 db 0Fh, 'tractor' ;cpy
  ;  s1 db 1Eh, 'tractor' ;rev
     s1 db 21h, 'tracto' ;cpyflip
;    s1 db 31h, 'tractor' ;flipcpy
    
    len equ $-s1
    s2 db len dup (?)  
_data ENDS

_stack SEGMENT stack public
_stack ends

_text SEGMENT public

reverse_str:
    ;in cx lungimea sirului sursa
    ;in es:di, ds:si sirurile dest si sursa
    pushf
    push ax
    
    cld
    add di, cx
    push di       ;dupa executia proceduri vreau ca di sa fie dupa destinatiei
                  ;daca nu-l salvez at el decrementeaza pana la di-u initial    

    sub di, 1     ;di pozitionat pe sf destinatiei
    inc cx
 again:    
    dec cx        
    je reverse_str_fin

    lodsb
    dec di        
    mov byte [es:di] , al 

    jmp again

  reverse_str_fin:
    pop di 
    pop ax
    popf
    ret
    
    
start:
    mov ax, _data
    mov ds, ax
    mov es, ax 
    
    mov ax, _stack
    mov ss, ax
;-----
    
    mov si,offset s1
    mov di,offset s2    
    mov cx, len
    
    mov al,s1
           
    sub al,0Fh
    jb copys1
    sub al, 1Fh - 0Fh
    jb reverses1
    sub al, 2Fh - 1Fh 
    jb copyflip
    jmp flipcopy
    
copys1:    
    cld
    rep movsb
    jmp finish
    
reverses1:
    call reverse_str
    jmp finish

copyflip:
    cld
    mov ax, cx    
    sar cx, 1       ;jumate 
    sub ax, cx      ; cealalta jumate :p
    
    rep movsb    ;copie 
    
    mov cx, ax
    call reverse_str

    jmp finish
    
flipcopy:
    cld
    mov ax, cx    
    sar cx, 1       ;jumate 
    sub ax, cx      ; cealalta jumate :p
    
    call reverse_str

    mov cx, ax
    rep movsb    ;copie 

    jmp finish


finish:
    mov ax, 4c00h
    int 21h
    
_text ENDS

END start 

