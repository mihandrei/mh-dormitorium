;autor: andrei mihai daniel math-inf 3
;testat tasm, td sub dosbox

;Dându-se 4 octeti, sa se obtina în AX suma numerelor întregi cuprinse 
;între bitii 4-6 ai celor 4 octeti.
;----------------------------------

ASSUME cs:_text,ds:data

data SEGMENT
octeti db 11110111b, 11100111b, 11110011b, 11101011b
octeti_end db 0
msk db 00011100b
data ENDS

_text SEGMENT public

start:
mov ax, data
mov ds, ax

;----
xor ax,ax
xor bx,bx

masked_add:
    mov dl, [bx]              ;reg addresing mode, ds e implicit 

    and dl,msk
    shr dl, 2
    xor dh, dh
    add ax,dx

    inc bx                    ;siru-i de bytes , de era dw at inc inc
    cmp bx,offset octeti_end  ;am ajuns la adresa de dupa sir
    jnz masked_add

; finish it
mov ax, 4c00h
int 21h
_text ENDS

END start 
