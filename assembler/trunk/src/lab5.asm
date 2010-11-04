;autor: andrei mihai daniel math-inf 3
;testat tasm, td sub dosbox

;un subprogram implementat intr-un modul separat. 
;7. Se dau trei siruri de caractere. Sa se afiseze cel mai lung prefix comun 
;pentru fiecare din cele trei perechi de cate doua siruri ce se pot forma. 


ASSUME cs:_text, ds:_data, ss:_stack

;extrn  tipar:near
;extrn  mul32_16:near

_data SEGMENT public      
                		;deocamdata presupun stirnguri de aceasi lungime
      					;daca nu-s atunci len tre sa fie lungimea cea mai mica	
  s1 db 'gatacacagggt'
  len equ $-s1			;cat e de aici pana la inceputu lu s1 == lungimea lui s1, compile time
  s2 db 'gatacacggact'
  s3 db 'gatacattttgg'
  
_data ENDS

_stack SEGMENT stack public
_stack ends

_text SEGMENT public

longest_prefix:
    ;in cx lungimea sirurilor
    ;daca nu-s la fel de lungi sirurile tre pasat lungimea mai mica 
    ;in es:di, ds:si sirurile
    ;intorace in cx lungimea celui mai lung prefix
     
    pushf
    push ax
    mov ax, cx    	   
    
    cld
    repe cmpsb         ;compara stringurile, se opreste la cx 0 sau zf set
                       ;deci dupa primu mismatch 
    jne mismatch        
    jmp logest_prefix_end
     
  mismatch:
    inc cx              ;atunci ultimu cmpsb o ajuns pe caracteru diferit
    				    ;deci ultima decrementare a lui cx nu reprezinta un caracter egal
  logest_prefix_end:
    sub cx,ax        
    neg cx
    
    pop ax
    popf
	ret

start:
    mov ax, _data
    mov ds, ax
    mov es, ax 
    
    mov ax, _stack
    mov ss, ax

;----

	lea si, s1
	lea di, s2    
	mov cx, len
	call longest_prefix
	;TODO: call min (cx, len(s3)), presupunand stringuri de acelasi len  minu ii cx
	;      pune in cx minimul
	lea si, s1		
	lea di, s3
	call longest_prefix

	lea si,s1
	add si ,cx		
	mov byte ptr [si], '$'
	
	mov dx, si
	mov ah, 09h
    int 21h
	
	jmp finish
	
finish:
    mov ax, 4c00h
    int 21h
    
	_text ENDS

END start 
