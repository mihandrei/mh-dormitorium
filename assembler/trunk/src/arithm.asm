;mihai andrei

;rutine aritmetice 
;adptate dupa arto of assembly si gcc -O0 la.c ; objdump -d -Mintel a.out

public add32, mul32_16

assume cs:_text, ds:_data, ss:_stack

_data segment public
_data ends

_stack SEGMENT stack public
  db 512 dup (?)        
_stack ends

_text segment public

add32:
    ;dx:ax = dx:ax + bx:cx
    add    ax,cx
    adc    dx,bx
    ret

mul32_16:
   ;dx:ax =high,low , c = multipl
   ;cx:dx:ax <-- dx:ax * cx
   ;implementat ca la scoala in baza 2**16
   
   push bx
   push di
   push si
      
   mov bx, dx    ;bx = high
      
   mul cx        ;dx:ax = low*multipl
   mov di, dx
   mov si, ax    ;di:si = low*multipl
   
   mov ax, bx    
   mul cx        ;dx:ax = hi*multipl
   
   add di,ax
   adc dx,0      ;dx:di:si
   
   mov cx,dx
   mov dx,di
   mov ax,si

   pop si   
   pop di
   pop bx

   ret

;FEATUREQ: mul_32_32, imul_32_32

;TODO testat acrobatiile cu bp    
add32_stack:
    ; aduna primele 2 dublucuvinte de pe stiva 
    ;usage
    ;push low1
    ;push high1
    ;push low2
    ;push high2
    ;call add32_stack
    ;pop rez_high
    ;pop rez_low

    ;obs: C se pare ca pune argumentele in stiva, le aceseaza cu [ebp] si 
    ;     intoarce in eax

    ;argumentele is pe stiva, da io vreau sa salvez pe stiva registrii
    ;si apoi sa acesez argumentele inainte de a pop reg salvati
    ;deci imi trebuie sa acesez stiva aleatoriu
    push   bp           ;pt adresarea 'base relative' a stivei
    mov    bp,sp        ;cu mov reg,[bp-4h] acesez bw al 2-lea din stiva
                        ;bp acum indica varful stivei cum era la apelul procedurii    
                        ;cred ca C face ceva asemanator, de verificat
    
    ;salvam registrii ca sa-i putem folosi in procedura
    push ax
	push bx
	push cx	
	push dx
    
   	mov    ax, [bp-02h]        ;low1
    mov    dx, [bp-04h]    ;high1
    mov    cx, [bp-06h]
    mov    bx, [bp-08h]

    add    ax,cx            ;adunarea propriu zisa
    adc    dx,bx
    
    mov [bp-02h], ax
    mov [bp-04h], dx
    
    add sp, 2               ;<=> pop ax , pop ax; scoate low2 high2 din stiva
    ;refacem contextul
	pop dx
	pop cx
	pop bx
	pop ax
	pop bp
	ret

arithm_test_add_stack:
    push 0F1F1h
    push 0DADAh
    push 0AFA4h
    push 0001Fh
    call add32_stack
    ret

arithm_test_mul32_16:       
    mov ax, 0FACEh
    mov bx, 00101h
    mul bx    
    mov cx, 00111h
    
    call mul32_16   ;test ok 10c8123ae
    ret

arithm_test:
    mov ax, _data
    mov ds, ax

    mov ax, _stack
    mov ss, ax

    call arithm_test_mul32_16

    mov ax, 4c00h
    int 21h
    
_text ends

end ;arithm_test
