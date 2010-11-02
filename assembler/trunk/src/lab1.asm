;autor: andrei mihai daniel math-inf 3
;testat tasm, td sub dosbox

;calculati in asm : (a*3+b*b*5)/(a*a+a*b)-a-b
;--------------------------------------------

;extrn	tipar:near

ASSUME cs:_text,ds:data,ss:stiva

data SEGMENT
;a db 126 ;test ok
;b db 127

;a db 25
;b db 3
;rez ffe3 test ok

a db -10
b db 32
;rez ffd3 test ok

c1 db 3
c2 db 5
rez dw ?
data ENDS

stiva SEGMENT stack     ;nu mai e folosita , pun valori in reg de index
  db 512 dup (?)        ;totusi poate trebuie mai incolo
stiva ends

_text SEGMENT public

start:
mov ax, data
mov ds, ax

mov ax, stiva
mov ss, ax

;----
mov al, c2
cbw
mov bx, ax          ;bx = c2 

mov al, b
imul b              ;ax = b*b          

imul bx             ;dx:ax = b*b*c2

mov di, dx
mov si, ax          ;di:si = b*b*c2
;--

mov al, a 
imul c1            
cwd                 ;dx:ax = a*c1

add si, ax
adc di, dx          ;di:si = (a*c1+b*b*c2) corect in 32b, luat in calcul 
                    ;overflow de 16, vezi ciorna
;-----

mov al, a 
imul a 
mov bx ,ax          ;bx = a*a

mov al, a 
imul b              ;ax = a*b 

cwd                 ;cwd innainte de add ca add 8000h,-1 sa nu devina > 0
add ax, bx          
adc dx,0            ;dx:ax = a*a+a*b no loss - check 
;------

mov bx,ax
mov cx,dx

mov dx,di
mov ax,si           ;dx:ax = (a*c1+b*b*c2) 
                    ;cx:bx = a*a+a*b

;nu gasesc nici o metoda simpla de a extinde precizia idiv la 32b / 32b
;todo: ii o metoda unsigned in Art of A 

;renunt la high order bit din cx:bx
;sper ca rezultatu incape in ax 

idiv bx             ;rem in dx
mov bx, ax

;raman in 16b ca deja am aruncat precizie,chiar daca suburile seteaza cf-u 

;ar fi fain un movsx ...
mov al,a
cbw
mov cx, ax          ;cx = a
mov al,b
cbw
mov dx, ax          ;dx = b

sub bx, cx     
sub bx, dx

mov rez, bx 
;mov ax,bx
;call tipar

; finish it
mov ax, 4c00h
int 21h
_text ENDS

END start 
