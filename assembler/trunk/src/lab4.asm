;autor: andrei mihai daniel math-inf 3
;testat tasm, td sub dosbox

;7. Să se afişeze spatiul ocupat pe o dischetă.

;tlink /v lab4.obj tipar.obj arithm.obj

ASSUME cs:_text, ds:_data, ss:_stack

extrn  tipar:near
extrn  mul32_16:near

_data SEGMENT public                    ;public il lipeste de alte _data
  invalidDriveMsg db 'Drive invalid$'
  freemsg  db ' bytes free out of $'

  drive db 'Z'

  free_clusters dw (?)
  all_clusters dw (?)
  
_data ENDS

_stack SEGMENT stack public
_stack ends

_text SEGMENT public

start:
    mov ax, _data
    mov ds, ax
    
    mov ax, _stack
    mov ss, ax

    ;----
    ;int 21 fn 36h disk space
    ;dx:ax 2**32 4 gb - pt dischete ok pt altele apiu asta nu cred ca mai mere         
    
    mov ah,36h           
    mov dl, drive
    sub dl, 'A' - 1      ;drive-'A'+1  1-A, 2-B ...
    int 21h

    cmp ax, 0FFFFh
    je invalid_drive

    mov all_clusters, dx            
    mov free_clusters, bx

    mul cx             ;dx:ax bytes/cluster , bx free clusters

    mov si,ax          ;di:si by/clust
    mov di,dx
    
    mov cx, bx
    call mul32_16      ;cx:dx:ax free bytes

    call printcxdxax

    lea dx, freemsg
    call print

    mov ax, si
    mov dx, di
    mov cx, all_clusters
    call mul32_16     

    call printcxdxax    
   
    jmp finish

printcxdxax:
    ;TODO implementeaza metoda asta
    ;nu-i simplu daca nu arunc high bits
    ;ca div pe multiword nu-i simplu
    ;implementarea de mai jos printeaza in b10 fiecare byte din reprez
    ;
    xchg ax,cx
    call tipar
    mov ax,dx
    call tipar
    mov ax,cx
    call tipar
    ret

print:    
    ;use lea dx msg
	mov ah, 09h
    int 21h
    ret
    
invalid_drive:
    mov ah,0ah
    lea dx ,invalidDriveMsg
    call print
    jmp finish
    
finish:
    mov ax, 4c00h
    int 21h
    
_text ENDS

END start 


